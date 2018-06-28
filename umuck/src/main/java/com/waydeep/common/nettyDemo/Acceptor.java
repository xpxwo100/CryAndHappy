package com.waydeep.common.nettyDemo;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * 调度 boss
 */
public class Acceptor implements Runnable{
    private ReactorDemo reactorDemo;
    public Acceptor(ReactorDemo reactorDemo) {
            this.reactorDemo = reactorDemo;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = reactorDemo.serverSocketChannel.accept();
            if(socketChannel != null){
                //具体的work
                new SocketReadHandler(reactorDemo.selector, socketChannel);
               // new SocketwriteHandler(reactorDemo.selector, socketChannel);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
