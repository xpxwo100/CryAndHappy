package com.waydeep.common.nettyDemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.UnsupportedEncodingException;

public class NettyClient implements Runnable {
    static ClientHandle client = new ClientHandle();
    public ClientHandle getClient(){
        return  client;
    }
    private SocketChannel socketChannel;
    public void sendMessage(Object msg) {
        if (socketChannel != null) {
            socketChannel.writeAndFlush(msg);
        }
    }
    @Override
    public void run() {
        String host = "127.0.0.1";
        int port = 4700;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            //保持连接数
            b.option(ChannelOption.SO_BACKLOG, 128);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(client);
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            if (f.isSuccess()) {
                // 得到管道，便于通信
                socketChannel = (SocketChannel) f.channel();
                System.out.println("客户端开启成功...");
            }
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 客户端发消息和接受消息的处理
     */
    public static class ClientHandle extends ChannelInboundHandlerAdapter {
        ChannelHandlerContext ctx;
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            this.ctx = ctx;
        }
        public boolean sendMsg(String msg){
            System.out.println("客户端发送消息："+msg);
            byte[] req = msg.getBytes();
            ByteBuf m = Unpooled.buffer(req.length);
            m.writeBytes(req);
            ctx.writeAndFlush(m);
            return msg.equals("q")?false:true;
        }
        /**
         * 收到服务器消息后调用
         * @throws
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            String body = new String(req,"utf-8");
            System.out.println("服务器消息："+body);
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
}
