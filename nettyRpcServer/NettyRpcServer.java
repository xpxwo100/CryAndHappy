package cn.com.aperfect.base.nettyRpcServer;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.aperfect.auap.external.common.startup.ServiceProConfig;
import cn.com.aperfect.auap.external.nettyRpcModel.ProtostuffCodecUtil;
import cn.com.aperfect.auap.external.nettyRpcModel.ProtostuffDecoder;
import cn.com.aperfect.auap.external.nettyRpcModel.ProtostuffEncoder;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcRequest;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcResponse;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcThreadPool;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
/**
 * netty启动
 * @author hasee
 *
 */
public class NettyRpcServer extends Thread {
	private static final Log logger = LogFactory.getLog(NettyRpcServer.class);
	private static NettyRpcServer server = null;
	public static final int SERVER_PORT = ServiceProConfig.getRpcThriftPort();
	private static volatile ListeningExecutorService threadPoolExecutor;
	private NettyRpcServer() {
	}
	@Override
	public void run() {
		bind(SERVER_PORT);
	}
	
	public void bind(int port){
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workGroup);
			b.channel(NioServerSocketChannel.class);
			//option主要是针对boss线程组，child主要是针对worker线程组
			b.option(ChannelOption.SO_BACKLOG, 128);
			b.childOption(ChannelOption.SO_KEEPALIVE, true);
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					 ProtostuffCodecUtil util = new ProtostuffCodecUtil();
				     util.setRpcDirect(true);
				     ChannelPipeline pipeline = ch.pipeline();
					 pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));  
                     pipeline.addLast(new LengthFieldPrepender(4)); 
                     pipeline.addLast(new ProtostuffEncoder(util));
                     pipeline.addLast(new ProtostuffDecoder(util));
                     pipeline.addLast(new ServerHandler()); 

				}
			});
			ChannelFuture channelFuture = b.bind(port).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
	public static void submit(Callable<Boolean> task, final ChannelHandlerContext ctx, final RpcRequest request, final RpcResponse response) {
		 if (threadPoolExecutor == null) {
	            synchronized (NettyRpcServer.class) {
	                if (threadPoolExecutor == null) {
	                	threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(5, -1));
	                    
	                }
	            }
	        }
		 ListenableFuture<Boolean> listenableFuture =  threadPoolExecutor.submit(task);
		 Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
			 @Override
			public void onSuccess(Boolean result) {
				 //业务逻辑处理成功后把结果返回客户端
				 ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
	                    @Override
	                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
	                    	logger.debug("RPC Server Send message-id respone: " + request.getMessageId()+"value:"+response.getResult());
	                    }
	                });
			}
			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		 });
	}
	 public static void startUp() {
		 	init();
	        server.start();
	 }
	 private static void init() {
		 if(server == null){
			 server = new NettyRpcServer();
		 }
	 }
	 
	 public static void main(String[] args) {
		 NettyRpcServer.startUp();
	}
}
