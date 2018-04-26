package cn.com.aperfect.base.thrift;


import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;

import cn.com.aperfect.auap.external.common.startup.ServiceProConfig;

public class RemoteProxyServer {
	/*端口*/
	public static final int SERVER_PORT = ServiceProConfig.getRpcThriftPort();
	/**
	 * 开启链接调用方法
	 */
    public static void exeute() {
        try {
        	/**
        	 * 多线程服务模型使用非阻塞IO（需要用TFramedTransport数据传输方式 ）
        	 * TThreadedSelectorServer模式是目前Thrift提供的最高级的模式，它内部有如果几个部分构成：
        	 * 一个AcceptThread线程对象，专门用于处理监听socket上的新连接；
        	 * 若干个SelectorThread对象专门用于处理业务socket的网络I/O操作，所有网络数据的读写均是有这些线程来完成；
        	 * 一个负载均衡器SelectorThreadLoadBalancer对象，主要用于AcceptThread线程接收到一个新socket连接请求时，
        	 * 决定将这个新连接请求分配给哪个SelectorThread线程。
        	 * 
        	 */
    	    TProcessor tprocessor = new RemoteProxyByThrift.Processor<RemoteProxyByThrift.Iface>(new RemoteProxyByThriftServiceImpl());
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(SERVER_PORT);
            TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
	        tArgs.processor(tprocessor);
	        tArgs.protocolFactory(new TBinaryProtocol.Factory());
	        TServer server = new TThreadedSelectorServer(tArgs);
	        server.serve();
        	
        	/**
        	 * 多线程服务模型使用标准的阻塞式IO，预先创建一组线程处理请求
        	 * 在并发量较大时新连接也能够被及时接受。线程池模式比较适合服务器端能预知最多有多少个客户端并发的情况
        	 * 线程池模式的处理能力受限于线程池的工作能力，当并发请求数大于线程池中的线程数时，新请求也只能排队等待。
        	 */
            /*TProcessor tprocessor = new RemoteProxyByThrift.Processor<RemoteProxyByThrift.Iface>(new RemoteProxyByThriftServiceImpl());
            TServerTransport tserverTransport = new TServerSocket(SERVER_PORT);
            TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(tserverTransport);
        	tArgs.minWorkerThreads(10);
			tArgs.maxWorkerThreads(200);
            tArgs.processor(tprocessor);
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            TServer server = new TThreadPoolServer(tArgs);
            server.serve();*/
            
        }catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
