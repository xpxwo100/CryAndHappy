package cn.com.aperfect.auap.external.thrift;

import java.util.HashMap;

import org.junit.Test;

import cn.com.aperfect.dto.base.ObjectDto;

import com.google.common.collect.Maps;

public class RpcBythriftTest {
	/**
	 * 测试远程调用框架
	 */
	@Test
	public void Test() {
		//service的类名 必须是继承自BaseService实现IBaseService接口的
		String SERVICE_BEAN_NAME = "syncTest";
		//调用的方法名
		String methodName = "Test";
		long start = System.currentTimeMillis();
		//构建参数 参数类型必须与调用的方法参数类型一致
		String sql = "SELECT top 1 * FROM PuPurchase";
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("sql", sql);
		//调用
		ObjectDto objectDto = RpcByThriftUtil.load(SERVICE_BEAN_NAME, methodName, map);
		System.out.println(objectDto);
		System.out.println(objectDto.getObj().toString());
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}

}
