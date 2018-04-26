package cn.com.aperfect.base.thrift;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class RpcByThriftListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//随后端启动时启动
		RemoteProxyServer.exeute();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
