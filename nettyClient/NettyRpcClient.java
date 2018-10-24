package cn.com.aperfect.auap.external.nettyClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.aperfect.auap.external.common.startup.CustomProConfig;
import cn.com.aperfect.auap.external.nettyRpcModel.ProtostuffCodecUtil;
import cn.com.aperfect.auap.external.nettyRpcModel.ProtostuffDecoder;
import cn.com.aperfect.auap.external.nettyRpcModel.ProtostuffEncoder;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcThreadPool;



public class NettyRpcClient  implements Runnable {
	//已连接主机的缓存
    private static Map<String, NettyRpcClient> clientMap = new HashMap<String, NettyRpcClient>();
    private static volatile NettyRpcClient nettyRpcClient;
    private String host;
    private int port;
    //等待Netty服务端链路建立通知信号
    private Lock lock = new ReentrantLock();
    private Condition signal = lock.newCondition();
    private ClientHandler clientHandler = null;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(5, -1);
    private EventLoopGroup group = new NioEventLoopGroup();
    private NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
	private NettyRpcClient() {
	}
	 public static NettyRpcClient getInstance() {
	        if (nettyRpcClient == null) {
	            synchronized (NettyRpcClient.class) {
	                if (nettyRpcClient == null) {
	                	nettyRpcClient = new NettyRpcClient();
	                }
	            }
	        }
	        return nettyRpcClient;
	    }
	 public  NettyRpcClient getConnect(String host, int port) throws InterruptedException {
	        if (clientMap.containsKey(host + port)) {
	            return clientMap.get(host + port);
	        }
	        this.host = host;
	        this.port = port;
	        NettyRpcClient nettyRpcClient =  getInstance();
	        threadPoolExecutor.submit(nettyRpcClient);
	        clientMap.put(host + port, nettyRpcClient);
	        return nettyRpcClient;
	    }
	 public void unLoad() {
		    clientHandler.close();
	        threadPoolExecutor.shutdown();
	        group.shutdownGracefully();
	  }
     @Override
	 public void run() {
    	 try {
			connect(host, port);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	 }	
	 private  NettyRpcClient connect(String host, int port) throws InterruptedException {
		 	try {
		 		Bootstrap bootstrap = new Bootstrap();
		        bootstrap.group(group);
		        bootstrap.channel(NioSocketChannel.class);
		        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
		            @Override
		            public void initChannel(SocketChannel channel) throws Exception {
		            	ProtostuffCodecUtil util = new ProtostuffCodecUtil();
		                util.setRpcDirect(false);
		            	channel.pipeline()
		                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
		                        .addLast(new LengthFieldPrepender(4))
		                        .addLast(new ProtostuffEncoder(util))
		                        .addLast(new ProtostuffDecoder(util))
		                		.addLast(new ClientHandler());
		            }
		        });
		        ChannelFuture future = bootstrap.connect(host, port);
		        future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if(future.isSuccess()){
							ClientHandler clientHandler = future.channel().pipeline().get(ClientHandler.class);
							setClientHandler(clientHandler);
						}
					}
				});
		        //future.channel().closeFuture().sync(); 
		 	}catch (Exception e) {
				e.printStackTrace();
				unLoad();
			}/*finally{
				group.shutdownGracefully();
			}*/
		 
	        return nettyRpcClient;
	 }
	/* public Object invoke(RpcRequest request) throws Exception{
		 MessageCallBack callBack = null;
		 ClientHandler clientHandler = getClientHandler();
		 callBack = clientHandler.sendRequest(request);
		 return callBack.start();
	 }*/
	 
	 public ClientHandler getClientHandler() throws InterruptedException {
	        try {
	            lock.lock();
	            //Netty服务端链路没有建立完毕之前，先挂起等待
	            if (clientHandler == null) {
	                signal.await();
	            }
	            return clientHandler;
	        } finally {
	            lock.unlock();
	        }
	}
	 
	 public void setClientHandler(ClientHandler clientHandler) {
	        try {
	            lock.lock();
	            this.clientHandler = clientHandler;
	            //唤醒所有等待客户端RPC线程
	            signal.signalAll();
	        } finally {
	            lock.unlock();
	        }
	    } 
	 
	public static Map<String, NettyRpcClient> getClientMap() {
		return clientMap;
	}

	public static void setClientMap(Map<String, NettyRpcClient> clientMap) {
		NettyRpcClient.clientMap = clientMap;
	}


	public EventLoopGroup getGroup() {
		return group;
	}

	public void setGroup(EventLoopGroup group) {
		this.group = group;
	}

}
