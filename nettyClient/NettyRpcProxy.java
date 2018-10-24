package cn.com.aperfect.auap.external.nettyClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;

import cn.com.aperfect.auap.external.common.startup.CustomProConfig;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcRequest;
/**
 * 动态代理
 * @author hasee
 *
 */
public class NettyRpcProxy implements InvocationHandler  {
	
	private static AtomicLong id = new AtomicLong(0);
	public static final String SERVER_IP = CustomProConfig.getRpcThriftIP();
	public static final int SERVER_PORT = CustomProConfig.getRpcThriftPort();
	public static <T> T get(Class<?> interfaceClass) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),  new Class<?>[]{interfaceClass}, new NettyRpcProxy());
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		RpcRequest request = new RpcRequest();
		Long idLong = id.incrementAndGet();
	    request.setMessageId(idLong.toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypeParameters(method.getParameterTypes());
        request.setParametersVal(args);
        ClientHandler clientHandler = NettyRpcClient.getInstance().getConnect(SERVER_IP, SERVER_PORT).getClientHandler();
        MessageCallBack callBack = clientHandler.sendRequest(request);
		return callBack.start();
	}
}
