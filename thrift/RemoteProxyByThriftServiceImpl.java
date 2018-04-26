package cn.com.aperfect.base.thrift;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ObjectUtils;

import cn.com.aperfect.auap.core.helper.base.ObjectHelper;
import cn.com.aperfect.auap.core.service.base.IBaseService;
import cn.com.aperfect.auap.external.asm2.reflect.AsmMethodRefUtil;
import cn.com.aperfect.auap.external.exception.CommandException;
import cn.com.aperfect.auap.external.exception.CustomException;
import cn.com.aperfect.auap.external.exception.ServiceException;
import cn.com.aperfect.auap.external.util.CollectionUtil;
import cn.com.aperfect.auap.external.util.SpringContextUtil;
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
	public ReSult load(DataWrapper dataWrapper)   {
		ReSult value = new ReSult();
		String methodName = dataWrapper.getMethodName();
		String beanName = dataWrapper.getBeanName();
		Map<String, List<Byte>> paramsMap = dataWrapper.getParamsMap();
		List<Byte> paramList = paramsMap.get("params");
		try {
			//参数解析
			byte[] b = new byte[paramList.size()];
			for(int i = 0; i<paramList.size();i++){
				b[i] = paramList.get(i);
			}
			Object map = (Object) FSTUtil.<Object>decode(b);
			Object[] arg = null;
			if(map != null){
				 arg = new Object[]{map};
			}
			//调用服务
			IBaseService baseService = (IBaseService) SpringContextUtil.getBean(beanName);
			Object obj = asmCall(arg, null, baseService, methodName,beanName);
			// 返回数据
			ObjectDto objectDto = null;
			if (obj != null) {
				ObjectHelper helper = new ObjectHelper();
				objectDto = helper.getDTO(obj);
			}
			HashMap<String, List<Byte>> mapValue = new HashMap();
			if(objectDto != null){
				byte[] inData = FSTUtil.encodeBuf(objectDto, ObjectDto.class);
				List<Byte> a = new ArrayList<>();
				for(byte bb : inData){
					a.add(bb);
				}
				mapValue.put("value", a);
			}else{
				List<Byte> a = new ArrayList<>();
				mapValue.put("value", a);
			}
			value.setRelData(mapValue);
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
		return value;
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
			logger.error("NullPointerException,BeanName:" + beanName+ ",MethodName:" + methodName
					+ ",Args:" + CollectionUtil.ObjectArray2String(args)
					+ ",ArgsType:" + CollectionUtil.ClassArray2String(argClz)
					, e);
			throw e;
		}
		return obj;
	}
}

