package cn.com.aperfect.auap.external.nettyRpcModel;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 线程工具
 * @author hasee
 *
 */
public class RpcThreadPool {
	 public static Executor getExecutor(int threads, int queues) {
	        String name = "RpcThreadPool";
	        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
	                queues == 0 ? new SynchronousQueue<Runnable>()
	                        : (queues < 0 ? new LinkedBlockingQueue<Runnable>()
	                                : new LinkedBlockingQueue<Runnable>(queues)),
	                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
	    }
}

