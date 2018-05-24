package com.waydeep.common.nettyDemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Scanner;

public class NettyTest {
    @Test
    public void nettyServer() throws IOException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(4700).sync();
            System.out.println("服务器开启：");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
    @Test
    public void nettyClientTest() throws IOException, InterruptedException {

        NettyClient  nettyClient = new NettyClient();
        new Thread(nettyClient).start();

        NettyClient.ClientHandle clientHandle = nettyClient.getClient();
       // Scanner scanner = new Scanner(System.in);
        /*while(clientHandle.sendMsg(scanner.nextLine())) {
            ;
        }*/
        while (true){
            Thread.sleep(3000);
            clientHandle.sendMsg("ffffffffff");
            Thread.sleep(3000);
        }
    }
    @Test
    public void reraaf(){
        Scanner scanner = new Scanner(System.in);
        String a = scanner.nextLine();
        System.out.println(a);
    }

    public static void main(String[] args) throws Exception {

        NettyClient  nettyClient = new NettyClient();
        new Thread(nettyClient).start();
        NettyClient.ClientHandle clientHandle = nettyClient.getClient();

        Scanner scanner = new Scanner(System.in);
        while(clientHandle.sendMsg(scanner.nextLine()));
    }
}
