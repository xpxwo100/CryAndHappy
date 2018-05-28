package cn.com.aperfect.base.thrift;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ObjectUtils;

import cn.com.aperfect.auap.core.helper.base.ObjectHelper;
import cn.com.aperfect.auap.core.service.base.IBaseService;
import cn.com.aperfect.auap.external.asm2.reflect.AsmMethodRefUtil;
import cn.com.aperfect.auap.external.exception.CommandException;
import cn.com.aperfect.auap.external.exception.CustomException;
import cn.com.aperfect.auap.external.exception.ExceptionConstants;
import cn.com.aperfect.auap.external.exception.FunctionalException;
import cn.com.aperfect.auap.external.exception.ServiceException;
import cn.com.aperfect.auap.external.thrift.ErrorMsgException;
import cn.com.aperfect.auap.external.util.CollectionUtil;
import cn.com.aperfect.auap.external.util.SpringContextUtil;
import cn.com.aperfect.auap.external.util.Usual;
import cn.com.aperfect.auap.external.util.serialize.FSTUtil;
import cn.com.aperfect.dto.base.ObjectDto;

/**
 * 后端thrift的具体服务实现 
 * @author hasee
 *
 */
public class RemoteProxyByThriftServiceImpl implements RemoteProxyByThrift.Iface {
	private static final Log logger = LogFactory.getLog(RemoteProxyByThriftServiceImpl.class);
	/**
	 * 解析参数调用指定service类的方法
	 */
	@Override
	public ReSult load(DataWrapper dataWrapper) throws CommandException {
		String methodName = dataWrapper.getMethodName();
		String beanName = dataWrapper.getBeanName();
		ReSult value = null;
		try {
			//参数解析
			byte[] b = dataWrapper.getParams();
			Object map = (Object) FSTUtil.<Object>decode(b);
			Object[] arg = null;
			if(map != null){
				 arg = new Object[]{map};
			}
			ObjectDto objectDto = execute(methodName, beanName, arg);
			value = new ReSult();
			if(objectDto != null){
				byte[] inData = FSTUtil.encodeBuf(objectDto, ObjectDto.class);
				value.setValue(ByteBuffer.wrap(inData));
			}else{
				value.setValue(ByteBuffer.wrap(Usual.mEmptyBytes));
			}
			value.setIsSuccess(true);
		} catch (FunctionalException e) {
			ErrorMsgException err = new ErrorMsgException(e.getCode(), e.getMsg(),  e.getDetailMessage(),  e.getArgs());
			ReSult result = returnExpBean(err);
			return result;
		}catch (CommandException e) {
			ErrorMsgException err = new ErrorMsgException( e.getCode(), e.getMsg(), e.getDetailMessage(), e.getArgs());
			ReSult result = returnExpBean(err);
			return result;
		}catch (Exception e) {
			ErrorMsgException err=new ErrorMsgException(ExceptionConstants.EJB_EXCEPTION_CODE, ExceptionConstants.EJB_EXCEPTION_MSG);
			ReSult result = returnExpBean(err);
			return result;
		}
		return value;
	}
	
	/**
	 * 序列化异常数据对象
	 * @param response
	 * @param mExp
	 */
	private  ReSult returnExpBean(ErrorMsgException mExp){
		ReSult value = new ReSult();
		byte[] mErrBytes=Usual.mEmptyBytes;
		mErrBytes = FSTUtil.encodeBuf(mExp, ErrorMsgException.class);
		value.setIsSuccess(false);
		value.setValue(ByteBuffer.wrap(mErrBytes));
		value.setErrorMessage(mExp.getMsg());
		return value;
	}
	/**
	 * 调用service
	 * @param methodName
	 * @param beanName
	 * @param arg
	 * @return
	 * @throws CommandException
	 */
	private ObjectDto execute(String methodName,String beanName,Object[] arg) throws CommandException {
		ObjectDto objectDto = null;
		try {
			//调用服务
			IBaseService baseService = (IBaseService) SpringContextUtil.getBean(beanName);
			Object obj = asmCall(arg, null, baseService, methodName,beanName);
			// 返回数据
			if (obj != null) {
				ObjectHelper helper = new ObjectHelper();
				objectDto = helper.getDTO(obj);
			}
		} catch (SecurityException e) {
			throw new CommandException(e);
		} catch (IllegalArgumentException e) {
			throw new CommandException(e);
		} catch (CommandException e) {
			throw new CommandException(e.getCode(), e.getMsg(), e.getArgs(), e.getDetailMessage(), e);
		} catch (CustomException e) {
			throw new CommandException(e.getCode(), e.getMessage(), e.getArgs(), "", e);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof CustomException) {
				CustomException ce = (CustomException) e.getTargetException();
				throw new CommandException(ce.getCode(), null, ce.getArgs(), ce);
			} else if (e.getTargetException() instanceof ServiceException) {
				ServiceException e1 = (ServiceException) e.getTargetException();
				throw new CommandException(e1.getCode(), e1.getMsg(),
						e1.getArgs(), e1.getDetailMessage(), e.getTargetException());
			} else if (e.getTargetException() instanceof CommandException) {
				CommandException e1 = (CommandException) e.getTargetException();
				throw new CommandException(e1.getCode(), e1.getMsg(),
						e1.getArgs(), e1.getDetailMessage(), e.getTargetException());
			} else {
				throw new CommandException(e);
			}
		} catch (Exception e) {
			logger.equals(e);
			throw new CommandException(e.getMessage(), e);
		}
		return objectDto;
	}
	
	
	/**
	 * asm字节码调用
	 * 
	 * @param commonCriteria
	 * @param baseService
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private Object asmCall(Object[] args,Class<?>[] argClz,
			IBaseService baseService,String methodName,String beanName) throws Exception {
		// long mBegin=System.currentTimeMillis();

		Object obj = null;
		try {
			// Asm字节码调用
			if (!ObjectUtils.isEmpty(args)) {
				if (ObjectUtils.isEmpty(argClz) ){
					int argsLength = args.length;
					argClz = new Class[argsLength];
					for (int i = 0; i < argsLength; i++) {
						argClz[i] = args[i].getClass(); // 存放参数类型
					}
				} 
			} else {
				args = null;
			}

			obj =AsmMethodRefUtil.invokeAsmFunc
			(
				baseService.getClass(), 
				methodName,
				args, 
				argClz,
				baseService
			);
		} catch (NullPointerException e) {
			logger.error("NullPointerException,BeanName:" + beanName+ ",MethodName:" + methodName
					+ ",Args:" + CollectionUtil.ObjectArray2String(args)
					+ ",ArgsType:" + CollectionUtil.ClassArray2String(argClz)
					, e);
			throw e;
		} catch (Exception e) {
			logger.error("NotDefinedException,BeanName:" + beanName+ ",MethodName:" + methodName
					+ ",Args:" + CollectionUtil.ObjectArray2String(args)
					+ ",ArgsType:" + CollectionUtil.ClassArray2String(argClz)
					, e);
			throw e;
		}
		return obj;
	}
}

