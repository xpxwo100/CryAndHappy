package cn.com.aperfect.auap.external.nettyClient;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;

import cn.com.aperfect.auap.external.nettyRpcModel.RpcRequest;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcResponse;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	private volatile Channel channel;
	private ConcurrentHashMap<String, MessageCallBack> mapCallBack = new ConcurrentHashMap<String, MessageCallBack>();
	//代理对象要到这里执行这个方法 把请求参数发出去
	public MessageCallBack sendRequest(RpcRequest request) throws Exception {
		 MessageCallBack callBack = new MessageCallBack(request);
		 mapCallBack.put(request.getMessageId(), callBack);
		 channel.writeAndFlush(request);
		 return callBack;
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		 //返回的数据
		 RpcResponse response = (RpcResponse)msg;
		 MessageCallBack callBack = mapCallBack.get(response.getMessageId());
		 if (callBack != null) {
			 mapCallBack.remove(response.getMessageId());
			 callBack.over(response);
		 }
	}
	 public void close() {
	        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		channel = ctx.channel();
	}

	/**
	 * 发生异常时调用
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
