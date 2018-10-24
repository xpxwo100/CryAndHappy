package cn.com.aperfect.base.nettyRpcServer;

import java.lang.reflect.InvocationTargetException;

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
import cn.com.aperfect.auap.external.nettyClient.NettyRpcLoad;
import cn.com.aperfect.auap.external.nettyRpcModel.ParamsData;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcResponse;
import cn.com.aperfect.auap.external.thrift.ErrorMsgException;
import cn.com.aperfect.auap.external.util.CollectionUtil;
import cn.com.aperfect.auap.external.util.SpringContextUtil;
import cn.com.aperfect.dto.base.ObjectDto;

public class NettyRpcLoadImpl implements NettyRpcLoad{
	
	private static final Log logger = LogFactory.getLog(NettyRpcLoadImpl.class);
	public NettyRpcLoadImpl() {
	}

	@Override
	public RpcResponse load(ParamsData pojo) {
		String methodName = pojo.getMethodName();
		String beanName = pojo.getBeanName();
		RpcResponse value = null;
		try {
			//参数解析
			Object map = pojo.getParams();
			Object[] arg = null;
			if(map != null){
				 arg = new Object[]{map};
			}
			ObjectDto objectDto = execute(methodName, beanName, arg);
			value = new RpcResponse();
			if(objectDto != null){
				value.setResult(objectDto);
				value.setError("");
				value.setReturnNotNull(true);
			}else{
				value.setResult(null);
				value.setError("");
				value.setReturnNotNull(false);
			}
			
		} catch (FunctionalException e) {
			ErrorMsgException err = new ErrorMsgException(e.getCode(), e.getMsg(),  e.getDetailMessage(),  e.getArgs());
			RpcResponse result = returnExpBean(err);
			return result;
		}catch (CommandException e) {
			ErrorMsgException err = new ErrorMsgException( e.getCode(), e.getMsg(), e.getDetailMessage(), e.getArgs());
			RpcResponse result = returnExpBean(err);
			return result;
		}catch (Exception e) {
			ErrorMsgException err=new ErrorMsgException(ExceptionConstants.EJB_EXCEPTION_CODE, ExceptionConstants.EJB_EXCEPTION_MSG);
			RpcResponse result = returnExpBean(err);
			return result;
		}
		return value;
	}
	
	/**
	 * 序列化异常数据对象
	 * @param response
	 * @param mExp
	 */
	private  RpcResponse returnExpBean(ErrorMsgException mExp){
		RpcResponse value = new RpcResponse();
		value.setResult(mExp);
		value.setError(mExp.getMsg());
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
