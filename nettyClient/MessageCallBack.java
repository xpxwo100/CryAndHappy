package cn.com.aperfect.auap.external.nettyClient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.aperfect.auap.external.nettyRpcModel.RpcRequest;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcResponse;


public class MessageCallBack {
	private RpcResponse response;
	private RpcRequest request;
	private Lock lock = new ReentrantLock();
	private Condition finish = lock.newCondition();
	
	public MessageCallBack(RpcRequest request) {
		this.request = request;
	}
	 public RpcResponse get() {
		 return this.response;
	 }
	 public void set(RpcResponse response) {
		  this.response = response;
	 }
	 public Object start() throws InterruptedException {
	        try {
	            lock.lock();
	            //设定一下超时时间，rpc服务器太久没有相应的话，就默认返回空吧。
	            finish.await(10*1000, TimeUnit.MILLISECONDS);//等待被over唤起
	            if (this.response != null) {
	            	 return this.response;
	            } else {
	                 return null;
	            }
	        } finally {
	        	NettyRpcClient.getInstance().unLoad();
	            lock.unlock();
	        }
	}
	/**
	 * 当读取返回数据的时候调用，唤起线程
	 * @param rpcResponse
	 */
	public void over(RpcResponse rpcResponse) {
		try {
			lock.lock();
			finish.signal();
			this.response = rpcResponse;
		} finally {
			lock.unlock();
		}
	}
	 
	 
	 
/*	 private boolean getInvokeResult() {
	        return (!this.response.getError().equals(RpcSystemConfig.FILTER_RESPONSE_MSG) &&
	                (!this.response.isReturnNotNull() || (this.response.isReturnNotNull() && this.response.getResult() != null)));
	    }*/
}
