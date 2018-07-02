package com.waydeep.common.nettyDemo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ReactorDemo implements Runnable{

    public final Selector selector;
    public final ServerSocketChannel serverSocketChannel;
    public ReactorDemo(int port) throws IOException {
        selector =  Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(),port  ));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT).attach(new Acceptor(this));
    }

    @Override
    public void run() {

            try {
                while (!Thread.interrupted()){
                    selector.select();
                    Set<SelectionKey> selectionKeys= selector.selectedKeys();
                    Iterator<SelectionKey> it =selectionKeys.iterator();
                    while (it.hasNext()){
                        SelectionKey selectionKey =  it.next();

                        dispatch(selectionKey);

                        selectionKeys.clear();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


    }

    /**
     * 具体分发处理 分给具体SelectionKey所attach的类
     * @param selectionKey
     */
    private void dispatch(SelectionKey selectionKey) {
        Runnable runnable = (Runnable) selectionKey.attachment();
        if(runnable != null){
            runnable.run();
        }
    }
}
