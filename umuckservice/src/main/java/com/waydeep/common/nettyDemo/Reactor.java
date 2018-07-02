package com.waydeep.common.nettyDemo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Runnable {
    public int id = 100001;
    public int bufferSize = 2048;
    @Override
    public void run() {
        init();
    }

    private void init() {
        try {
            ServerSocketChannel serverSocketChannel =  ServerSocketChannel.open();
            Selector selector =  Selector.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(),4700  ));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT).attach(id++);
            System.out.println("服务器开启 .... port:4700");
            listener(selector);
        }catch (Exception e){

        }

    }

    private void listener(Selector selector) throws IOException {
        try {
            while (true){
                Thread.sleep(1*1000);
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey =  iterator.next();
                    dispatch(selector, selectionKey);
                    iterator.remove();
                }
            }
       }catch (Exception ex)
       {
           System.out.println("Error - " + ex.getMessage());
           ex.printStackTrace();
       }

    }

    private void dispatch(Selector selector, SelectionKey selectionKey) throws IOException {
        if(selectionKey.isAcceptable()){
            System.out.println(selectionKey.attachment() + " - 接受请求事件");
            ServerSocketChannel serverSocketChannel =(ServerSocketChannel ) selectionKey.channel();
            SocketChannel socketChannel =  serverSocketChannel.accept();
            socketChannel.configureBlocking(false)
                    .register(selector,SelectionKey.OP_READ|SelectionKey.OP_WRITE);
            System.out.println(selectionKey.attachment() + " - 已连接");
        }
        if(selectionKey.isReadable()){
            //读取客户端数据
            System.out.println(" - 读数据事件");
            SocketChannel clientChannel=(SocketChannel)selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
            clientChannel.read(byteBuffer);

            System.out.println(" - 读取客户端数据：" + getString(byteBuffer));
        }
        if(selectionKey.isWritable()){
            //返回数据给客户端
            System.out.println(" - 返回数据给客户端");
            SocketChannel clientChannel=(SocketChannel)selectionKey.channel();
            ByteBuffer sendBuf = ByteBuffer.allocate(bufferSize);
            String sendText = "hello fuck you from server!!!\n";
            sendBuf.put(sendText.getBytes());
            sendBuf.flip();
            clientChannel.write(sendBuf);
        }
        if (selectionKey.isConnectable()) {
            System.out.println(selectionKey.attachment()
                    + " - 连接事件");
        }
    }

    public static String getString(ByteBuffer buffer){
        String string = "";
        try
        {
            for(int i = 0; i<buffer.position();i++){
                string += (char)buffer.get(i);
            }
            return string;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "";
        }
    }

}
