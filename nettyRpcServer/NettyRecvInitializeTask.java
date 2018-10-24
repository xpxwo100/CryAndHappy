package cn.com.aperfect.base.nettyRpcServer;

import io.netty.channel.ChannelHandlerContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.aperfect.auap.external.nettyRpcModel.RpcRequest;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcResponse;
import cn.com.aperfect.base.thrift.RemoteProxyByThriftServiceImpl;
/**
 * 独立的线程业务处理
 * @author hasee
 *
 */
public class NettyRecvInitializeTask implements Callable<Boolean> {
	private static final Log logger = LogFactory.getLog(RemoteProxyByThriftServiceImpl.class);
	private RpcRequest request = null;
	private RpcResponse response = null;
	private ChannelHandlerContext ctx = null;
	protected boolean returnNotNull = true;
	public static final String FILTER_RESPONSE_MSG = "Illegal request,NettyRPC server refused to respond!";

	public NettyRecvInitializeTask(RpcRequest request, RpcResponse response,
			ChannelHandlerContext ctx) {
		this.request = request;
		this.response = response;
		this.ctx = ctx;
	}

	public NettyRecvInitializeTask() {
	}

	@Override
	public Boolean call() throws Exception {
		try {
			RpcResponse res = (RpcResponse) reflect(request);
			response.setMessageId(request.getMessageId());
			response.setResult(res.getResult());
	        response.setError(res.getError());
	        response.setReturnNotNull(res.isReturnNotNull());
	        logger.debug("NettyRecvInitializeTask call"+response.toString());
	        return Boolean.TRUE;
		}catch (Throwable t) {
			response.setError(getStackTrace(t));
            t.printStackTrace();
            logger.debug("RPC Server invoke error!\n");
            return Boolean.FALSE;
		}
	}

	/**
	 * 調用接口
	 * 
	 * @param request2
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private Object reflect(RpcRequest request) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException, ClassNotFoundException {
		String methodName = request.getMethodName();
		Object[] parameters = request.getParametersVal();
		NettyRpcLoadImpl nettyRpcLoadImpl = new NettyRpcLoadImpl();
		return MethodUtils.invokeMethod(nettyRpcLoadImpl, methodName,parameters);
	}

	public String getStackTrace(Throwable ex) {
		StringWriter buf = new StringWriter();
		ex.printStackTrace(new PrintWriter(buf));
		return buf.toString();
	}
}
