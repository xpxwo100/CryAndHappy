package cn.com.aperfect.auap.external.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import cn.com.aperfect.auap.external.common.startup.CustomProConfig;
import cn.com.aperfect.auap.external.exception.AppRunTimeException;
import cn.com.aperfect.auap.external.exception.CommandException;
/**
 * rpc客户端
 * @author hasee
 *
 */
public class RemoteProxyServiceClient {
	public static final String SERVER_IP = CustomProConfig.getRpcThriftIP();
	public static final int SERVER_PORT = CustomProConfig.getRpcThriftPort();
    public ReSult load(DataWrapper dataWrapper){
        TTransport transport = null;
        ReSult result = null;
        try {
        	//非阻塞  服务端可使用 TNonblockingServer 下的  TThreadedSelectorServer
        	 transport = new TFramedTransport(new TSocket(SERVER_IP, SERVER_PORT));
        	
        	 //阻塞  服务端可使用TSimpleServer、TThreadedServer、 TThreadPoolServer 3种
        	 //transport = new TSocket(SERVER_IP, SERVER_PORT);
        	 result = buildConnection(transport,dataWrapper,result);
        } catch (Exception e) {
            e.printStackTrace();
            result = new ReSult();
            result.setIsSuccess(false);
            result.setErrorMessage(e.getMessage());
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
		return result;
    }
    /**
     * 建立连接
     * @param transport
     * @param dataWrapper
     * @param result
     * @return
     * @throws TException
     */
    private ReSult buildConnection(TTransport transport,DataWrapper dataWrapper,ReSult result) throws TException{
    	  // 协议要和服务端一致
        TProtocol protocol = new TBinaryProtocol(transport);// 二进制格式
        RemoteProxyByThrift.Client client = new RemoteProxyByThrift.Client(protocol);
        transport.open();
        result = client.load(dataWrapper);
    	return result;
    }
}
