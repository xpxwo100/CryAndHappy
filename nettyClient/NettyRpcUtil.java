package cn.com.aperfect.auap.external.nettyClient;

import java.io.IOException;
import java.math.BigDecimal;

import cn.com.aperfect.auap.external.exception.AppRunTimeException;
import cn.com.aperfect.auap.external.nettyRpcModel.ParamsData;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcResponse;
import cn.com.aperfect.auap.external.thrift.ErrorMsgException;
import cn.com.aperfect.dto.base.ObjectDto;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
/**
 * netty工具类
 * @author hasee
 *
 */
public class NettyRpcUtil {
	/**
	 * rpc远程调用后端
	 * @param SERVICE_BEAN_NAME SERVICE类名
	 * @param methodName 
	 * @param param
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public  static ObjectDto load(String SERVICE_BEAN_NAME,String methodName,Object param)  {
		if(SERVICE_BEAN_NAME == null || methodName == null ){
			return new ObjectDto();
		}
		ParamsData paramsData = bulidParamsData(SERVICE_BEAN_NAME,methodName,param);
		NettyRpcLoad nettyRpcLoad = NettyRpcProxy.get(NettyRpcLoad.class);
		RpcResponse rpcResponse = (RpcResponse)nettyRpcLoad.load(paramsData);
	
		ObjectDto objectDto = null;
		
		if("".equals(rpcResponse.getError())){
			//对返回数据进行解析
			if(rpcResponse.getResult() !=null){
			    objectDto = (ObjectDto)rpcResponse.getResult();
			}
		}else{
			//失败则抛出异常
			if(rpcResponse.getResult() != null){
				ErrorMsgException mExp = (ErrorMsgException) rpcResponse.getResult();
				throw new AppRunTimeException(mExp.getCode(), mExp.getMsg(),mExp.getArgs());
			}else{
				throw new AppRunTimeException(rpcResponse.getError());
			}
		}
		return objectDto;
	}
	/**
	 * 构建DataWrapper
	 * @param SERVICE_BEAN_NAME
	 * @param methodName
	 * @param byteParamList 
	 * @param inData 
	 * @param listArgs
	 * @return
	 */
	private static ParamsData bulidParamsData(String SERVICE_BEAN_NAME,String methodName, Object inData){
		ParamsData paramsData = new ParamsData();
		paramsData.setBeanName(SERVICE_BEAN_NAME);
		paramsData.setMethodName(methodName);
		paramsData.setParams(inData);
		return paramsData;
	}
	/** 
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节) 
     *  
     * @param bytes 
     * @return 
     */  
    public static String bytes2kb(long bytes) {  
        BigDecimal filesize = new BigDecimal(bytes);  
        BigDecimal megabyte = new BigDecimal(1024 * 1024);  
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)  
                .floatValue();  
        if (returnValue > 1)  
            return (returnValue + "MB");  
        BigDecimal kilobyte = new BigDecimal(1024);  
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)  
                .floatValue();  
        return (returnValue + "KB");  
    }  
}
