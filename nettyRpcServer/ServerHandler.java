package cn.com.aperfect.base.nettyRpcServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcRequest;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcResponse;
/**
 * 业务处理类
 * @author hasee
 *
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		RpcResponse response = new RpcResponse();
		System.out.println("channelRead");
		//处理请求
		RpcRequest request = (RpcRequest)msg;
		NettyRecvInitializeTask nettyRecvInitializeTask = new NettyRecvInitializeTask(request,response,ctx);
		//不要阻塞nio线程，复杂的业务逻辑丢给专门的线程池
		NettyRpcServer.submit(nettyRecvInitializeTask, ctx, request, response);
	}
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}
	

	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
