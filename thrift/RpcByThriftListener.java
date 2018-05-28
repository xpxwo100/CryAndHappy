package cn.com.aperfect.base.thrift;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class RpcByThriftListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//使在自己的线程中运行，彻底解决同线程会阻塞http接口的问题
		//ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 4, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			@Override
			public void run() {
				RemoteProxyServer.exeute();
			}
		});
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
