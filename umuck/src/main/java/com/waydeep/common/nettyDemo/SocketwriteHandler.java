package com.waydeep.common.nettyDemo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SocketwriteHandler  implements Runnable{
    private SocketChannel socketChannel;
    public SocketwriteHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel=socketChannel;
        socketChannel.configureBlocking(false);
        SelectionKey selectionKey = socketChannel.register(selector,0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }
    @Override
    public void run() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.clear();
        try {
            String sendText = "hello fuck you from server!!!\n";
            byteBuffer.put(sendText.getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
