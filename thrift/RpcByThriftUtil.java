package cn.com.aperfect.auap.external.thrift;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;

import cn.com.aperfect.auap.external.exception.AppRunTimeException;
import cn.com.aperfect.auap.external.util.serialize.FSTUtil;
import cn.com.aperfect.dto.base.ObjectDto;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class RpcByThriftUtil {
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
	public static ObjectDto load(String SERVICE_BEAN_NAME,String methodName,Object param)  {
		if(SERVICE_BEAN_NAME == null || methodName == null ){
			return new ObjectDto();
		}
		byte[] inData = FSTUtil.encode(param);
		DataWrapper dataWrapper = bulidDataWrapper(SERVICE_BEAN_NAME,methodName,inData);
		inData = null;
		RemoteProxyServiceClient remoteProxyServiceClient = new RemoteProxyServiceClient();
		ObjectDto objectDto = null;
		//客户端连接
		ReSult reSult = remoteProxyServiceClient.load(dataWrapper);
		byte[] value = reSult.getValue();
		if(reSult.isSuccess){
			//对返回数据进行解析
			if(value !=null){
			    objectDto = (ObjectDto) FSTUtil.decodeBuf(value, ObjectDto.class);
			}
		}else{
			//失败则抛出异常
			if(value != null){
				ErrorMsgException mExp=(ErrorMsgException) FSTUtil.decodeBuf(value, ErrorMsgException.class);
				throw new AppRunTimeException(mExp.getCode(), mExp.getMsg(),mExp.getArgs());
			}else{
				throw new AppRunTimeException(reSult.getErrorMessage());
			}
		}
		value = null;
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
	private static DataWrapper bulidDataWrapper(String SERVICE_BEAN_NAME,String methodName, byte[] inData){
		DataWrapper dataWrapper = new DataWrapper();
		dataWrapper.setBeanName(SERVICE_BEAN_NAME);
		dataWrapper.setMethodName(methodName);
		dataWrapper.setParams(ByteBuffer.wrap(inData));
		return dataWrapper;
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
