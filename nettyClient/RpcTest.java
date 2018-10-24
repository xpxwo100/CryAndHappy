package cn.com.aperfect.auap.external.nettyClient;

import java.util.HashMap;

import cn.com.aperfect.auap.external.nettyRpcModel.ParamsData;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcResponse;
import cn.com.aperfect.auap.external.thrift.RpcByThriftUtil;
import cn.com.aperfect.dto.base.ObjectDto;
import cn.com.aperfect.model.pu.PuPurchases;

public class RpcTest {
	 public static void main(String[] args) throws InterruptedException {
		 
		 
		 HashMap<String, Object> map2 = new HashMap<>();
		 for(int i=0;i<10000;i++){
				PuPurchases p = new PuPurchases();
				p.setArtNo("AAa");
				p.setProNo("BBB");
				map2.put(i+"", p);
			}
		 
	/*	 ParamsData paramsData = new ParamsData();
		 paramsData.setBeanName("syncTest");
		 paramsData.setParams(map2);
		 paramsData.setMethodName("test2");
		
		 NettyRpcLoad nettyRpcLoad = NettyRpcProxy.get(NettyRpcLoad.class);
		RpcResponse objectDto = (RpcResponse)nettyRpcLoad.load(paramsData);
		System.out.println(objectDto.getResult().toString());*/
		 long start = System.currentTimeMillis();
		ObjectDto objectDto =  NettyRpcUtil.load("syncTest", "test2", map2);
		long end = System.currentTimeMillis();
		//System.out.println(objectDto.getObj().toString());
		System.out.println("RpcByThrift 花费时间："+(end-start));
		
	 }
}
