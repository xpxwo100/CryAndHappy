package cn.com.aperfect.auap.external.thrift;

import java.util.HashMap;

import org.junit.Test;

import cn.com.aperfect.auap.external.exception.AppRunTimeException;
import cn.com.aperfect.dto.base.ObjectDto;
import cn.com.aperfect.model.pu.PuPurchases;

public class RpcBythriftTest {
	/**
	 * 测试远程调用框架
	 */
	@Test
	public void Test() {
		//service的类名 必须是继承自BaseService实现IBaseService接口的
		String SERVICE_BEAN_NAME = "syncTest";
		//调用的方法名
		String methodName = "test2";
		
		//构建参数 参数类型必须与调用的方法参数类型一致
		//调用
		try {
			/*HashMap<String, Object> map2 = new HashMap<>();
			for(int i=0;i<100;i++){
				PuPurchases p = new PuPurchases();
				p.setArtNo("AAa");
				p.setProNo("BBB");
				map2.put(i+"", p);
			}*/
			long start = System.currentTimeMillis();
			ObjectDto objectDto = RpcByThriftUtil.load(SERVICE_BEAN_NAME, methodName, null);
			long end = System.currentTimeMillis();
			System.out.println("RpcByThrift 花费时间："+(end-start));
			//System.out.println(objectDto);
			System.out.println(objectDto.getObj().toString());
			//System.out.println(end-start);
		} catch (AppRunTimeException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getMsg());
			System.out.println(e.getCode());
			System.out.println(e.getArgs());
		}
	}

}
