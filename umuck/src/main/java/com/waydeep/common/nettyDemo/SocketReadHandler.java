package com.waydeep.common.nettyDemo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SocketReadHandler  implements Runnable{
    private SocketChannel socketChannel;
    public SocketReadHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel=socketChannel;
        socketChannel.configureBlocking(false);
        SelectionKey selectionKey = socketChannel.register(selector,0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.clear();
        try {
            byteBuffer.flip();
            socketChannel.read(byteBuffer);
            while(byteBuffer.hasRemaining()){
                System.out.print(" - 读取客户端数据："+ (char) byteBuffer.get()); // read 1 byte at a time
            }
        } catch (IOException e) {
            e.printStackTrace();
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
