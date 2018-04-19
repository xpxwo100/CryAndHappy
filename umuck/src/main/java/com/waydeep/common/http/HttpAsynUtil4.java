package com.waydeep.common.http;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import com.ning.http.client.cookie.Cookie;
import com.ning.http.client.providers.grizzly.GrizzlyAsyncHttpProvider;
import com.ning.http.client.providers.jdk.JDKAsyncHttpProvider;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;
import io.netty.handler.codec.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Http异步高性能调用类
 */
@SuppressWarnings("unused")
public class HttpAsynUtil4
{

	private static Logger mLogger = LoggerFactory.getLogger(HttpAsynUtil4.class);
	
	public AsyncHttpClient asyncHttpClient=null;
	
	private AsyncHttpClientConfig asyncHttpClientConfig =null;
	
	private NettyAsyncHttpProvider nettyAsyncHttpProvider=null;
	
	private GrizzlyAsyncHttpProvider grizzlyAsyncHttpProvider=null;
	
	private JDKAsyncHttpProvider jdkProvider=null;
	
	private static int gzipMinBytesLength=64;
	
	
	
	/*
	 * 构造函数
	 */
	public HttpAsynUtil4()
	{
	}
	//基础函数
	/**
	 * 获取Builder
	 * @param isSSL
	 * @return
	 */
	public static AsyncHttpClientConfig.Builder getBuilder(boolean isSSL)
	{
		Builder mConfig = new AsyncHttpClientConfig.Builder();
		mConfig.setAllowPoolingConnections(true);
		mConfig.setMaxRequestRetry(2);
		mConfig.setConnectTimeout(Usual.mUrlConTime);
		mConfig.setRequestTimeout(Usual.mUrlReadTime);
		mConfig.setPooledConnectionIdleTimeout(Usual.mUrlReadTime);
		mConfig.setConnectionTTL(Usual.mUrlReadTime);
		mConfig.setMaxConnections(500);
		mConfig.setMaxConnectionsPerHost(300);
		mConfig.setCompressionEnforced(true);	//强制http压缩
//		mConfig.setExecutorService(Executors.newFixedThreadPool(500));
//		if (isSSL)
//		{
			mConfig.setAcceptAnyCertificate(true);
			mConfig.setAllowPoolingSslConnections(true);
			mConfig.setSSLContext(createSimSSLContext());
			mConfig.setSslSessionCacheSize(5000);
			mConfig.setSslSessionTimeout(1000*60*10);
			mConfig.setHostnameVerifier(new HostnameVerifier() 
			{
			     public boolean verify(String urlHostName, SSLSession session) 
			     {
			          return true;
			     }
			});
//		}
		return mConfig;
	}
	/**
	 * 获取唯一AsyncHttpClient
	 * @param mConfig
	 * @return
	 */
	public AsyncHttpClient getAsyncHttpClient(Builder mConfig)
	{
		asyncHttpClientConfig=mConfig.build();
		//Netty在Resin下长时间运行Nio异常
//		if(nettyAsyncHttpProvider==null){
//			nettyAsyncHttpProvider = new NettyAsyncHttpProvider(asyncHttpClientConfig);
//		}
//		if(asyncHttpClient==null){
//			asyncHttpClient = new AsyncHttpClient(nettyAsyncHttpProvider);
//		}
		if(grizzlyAsyncHttpProvider==null){
			grizzlyAsyncHttpProvider = new GrizzlyAsyncHttpProvider(asyncHttpClientConfig);
		}
		if(asyncHttpClient==null){
			asyncHttpClient = new AsyncHttpClient(grizzlyAsyncHttpProvider);
		}
//		if(jdkProvider==null){
//			jdkProvider = new JDKAsyncHttpProvider(asyncHttpClientConfig);
//		}
//		if(asyncHttpClient==null){
//			asyncHttpClient = new AsyncHttpClient(jdkProvider);
//		}
		return asyncHttpClient;
	}
	/**
	 * 释放AsyncHttpClient
	 */
	public void close()
	{
		if(asyncHttpClient!=null)
		{
			asyncHttpClient.getProvider().close();
			asyncHttpClient.close();
			asyncHttpClient=null;
		}
	}
	
