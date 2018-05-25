package com.xpx.cloudcenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableEurekaServer
public class ServicecoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServicecoreApplication.class, args);
	}

/*	@Test
	public void test(){
		ExecutorService e = Executors.newCachedThreadPool();
		for(int i=0;i<100;i++){
			final int finalI = i;
			e.execute(new Runnable() {
				@Override
				public void run() {
					*//*TestBean a = ServicecoreApplication.show(finalI);
					System.out.println(Thread.currentThread().getName());
					System.out.println(a.getA());*//*
					System.out.println(count);
				}
			});
		}
		e.shutdown();
	}*/
	public static TestBean show(int a){
		TestBean t = new TestBean(a);
		/*synchronized (ServicecoreApplication.class){
			t.setA(aaa);
		}*/
		return t;
	}
	protected long count = 0;
	@Test
	public void add() throws IOException {
	//	Selector selector = Selector.open();
		/*ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_READ);*/
		/*while(true) {
			int a = selector.select();
			if(a == 0) continue;
			Set set = selector.selectedKeys();
			Iterator keyIterator = set.iterator();
			while(keyIterator.hasNext()) {
				SelectionKey key = (SelectionKey) keyIterator.next();
				if(key.isAcceptable()) {
					// a connection was accepted by a ServerSocketChannel.
				} else if (key.isConnectable()) {
					// a connection was established with a remote server.
				} else if (key.isReadable()) {
					// a channel is ready for reading
				} else if (key.isWritable()) {
					// a channel is ready for writing
				}
				keyIterator.remove();
			}
		}*/
	/*	SocketChannel socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress("110.80.25.71", 9002));
		ByteBuffer buf = ByteBuffer.allocate(48);
		int bytesRead = socketChannel.read(buf);
		while(bytesRead!= -1){
			while(buf.hasRemaining()) {
					System.out.println(buf.get());
				}
		}
		socketChannel.close();*/
		/*ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(9999));*/
		/*while(true){
			SocketChannel socketChannel =serverSocketChannel.accept();

			//do something with socketChannel...
		}*/
	/*	//Java NIO 管道是2个线程之间的单向数据连接
		Pipe pipe = Pipe.open();
		//要向管道写数据，需要访问sink通道。像这样：
		Pipe.SinkChannel sinkChannel = pipe.sink();
		//从管道读取数据
		Pipe.SourceChannel sourceChannel = pipe.source();*/
		String a = "fuck you dsadgauydgyaguy";
		byte[] adad = a.getBytes();
		ByteBuffer buf = ByteBuffer.wrap(adad);
		//

		byte[] b = new byte[buf.remaining()];
		buf.get(b, 0, b.length);
		String adsad = new String(b);
		System.out.println(adsad);
	}
}
