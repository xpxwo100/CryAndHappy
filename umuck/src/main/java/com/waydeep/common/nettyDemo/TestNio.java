package com.waydeep.common.nettyDemo;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class TestNio {
    @Test
    public void test() throws IOException {
       /* RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");
        FileChannel fileChannel = aFile.getChannel();
        ByteBuffer buf = ByteBuffer.allocate(48);
        int bytesRead = fileChannel.read(buf);
        while (bytesRead != -1) {
            System.out.println("Read " + bytesRead);
            buf.flip();
            while(buf.hasRemaining()){
                System.out.print((char) buf.get());
            }
            buf.clear();
            bytesRead = fileChannel.read(buf);
        }
        aFile.close();*/
        CharBuffer c = CharBuffer.allocate(1024);
        c.put("guck");
        c.flip();
        while(c.hasRemaining()){
            System.out.print((char) c.get());
        }
        RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");
        FileChannel fileChannel = aFile.getChannel();
        ByteBuffer header = ByteBuffer.allocate(128);
        ByteBuffer body   = ByteBuffer.allocate(1024);
        ByteBuffer[] bufferArray = { header, body };
        fileChannel.write(bufferArray);

    }
    @Test
    public void test2(){
        PrintWriter pw = null;
        String name = "张松伟";
        int age = 22;
        float score = 32.5f;
        char sex = '男';
        try{
            pw = new PrintWriter(new FileWriter(new File("e:\\file.txt")),true);
            pw.println("fuck you shit xxxxxxpp");
            pw.println("多多指教");
            pw.write(name.toCharArray());
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            pw.close();
        }
    }

    /**
     * 开启Nio服务端
     */
    @Test
    public void testNioServer(){
        new Thread(new Reactor()).start();
        while (true){
            try {
                Thread.sleep(3*1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * NIO创建客户端
     */
    @Test
    public void testConnect() throws IOException {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(),4700));
            while (!socketChannel.finishConnect()) {
                System.out.println("等待非阻塞连接建立....");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("连接成功");
            ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
            byteBuffer.put("hello word from cliend!".getBytes());
            byteBuffer.flip();
            while(byteBuffer.hasRemaining()) {
                socketChannel.write(byteBuffer);
            }
            System.out.println("准备读取数据~~");


            byteBuffer.clear();
            int numBytesRead;
            while ((numBytesRead = socketChannel.read(byteBuffer)) != -1) {
                if (numBytesRead == 0) {
                    // 如果没有数据，则稍微等待一下
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                // 转到最开始
                byteBuffer.flip();
                while(byteBuffer.hasRemaining()){
                    System.out.print((char) byteBuffer.get()); // read 1 byte at a time
                }
                byteBuffer.clear();
            }

        } catch (Exception e) {
          e.printStackTrace();
          if(socketChannel!=null){ socketChannel.close();}
      }
    }
}
