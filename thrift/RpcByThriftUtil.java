package cn.com.aperfect.auap.external.thrift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public static ObjectDto load(String SERVICE_BEAN_NAME,String methodName,Object param) {
		if(SERVICE_BEAN_NAME == null || methodName == null || param == null){
			return new ObjectDto();
		}
		Map<String, List<Byte>> mapPrarm = new HashMap();
		byte[] inData = FSTUtil.encode(param);
		List<Byte> byteParamList = new ArrayList<>();
		for(byte b : inData){
			byteParamList.add(b);
		}
		mapPrarm.put("params", byteParamList);
		DataWrapper dataWrapper = bulidDataWrapper(SERVICE_BEAN_NAME,methodName,mapPrarm);
		RemoteProxyServiceClient remoteProxyServiceClient = new RemoteProxyServiceClient();
		//客户端连接
		ReSult reSult = remoteProxyServiceClient.load(dataWrapper);
		//对返回数据进行解析
		Map<String, List<Byte>> mapRe = reSult.getRelData();
		ObjectDto objectDto = null;
		if(mapRe != null){
			List<Byte> byteList = null;
			if(mapRe.get("value") !=null){
				byteList = (List<Byte>)mapRe.get("value");
			}
			byte[] b = listToByteArr(byteList);
		    objectDto = (ObjectDto) FSTUtil.decodeBuf(b, ObjectDto.class);
		}
		if(objectDto == null){
			return new ObjectDto();
		}
		return objectDto;
	}
	/**
	 * 构建DataWrapper
	 * @param SERVICE_BEAN_NAME
	 * @param methodName
	 * @param listArgs
	 * @return
	 */
	public static DataWrapper bulidDataWrapper(String SERVICE_BEAN_NAME,String methodName,Map<String, List<Byte>> args){
		DataWrapper dataWrapper = new DataWrapper();
		dataWrapper.setBeanName(SERVICE_BEAN_NAME);
		dataWrapper.setMethodName(methodName);
		dataWrapper.setParamsMap(args);
		return dataWrapper;
	}
	/**
	 * List<Byte>转为byte[]
	 * @param byteList
	 * @return
	 */
	public static byte[] listToByteArr(List<Byte> byteList){
		byte[] b = null;
		if(byteList == null || byteList.size() == 0){
			b = new byte[0];
		}else{
			b = new byte[byteList.size()];
			for(int i = 0; i<byteList.size();i++){
				b[i] = byteList.get(i);
			}
		}
		return b;
	}
}