	//Http调用函数
	/**
	 * 泛型方法调用
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param asynHandler
	 * @return
	 * @throws Exception
	 */
	public <T> T httpRestful
	(
			String URI, String method, Object data,
			Charset charset, String contextType, String acceptType,
			FluentCaseInsensitiveStringsMap headers, AsyncCompletionHandler<T> asynHandler
	)
	throws Exception
	{
		T returnValue = null;
		boolean isSSL=false;
		if(URI.toLowerCase().trim().startsWith("https")){
			isSSL=true;
		}else{
			isSSL=false;
		}
		// 设置Http请求参数
		Builder mConfig = HttpAsynUtil4.getBuilder(isSSL);
		getAsyncHttpClient(mConfig);
		RequestBuilder mBuilder = HttpAsynUtil4.getRequestBuilder(URI, method, data, contextType,acceptType,headers);
		Request mRequest = mBuilder.build();
		try
		{
			returnValue = asyncHttpClient.executeRequest(mRequest, asynHandler).get();
			return returnValue;
		} 
		finally
		{
//			asyncHttpClient.close();
			this.close();
		}
	}
	
	/**
	 * Http Restful返回HttpURLConnection
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param asynHandler
	 * @return
	 * @throws Exception
	 */
	public Response httpRestfulStream(String URI, String method, Object data, Charset charset,
			String contextType,String acceptType,AsyncCompletionHandler<Response> asynHandler)
	throws Exception
	{
		return httpRestfulStream(URI,method,data,charset,contextType,acceptType,null,asynHandler);
	}
	/**
	 * Http Restful返回HttpURLConnection
	 * 
	 * @param URI 调用Url
	 * @param method POST,GET,PUT,DELETE
	 * @param data 传入数据String
	 * @param charset Charset字符集
	 * @param contextType 数据传输格式
	 * @param isSSL 是否Https
	 * @return InputStream流
	 */
	public Response httpRestfulStream
	(
		String URI, String method, Object data, 
		Charset charset,String contextType,String acceptType,
		FluentCaseInsensitiveStringsMap headers,AsyncCompletionHandler<Response> asynHandler
	)
	throws Exception
	{
		boolean isSSL=false;
		if(URI.toLowerCase().trim().startsWith("https")){
			isSSL=true;
		}else{
			isSSL=false;
		}
		// 设置Http请求参数
		Builder mConfig = getBuilder(isSSL);
		getAsyncHttpClient(mConfig);
		RequestBuilder mBuilder = getRequestBuilder(URI, method, data, contextType,acceptType,headers);
		Request mRequest = mBuilder.build();
		Response mResponse = null;
		try
		{
			mResponse = asyncHttpClient.executeRequest(mRequest, asynHandler).get();
			int mCode = mResponse.getStatusCode();
			if (mCode != HttpURLConnection.HTTP_OK)
			{
				mLogger.warn("HttpStatus异常:" + mCode);
//				new IOException("HttpStatus异常:" + mCode).printStackTrace();
			}
		}
		finally
		{
//			asyncHttpClient.close();
			this.close();
		}
		return mResponse;
	}
	/**
	 * Http Restful返回byte[]数据
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param asynHandler
	 * @return
	 * @throws Exception
	 */
	public byte[] httpRestfulByte(String URI, String method, Object data, Charset charset, 
			String contextType,String acceptType, AsyncCompletionHandler<Response> asynHandler) 
	throws Exception
	{
		return httpRestfulByte(URI,method,data,charset,contextType,acceptType,null,asynHandler);
	}
	/**
	 * Http Restful返回byte[]数据
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param headers
	 * @param asynHandler
	 * @return
	 */
	public byte[] httpRestfulByte
	(
		String URI, String method, Object data, 
		Charset charset, String contextType,String acceptType,
		FluentCaseInsensitiveStringsMap headers, AsyncCompletionHandler<Response> asynHandler
	)
	throws Exception
	{
		return httpRestfulByte(URI,method,data,charset,contextType,acceptType,headers,asynHandler,null);
	}
	/**
	 * Http Restful返回byte[]数据
	 * 
	 * @param URI 调用Url
	 * @param method POST,GET,PUT,DELETE
	 * @param data 传入数据String
	 * @param charset Charset字符集
	 * @param contextType 数据传输格式
	 * @param isSSL 是否Https
	 * @return byte[]数组
	 * @throws Exception
	 */
	public byte[] httpRestfulByte
	(
		String URI, String method, Object data, 
		Charset charset, String contextType,String acceptType,
		FluentCaseInsensitiveStringsMap headers, AsyncCompletionHandler<Response> asynHandler,
		HashMap<String,Object> rheaders
	) 
	throws Exception
	{
		byte[] mReturnData = Usual.mEmptyBytes;
		boolean isSSL=false;
		if(URI.toLowerCase().trim().startsWith("https")){
			isSSL=true;
		}else{
			isSSL=false;
		}
		// 设置Http请求参数
		Builder mConfig = getBuilder(isSSL);
		getAsyncHttpClient(mConfig);
		RequestBuilder mBuilder = getRequestBuilder(URI, method, data, contextType,acceptType,headers);
		Request mRequest = mBuilder.build();
		try
		{
			Response mResponse = asyncHttpClient.executeRequest(mRequest, asynHandler).get();
			int mCode = mResponse.getStatusCode();
			if (mCode != HttpURLConnection.HTTP_OK)
			{
				mLogger.warn("HttpStatus异常:" + mCode);
//				throw new IOException("HttpStatus异常:" + mCode);
			}
			else
			{
				mReturnData = mResponse.getResponseBodyAsBytes();
			}
			//判断获取返回header
			if(rheaders!=null){
				//加入Cookiess
				rheaders.put("cookiess",mResponse.getCookies());
				Iterator<?> iter = mResponse.getHeaders().entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String tmpKey=entry.getKey().toString();
					if(tmpKey.indexOf("cookie")<0){
						rheaders.put(tmpKey,entry.getValue().toString());
					}
				}
			}
		}
		finally
		{
//			asyncHttpClient.close();
			this.close();
		}
		return mReturnData;
	}
	/**
	 * Http Restful返回String数据
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param asynHandler
	 * @return
	 * @throws Exception
	 */
	public String httpRestfulString
	(
		String URI, String method, Object data, 
		Charset charset,String contextType,String acceptType,
		AsyncCompletionHandler<Response> asynHandler
	) 
	throws Exception
	{
		return httpRestfulString(URI,method,data,charset,contextType,acceptType,null,asynHandler);
	}
	public String httpRestfulString
	(
		String URI, String method, Object data, 
		Charset charset,String contextType,String acceptType,
		FluentCaseInsensitiveStringsMap headers, AsyncCompletionHandler<Response> asynHandler
	) 
	throws Exception
	{
		return httpRestfulString(URI,method,data,charset,contextType,acceptType,headers,asynHandler,null);
	}
	/**
	 * Http Restful返回String数据
	 * 
	 * @param URI 调用Url
	 * @param method POST,GET,PUT,DELETE
	 * @param data 传入数据String
	 * @param charset Charset字符集
	 * @param contextType 数据传输格式
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String httpRestfulString
	(
		String URI, String method, Object data, 
		Charset charset,String contextType,String acceptType,
		FluentCaseInsensitiveStringsMap headers, AsyncCompletionHandler<Response> asynHandler,
		HashMap<String,Object> rheaders
	) 
	throws Exception
	{
		String mReturnData = Usual.mEmpty;
		boolean isSSL=false;
		if(URI.toLowerCase().trim().startsWith("https")){
			isSSL=true;
		}else{
			isSSL=false;
		}
		// 设置Http请求参数
		Builder mConfig = getBuilder(isSSL);
		getAsyncHttpClient(mConfig);
		RequestBuilder mBuilder = getRequestBuilder(URI, method, data, contextType,acceptType,headers);
		Request mRequest = mBuilder.build();
		try
		{
			Response mResponse = asyncHttpClient.executeRequest(mRequest, asynHandler).get();
			int mCode = mResponse.getStatusCode();
			if (mCode != HttpURLConnection.HTTP_OK)
			{
				mLogger.warn("HttpStatus异常:" + mCode);
//				throw new IOException("HttpStatus异常:" + mCode);
			}
			else
			{
				mReturnData = mResponse.getResponseBody();
			}
			//判断获取返回header
			if(rheaders!=null){
				//加入Cookiess
				rheaders.put("cookiess",mResponse.getCookies());
				Iterator<?> iter = mResponse.getHeaders().entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String tmpKey=entry.getKey().toString();
					if(tmpKey.indexOf("cookie")<0){
						rheaders.put(tmpKey,entry.getValue().toString());
					}
				}
			}
		}
		finally
		{
//			asyncHttpClient.close();
			this.close();
		}
		return mReturnData;
	}
	/**
	 * Http GET返回HttpURLConnection
	 * 
	 * @param URI 调用Url
	 * @param isSSL 是否Https
	 * @return InputStream流
	 * @throws Exception
	 */
	public Response getStream(String URI) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		return httpRestfulStream(URI, HttpMethod.GET, Usual.mEmpty, Usual.mCharset_utf8,
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http GET返回byte[]数据
	 * 
	 * @param URI 调用Url
	 * @param isSSL 是否Https
	 * @return byte[]数组
	 * @throws Exception
	 */
	public byte[] getByte(String URI) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		return httpRestfulByte(URI, HttpMethod.GET, Usual.mEmpty, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http GET返回String数据
	 * 
	 * @param URI 调用Url
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String getString(String URI) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		return httpRestfulString(URI, HttpMethod.GET, Usual.mEmpty, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http POST返回Response
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据String
	 * @param isSSL 是否Https
	 * @return InputStream
	 * @throws Exception
	 */
	public Response postStream(String URI, String data) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (Usual.isNullOrWhiteSpace(data))
		{
			return null;
		}
		return httpRestfulStream(URI, HttpMethod.POST, data, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http POST返回Response
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据StringBuilder
	 * @param isSSL 是否Https
	 * @return InputStream
	 * @throws Exception
	 */
	public Response postStream(String URI, StringBuilder sb) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (sb == null || sb.length() == 0)
		{
			return null;
		}
		return httpRestfulStream(URI, HttpMethod.POST, sb.toString(), Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http POST返回Response
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据byte[]
	 * @param isSSL 是否Https
	 * @return InputStream
	 * @throws Exception
	 */
	public Response postStream(String URI, byte[] bts) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (bts == null || bts.length == 0)
		{
			return null;
		}
		return httpRestfulStream(URI, HttpMethod.POST,bts, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http POST返回byte[]
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据String
	 * @param isSSL 是否Https
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] postByte(String URI, String data) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (Usual.isNullOrWhiteSpace(data))
		{
			return new byte[0];
		}
		return httpRestfulByte(URI, HttpMethod.POST, data, Usual.mCharset_utf8,
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http POST返回byte[]
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据StringBuilder
	 * @param isSSL 是否Https
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] postByte(String URI, StringBuilder sb) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (sb == null || sb.length() == 0)
		{
			return null;
		}
		return httpRestfulByte(URI, HttpMethod.POST, sb.toString(), Usual.mCharset_utf8,
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http POST返回byte[]
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据byte[]
	 * @param isSSL 是否Https
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] postByte(String URI, byte[] bts) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (bts == null || bts.length == 0)
		{
			return new byte[0];
		}
		return httpRestfulByte(URI, HttpMethod.POST,bts, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http POST返回String
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据String
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String postString(String URI, String data) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (Usual.isNullOrWhiteSpace(data))
		{
			return Usual.mEmpty;
		}
		return httpRestfulString(URI, HttpMethod.POST, data, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http POST返回String
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据StringBuilder
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String postString(String URI, StringBuilder sb) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (sb == null || sb.length() == 0)
		{
			return Usual.mEmpty;
		}
		return httpRestfulString(URI, HttpMethod.POST, sb.toString(), Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http POST返回String
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据byte[]
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String postString(String URI, byte[] bts) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (bts == null || bts.length == 0)
		{
			return Usual.mEmpty;
		}
		return httpRestfulString(URI, HttpMethod.POST,bts, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http PUT返回Response
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据String
	 * @param isSSL 是否Https
	 * @return InputStream
	 * @throws Exception
	 */
	public Response putStream(String URI, String data) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (Usual.isNullOrWhiteSpace(data))
		{
			return null;
		}
		return httpRestfulStream(URI, HttpMethod.PUT, data, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http PUT返回Response
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据StringBuilder
	 * @param isSSL 是否Https
	 * @return InputStream
	 * @throws Exception
	 */
	public Response putStream(String URI, StringBuilder sb) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (sb == null || sb.length() == 0)
		{
			return null;
		}
		return httpRestfulStream(URI, HttpMethod.PUT, sb.toString(), Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http PUT返回Response
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据byte[]
	 * @param isSSL 是否Https
	 * @return InputStream
	 * @throws Exception
	 */
	public Response putStream(String URI, byte[] bts) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (bts == null || bts.length == 0)
		{
			return null;
		}
		return httpRestfulStream(URI, HttpMethod.PUT,bts, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http PUT返回byte[]
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据String
	 * @param isSSL 是否Https
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] putByte(String URI, String data) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (Usual.isNullOrWhiteSpace(data))
		{
			return new byte[0];
		}
		return httpRestfulByte(URI, HttpMethod.PUT, data, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http PUT返回byte[]
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据StringBuilder
	 * @param isSSL 是否Https
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] putByte(String URI, StringBuilder sb) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (sb == null || sb.length() == 0)
		{
			return new byte[0];
		}
		return httpRestfulByte(URI, HttpMethod.PUT, sb.toString(), Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http PUT返回byte[]
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据byte[]
	 * @param isSSL 是否Https
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] putByte(String URI, byte[] bts) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (bts == null || bts.length == 0)
		{
			return new byte[0];
		}
		return httpRestfulByte(URI, HttpMethod.PUT,bts, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http PUT返回String
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据String
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String putString(String URI, String data) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (Usual.isNullOrWhiteSpace(data))
		{
			return Usual.mEmpty;
		}
		return httpRestfulString(URI, HttpMethod.PUT, data, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http PUT返回String
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据StringBuilder
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String putString(String URI, StringBuilder sb) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (sb == null || sb.length() == 0)
		{
			return Usual.mEmpty;
		}
		return httpRestfulString(URI, HttpMethod.PUT, sb.toString(), Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http PUT返回String
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据byte[]
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String putString(String URI, byte[] bts) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (bts == null || bts.length == 0)
		{
			return Usual.mEmpty;
		}
		return httpRestfulString(URI, HttpMethod.PUT, bts, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http DELETE返回Response
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据String
	 * @param isSSL 是否Https
	 * @return InputStream
	 * @throws Exception
	 */
	public Response deleteStream(String URI, String data) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (Usual.isNullOrWhiteSpace(data))
		{
			return null;
		}
		return httpRestfulStream(URI, HttpMethod.DELETE, data, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http DELETE返回Response
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据StringBuilder
	 * @param isSSL 是否Https
	 * @return InputStream
	 * @throws Exception
	 */
	public Response deleteStream(String URI, StringBuilder sb) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (sb == null || sb.length() == 0)
		{
			return null;
		}
		return httpRestfulStream(URI, HttpMethod.DELETE, sb.toString(), Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http DELETE返回Response
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据byte[]
	 * @param isSSL 是否Https
	 * @return InputStream
	 * @throws Exception
	 */
	public Response deleteStream(String URI, byte[] bts) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (bts == null || bts.length == 0)
		{
			return null;
		}
		return httpRestfulStream(URI, HttpMethod.DELETE, bts, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http DELETE返回byte[]
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据String
	 * @param isSSL 是否Https
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] deleteByte(String URI, String data) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (Usual.isNullOrWhiteSpace(data))
		{
			return new byte[0];
		}
		return httpRestfulByte(URI, HttpMethod.DELETE, data, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http DELETE返回byte[]
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据StringBuilder
	 * @param isSSL 是否Https
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] deleteByte(String URI, StringBuilder sb) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (sb == null || sb.length() == 0)
		{
			return new byte[0];
		}
		return httpRestfulByte(URI, HttpMethod.DELETE, sb.toString(), Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http DELETE返回byte[]
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据byte[]
	 * @param isSSL 是否Https
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] deleteByte(String URI, byte[] bts) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (bts == null || bts.length == 0)
		{
			return new byte[0];
		}
		return httpRestfulByte(URI, HttpMethod.DELETE,bts, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http DELETE返回String
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据String
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String deleteString(String URI, String data) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (Usual.isNullOrWhiteSpace(data))
		{
			return Usual.mEmpty;
		}
		return httpRestfulString(URI, HttpMethod.DELETE, data, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http DELETE返回String
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据StringBuilder
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String deleteString(String URI, StringBuilder sb) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (sb == null || sb.length() == 0)
		{
			return Usual.mEmpty;
		}
		return httpRestfulString(URI, HttpMethod.DELETE, sb.toString(), Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	/**
	 * Http DELETE返回String
	 * 
	 * @param URI 调用Url
	 * @param data 传输数据byte[]
	 * @param isSSL 是否Https
	 * @return String
	 * @throws Exception
	 */
	public String deleteString(String URI, byte[] bts) throws Exception
	{
		AsynHandler mHandler=new AsynHandler();
		if (bts == null || bts.length == 0)
		{
			return Usual.mEmpty;
		}
		return httpRestfulString(URI, HttpMethod.DELETE,bts, Usual.mCharset_utf8, 
				Usual.mContentTypeBase,null,mHandler.mHandlerResponse);
	}
	//私有函数
	/**
	 * 获取RequestBuilder
	 * @param URI
	 * @param method
	 * @param data
	 * @param contextType
	 * @return
	 */
	public static RequestBuilder getRequestBuilder(String URI, String method, Object data, 
			String contextType,String acceptType,FluentCaseInsensitiveStringsMap headers)
	{
		if(headers==null){
			headers=new FluentCaseInsensitiveStringsMap();
		}
		//定义content-encoding,accept-encoding状态
		boolean contentGzip=false;
		//定义RequestBuilder
		RequestBuilder mRequestBuilder = new RequestBuilder();
		mRequestBuilder.setUrl(URI);
		// 进行Http请求
		if (Usual.isNullOrWhiteSpace(method))
		{
			mRequestBuilder.setMethod(HttpMethod.POST);
		}
		else
		{
			mRequestBuilder.setMethod(method.toUpperCase());
		}
		//设置Content-Type
		if (Usual.isNullOrWhiteSpace(contextType))
		{
			headers.add("Content-Type", Usual.mContentTypeBase);
		}
		else
		{
			headers.add("Content-Type", contextType);
		}
		//设置Accept参数
		if (!Usual.isNullOrWhiteSpace(acceptType))
		{
			headers.add("Accept", acceptType);
		}
		else
		{
			headers.add("Accept", "*/*");
		}
		//设置浏览器模拟请求头
		headers.add("Protocol", "HTTP/1.1");
//		headers.add("Accept-Encoding","gzip");
		headers.add("Accept-Language","zh-cn");
		headers.add("UserAgent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2327.5 Safari/537.36");
		
		
//		//Connection在非手机环境开启,提高连接速度
//		// 手机:导致每次查询后都会导致cpu占用率达到100%
//		// 手机:并持续两三分钟甚至更长才回落到正常状态
		// Wildfly无法使用Keep-Alive
//		headers.add("Connection", "Keep-Alive");
//		// 手机:在亮屏的情况下操作可以减少很多耗电
//		headers.add("Connection", "Close");

		//如果content-encoding执行Gzip压缩
		if(headers.containsKey("Content-Encoding") && headers.get("Content-Encoding").toString().toLowerCase().indexOf("gzip")>-1){
			contentGzip=true;
		}
		
		if (data != null)
		{
			Class<?> mClass = data.getClass();
			if (mClass.equals(String.class))
			{
				if(((String)data).length()>0){
					byte[] mData=Usual.toBytes((String)data);
					//增加最小字节数判断,低于不做Gzip
					if(Usual.isNullOrZeroBytes(mData) || mData.length<gzipMinBytesLength){
						delContentGzipHeader(headers);
						contentGzip=false;
					}
					//
					if(contentGzip){
						mData=Compress.gzipCompress(mData);
					}
					mRequestBuilder.setBody(mData);
					headers.add(HttpHeaders.Names.CONTENT_LENGTH, mData.length+"");
				}else{
					delContentGzipHeader(headers);
				}
			}
			else if (mClass.equals(byte[].class))
			{
				if(((byte[]) data).length>0)
				{
					byte[] mData=(byte[])data;
					//增加最小字节数判断,低于不做Gzip
					if(Usual.isNullOrZeroBytes(mData) || mData.length<gzipMinBytesLength){
						delContentGzipHeader(headers);
						contentGzip=false;
					}
					if(contentGzip){
						mData=Compress.gzipCompress(mData);
					}
					mRequestBuilder.setBody(mData);
//					mRequestBuilder.setContentLength(mData.length);
					headers.add(HttpHeaders.Names.CONTENT_LENGTH, mData.length+"");
					//使用Chunking
					// ByteArrayInputStream mInStream=new ByteArrayInputStream((byte[])data);
					// InputStreamBodyGenerator mStreamBody=new InputStreamBodyGenerator(mInStream);
					// mBuilder.setBody(mStreamBody);
				}
				else
				{
					delContentGzipHeader(headers);
				}
			}
		}else{
			delContentGzipHeader(headers);
		}
		if(headers!=null && headers.size()>0){
			mRequestBuilder.setHeaders(headers);
		}
		return mRequestBuilder;
	}
	/**
	 * 删除Content-Encoding gzip标识
	 * @param headers
	 */
	private static void delContentGzipHeader(FluentCaseInsensitiveStringsMap headers){
		headers.delete("Content-Encoding");
		headers.add("Content-Encoding","");
	}
	/**
	 * 获取RequestBuilder
	 * @param URI
	 * @param method
	 * @param data
	 * @param contextType
	 * @param acceptType
	 * @return
	 */
	public static RequestBuilder getRequestBuilder(String URI, String method, Object data, 
			String contextType,String acceptType)
	{
		return getRequestBuilder(URI, method, data, contextType,acceptType,null);
	}
	/**
	 * 动态生成SSL认证证书
	 */
	public static SSLContext createSimSSLContext()
	{
		return createSimSSLContext(null, null);
	}
	/**
	 * 动态生成SSL认证证书
	 * 
	 * @param keystore
	 * @param sran
	 * @return
	 */
	public static SSLContext createSimSSLContext(KeyStore keystore, SecureRandom sran)
	{
		try
		{
			//可以为SSL或者TLS
			SSLContext context = SSLContext.getInstance("SSL");
			//TLS标准,必须要数字证书,无法采用EasyX509TrustManager
//			SSLContext context = SSLContext.getInstance("TLS");
			context.init
			(
				null, 
				new TrustManager[] 
				{ 
					new EasyX509TrustManager(keystore) 
				},
				sran
			);
			return context;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	//测试函数
	public static void main(String[] args)
	{
//		String URI = "http://127.0.0.1:4447/auap-biz-web/mapi/site/getHeaderClass";
//		try
//		{
//			HttpAsynUtil httpAsynUtil=new HttpAsynUtil(); 
//			byte[] mBytes = httpAsynUtil.httpRestfulByte(URI, HttpMethod.POST, Usual.mEmptyBytes, Usual.mCharset_utf8,
//			"application/json","application/json",new AsyncCompletionHandler<Response>()
//			{
//				@Override
//				public Response onCompleted(Response response) throws Exception
//				{
//					System.out.println(response.getResponseBody("UTF-8"));
//					// Do something with the Response
//					return response;
//				}
//				@Override
//				public void onThrowable(Throwable t)
//				{
//					// Something wrong happened.
//				}
//			});
//		}
//		catch (Exception e)
//		{
//			// TODO: handle exception
//		}
		
		test02();
	}
	
	private static void test02(){
		
		HttpAsynUtil4 httpAsyn=new HttpAsynUtil4();
		String URI="http://test.ypjyun.com/srm/ClinicCountManager/captcha-image.do";
		HashMap<String,Object> rheaders=new HashMap<>();
		try 
		{
			byte[] mBytes = httpAsyn.httpRestfulByte
			(
					URI,
					"GET",
					null,
					Usual.mCharset_utf8,
					"text/plain",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
					null, 
					new AsyncCompletionHandler<Response>()
					{
						@Override
						public Response onCompleted(Response response) throws Exception
						{
							return response;
						}
						@Override
						public void onThrowable(Throwable t)
						{
							
						}
					},
					rheaders
			);
			String xx="";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void test01(){
		long beginTime=System.currentTimeMillis();
		
		for(int i=0;i<5;i++){
//			String URI = "https://www.cnblogs.com";
			String URI = "http://119.29.105.15/srm/site/index/index.jsp";
			try
			{
				HttpAsynUtil4 httpAsynUtil=new HttpAsynUtil4(); 
				byte[] mBytes = httpAsynUtil.httpRestfulByte(URI, HttpMethod.GET, Usual.mEmptyBytes, Usual.mCharset_utf8,
				Usual.mContentTypeBase,null,new AsyncCompletionHandler<Response>()
				{
					@Override
					public Response onCompleted(Response response) throws Exception
					{
						System.out.println("aaa");
						// Do something with the Response
						return response;
					}
					@Override
					public void onThrowable(Throwable t)
					{
						// Something wrong happened.
					}
				});
				System.out.println("==========================================");
				URI = "https://119.29.105.15/srm/site/index/index.jsp";
				mBytes=new byte[0];
				mBytes = httpAsynUtil.httpRestfulByte(URI, HttpMethod.GET, Usual.mEmptyBytes, Usual.mCharset_utf8,
						Usual.mContentTypeBase,null,new AsyncCompletionHandler<Response>()
						{
							@Override
							public Response onCompleted(Response response) throws Exception
							{
								System.out.println("bbb");
//								System.out.println(response.getResponseBody());
								return response;
							}
							@Override
							public void onThrowable(Throwable t)
							{
								// Something wrong happened.
							}
						});
				System.out.println("==========================================");
			}
			catch (Exception e)
			{
				// TODO: handle exception
			}
		}
		System.out.print("Time:"+(System.currentTimeMillis()-beginTime));
	}
}