package com.waydeep.common.http;

import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.waydeep.util.ProtostuffUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 项目组http请求拓展，封装http请求与返回值的序列化和反序列化
 */
@SuppressWarnings("unchecked")
public class HttpAsyn
{

	/**
	 * 默认使用java fst数据格式
	 */
	static String defaultFstType = "application/x-fst";
	/**
	 * Srping3.2以后自带Protobuf格式支持
	 */
	static String defaultType = defaultFstType;
	static String springProtoType = "application/x-protobuf";
	static String defaultProtoType = "text/x-protobuf";
	static String defaultProtoStuffType = "application/x-protostuff";
	static String defaultJsonType = "application/json";
	static String defaultJsonJSType = "text/json";
	static String defaultXmlType = "application/xml";
	static String defaultXmlJSType = "text/xml";
	static String defaultStringType = "text/plain";
	static String defaultByteType = "application/octet-stream";

	
	//Rest基础调用函数
	/**
	 * Rest基础调用函数,使用FST作为序列化格式
	 * @param uri
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param defaultValue
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRest
	(
			String uri, String method, DataWrapper<K> data, 
			String contextType, String acceptType, final HttpResult<T> defaultValue,
			FluentCaseInsensitiveStringsMap headers
	)
	throws Exception
	{
		return HttpAsyn.<T,K>httpRestFst(uri,method,data,contextType,acceptType,defaultValue,headers);
	}
	/**
	 * Rest基础调用函数,使用FST作为序列化格式
	 * @param uri
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param defaultValue
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRest
	(
			String uri, String method, DataWrapper<K> data, 
			String contextType, String acceptType, final HttpResult<T> defaultValue,
			FluentCaseInsensitiveStringsMap headers,HashMap<String,Object> rheaders
	)
	throws Exception
	{
		return HttpAsyn.<T,K>httpRestFst(uri,method,data,contextType,acceptType,defaultValue,headers,rheaders);
	}
	/**
	 * Rest基础调用函数,使用FST作为序列化格式
	 * @param uri
	 * @param method
	 * @param data
	 * @param defaultValue
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRest(String uri, String method, DataWrapper<K> data,  final HttpResult<T> defaultValue) 
	throws Exception
	{
		return HttpAsyn.<T,K>httpRest(uri, method, data, defaultType, defaultType, defaultValue,null);
	}
	//ProtoBuf数据格式
	/**
	 * 将传入参数K进行protobuf序列化，并调用REST，返回T类型数据
	 * 默认UTF-8编码，Content-Type/Accept application/x-protobuf，ssl=false
	 * 
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestProto
	(
			String uri, String method, DataWrapper<K> data, 
			String contextType, String acceptType, final HttpResult<T> defaultValue,
			FluentCaseInsensitiveStringsMap headers
	)
	throws Exception
	{
		return httpRestProto(uri,method,data,contextType,acceptType,defaultValue,headers,null);
	}
	/**
	 * 将传入参数K进行protobuf序列化，并调用REST，返回T类型数据
	 * 默认UTF-8编码，Content-Type/Accept application/x-protobuf，ssl=false
	 * 
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestProto
	(
			String uri, String method, DataWrapper<K> data, 
			String contextType, String acceptType, final HttpResult<T> defaultValue,
			FluentCaseInsensitiveStringsMap headers,HashMap<String,Object> rheaders
	)
	throws Exception
	{
		byte[] bts = null;
		if (data != null)
		{
			bts = ProtostuffUtil.encodeTuff(data,data.getClass());
		}
		HttpAsynUtil4 httpAsynUtil=new HttpAsynUtil4(); 
		byte[] mBytes = httpAsynUtil.httpRestfulByte
		(
				uri, 
				method, 
				bts, 
				Usual.mCharset_utf8,
				contextType,
				acceptType,
				headers,
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
		HttpResult<T> result=new HttpResult<>();
		if(!Usual.isNullOrZeroBytes(mBytes)){
			Schema<Object> mSchema = (Schema<Object>) ProtostuffUtil.getClassSchema(defaultValue.getClass());
			ProtobufIOUtil.mergeFrom(bts, result, mSchema);
			mBytes=Usual.mEmptyBytes;
		}
		return result;
	}
	/**
	 * 将传入参数K进行protobuf序列化，并调用REST，返回T类型数据 
	 * 默认UTF-8编码，Content-Type/Accept application/x-protobuf，ssl=false
	 * 
	 * @param uri
	 * @param method
	 * @param data
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestProto(String uri, String method, DataWrapper<K> data,  final HttpResult<T> defaultValue) 
	throws Exception
	{
		return httpRestProto(uri, method, data, defaultProtoType, defaultProtoType, defaultValue,null);
	}
	//Json数据格式
	/**
	 * 将传入参数K进行json序列化，并调用REST，返回HttpResult<T>类型数据 
	 * 默认UTF-8编码，Content-Type/Accept application/json，ssl=false
	 * 
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param cls jackson定义的JaveType类型，用于复杂对象类型需进行JavaType的构造
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestJson
	(
			String uri, String method, DataWrapper<K> data, 
			String contextType, final String acceptType,  final JavaType cls,
			FluentCaseInsensitiveStringsMap headers
	) 
	throws Exception
	{
		return httpRestJson(uri,method,data,contextType,acceptType,cls,headers,null);
	}
	/**
	 * 将传入参数K进行json序列化，并调用REST，返回HttpResult<T>类型数据 
	 * 默认UTF-8编码，Content-Type/Accept application/json，ssl=false
	 * 
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param cls jackson定义的JaveType类型，用于复杂对象类型需进行JavaType的构造
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestJson
	(
			String uri, String method, DataWrapper<K> data, 
			String contextType, final String acceptType,  final JavaType cls,
			FluentCaseInsensitiveStringsMap headers,HashMap<String,Object> rheaders
	) 
	throws Exception
	{
		byte[] bts = Usual.mEmptyBytes;
		if (data != null)
		{
			ObjectMapper mapper =HttpAsyn.jsonMaper(contextType);
			bts = mapper.writeValueAsBytes(data);
		}
		HttpAsynUtil4 httpAsynUtil=new HttpAsynUtil4(); 
		byte[] mBytes = httpAsynUtil.httpRestfulByte
		(
				uri, 
				method, 
				bts, 
				Usual.mCharset_utf8,
				contextType,
				acceptType,
				headers,
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
		HttpResult<T> result=new HttpResult<>();
		if(!Usual.isNullOrZeroBytes(mBytes)){
			ObjectMapper mapper =HttpAsyn.jsonMaper(acceptType);
			result = mapper.readValue(mBytes, cls);
			mBytes=Usual.mEmptyBytes;
		}
		return result;
	}
	/**
	 * 将传入参数K进行json序列化，并调用REST，返回HttpResult<T>类型数据 , 如果返回的是List，请调用
	 * {@link #httpRestJson(String, String, Object, Charset, String, String, Boolean, JavaType)} 或
	 * {@link #httpRestfulListJson(String, String, Object, boolean, Class)} 默认UTF-8编码，Content-Type/Accept
	 * application/json，ssl=false
	 * 
	 * @param uri
	 * @param method
	 * @param data
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestJson(String uri, String method, DataWrapper<K> data,  final Class<T> cls) throws Exception
	{
		JavaType type = TypeFactory.defaultInstance().constructParametricType(HttpResult.class, cls);
		return httpRestJson(uri, method, data, defaultJsonType, defaultJsonType,  type,null);
	}
	public static <T, K> HttpResult<T> httpRestJson(String uri, String method, DataWrapper<K> data,  final Class<T> cls,FluentCaseInsensitiveStringsMap headers) throws Exception
	{
		JavaType type = TypeFactory.defaultInstance().constructParametricType(HttpResult.class, cls);
		return httpRestJson(uri, method, data, defaultJsonType, defaultJsonType, type,headers);
	}
	/**
	 * 将传入参数K进行json序列化，并调用REST，返回HttpResult<List<T>>类型数据 
	 * 默认UTF-8编码，Content-Type/Accept application/json，ssl=false
	 * 
	 * @param uri
	 * @param method
	 * @param data
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<List<T>> httpRestJsonList(String uri, String method, DataWrapper<K> data,  final Class<T> cls) throws Exception
	{
		TypeFactory factory = TypeFactory.defaultInstance();
		JavaType type = factory.constructParametricType(HttpResult.class, factory.constructParametricType(List.class, cls));
		return httpRestJson(uri, method, data, defaultJsonType, defaultJsonType, type,null);
	}
	public static <T, K> HttpResult<List<T>> httpRestJsonList(String uri, String method, DataWrapper<K> data,  final Class<T> cls,FluentCaseInsensitiveStringsMap headers) throws Exception
	{
		TypeFactory factory = TypeFactory.defaultInstance();
		JavaType type = factory.constructParametricType(HttpResult.class, factory.constructParametricType(List.class, cls));
		return httpRestJson(uri, method, data, defaultJsonType, defaultJsonType, type,headers);
	}
	//byte[]数据格式
	/**
	 * 
	 * @param uri
	 * @param method
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] httpRestfulBytes(String uri, String method, Object data) throws Exception
	{
		AsynHandler mHandler = new AsynHandler();
		HttpAsynUtil4 httpAsynUtil=new HttpAsynUtil4();
		return httpAsynUtil.httpRestfulByte(uri, method, data, Usual.mCharset_utf8, defaultByteType, defaultByteType, mHandler.mHandlerResponse);
	}
	//String字符串数据格式
	/**
	 * 
	 * @param uri
	 * @param method
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String httpRestfulString(String uri, String method, Object data) throws Exception
	{
		AsynHandler mHandler = new AsynHandler();
		HttpAsynUtil4 httpAsynUtil=new HttpAsynUtil4();
		return httpAsynUtil.httpRestfulString(uri, method, data, Usual.mCharset_utf8, defaultStringType, defaultStringType, mHandler.mHandlerResponse);
	}
	//Java FST数据格式///////////////////////////////////////////////////////////////
	/**
	 * 将传入参数K进行FST序列化，并调用REST，返回HttpResult<T>类型数据 
	 * 默认UTF-8编码，Content-Type/Accept application/x-fst，ssl=false
	 * 
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param cls jackson定义的JaveType类型，用于复杂对象类型需进行JavaType的构造
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestFst
	(
			String uri, String method, DataWrapper<K> data, 
			String contextType, String acceptType,final HttpResult<T> defaultValue,
			FluentCaseInsensitiveStringsMap headers
	) 
	throws Exception
	{
		return httpRestFst(uri,method,data,contextType,acceptType,defaultValue,headers,null);
	}
	/**
	 * 将传入参数K进行FST序列化，并调用REST，返回HttpResult<T>类型数据 
	 * 默认UTF-8编码，Content-Type/Accept application/x-fst，ssl=false
	 * 
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param cls jackson定义的JaveType类型，用于复杂对象类型需进行JavaType的构造
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestFst
	(
			String uri, String method, DataWrapper<K> data, 
			String contextType, String acceptType,final HttpResult<T> defaultValue,
			FluentCaseInsensitiveStringsMap headers,HashMap<String,Object> rheaders
	) 
	throws Exception
	{
		byte[] bts = Usual.mEmptyBytes;
		if (data != null)
		{
			bts = FSTUtil.encode(data,data.getClass());
		}

		/**
		 * 接收异步异常
		 */
		class  SetableThrowable{
			/**
			 * 获取或设置服务器server自定义异常:404,400,500等...
			 */
			private String errorMesg=null;
			public void setT(String _t)
			{
				errorMesg=_t;
			}
			public String getT()
			{
				return errorMesg;
			}
			
			/**
			 * 获取WebApi自定义Header:errStatus，例如999
			 */
			private int errStatus=0;
			public int getErrStatus()
			{
				return errStatus;
			}
			public void setErrStatus(int errStatus)
			{
				this.errStatus = errStatus;
			}

		 };
		 
	   /**
	    * 接收异步异常
	    */
	   final SetableThrowable setablet=new SetableThrowable();
	   
	   HttpAsynUtil4 httpAsynUtil=new HttpAsynUtil4();
		byte[] mBytes = httpAsynUtil.httpRestfulByte
		(
				uri, 
				method, 
				bts, 
				Usual.mCharset_utf8,
				contextType,
				acceptType,
				headers,
				new AsyncCompletionHandler<Response>()
				{
					@Override
					public Response onCompleted(Response response) throws Exception
					{
						//如果返回StatuesCodee不为200,服务器接口api异常
						if(response.getStatusCode()!=200)
						{
							setablet.setT("访问接口异常,"+ response.getStatusCode()+"");
						}
						else
						{
							String mErrStatus=response.getHeader("errStatus");
							if(!Usual.isNullOrWhiteSpace(mErrStatus)){
								setablet.setErrStatus(Integer.parseInt(mErrStatus));
							}
						}
						return response;
					}
					@Override
					public void onThrowable(Throwable t)
					{
						setablet.setT(t.getMessage());
					}
				},
				rheaders
		);
		
		HttpResult<T> result=new HttpResult<>();
		if(!Usual.isNullOrWhiteSpace(setablet.getT()))//有WebServcie返回的服务器定义异常
		{
			result.setErrorMessage(setablet.getT());
			result.setSuccess(false);
		}
		else
		{
			if(!Usual.isNullOrZeroBytes(mBytes)){
				result = FSTUtil.decode(mBytes,defaultValue.getClass());
				mBytes=Usual.mEmptyBytes;
			}
			//判断服务器返回header:errStatus信息
			if(result.isSuccess() && setablet.getErrStatus()>0){
				result.setSuccess(false);
			}
		}
		return result;
	}
	/**
	 * 将传入参数K进行json序列化，并调用REST，返回HttpResult<T>类型数据 , 如果返回的是List，请调用
	 * {@link #httpRestJson(String, String, Object, Charset, String, String, Boolean, JavaType)} 或
	 * {@link #httpRestfulListJson(String, String, Object, boolean, Class)} 默认UTF-8编码，Content-Type/Accept
	 * application/x-fst，ssl=false
	 * 
	 * @param uri
	 * @param method
	 * @param data
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestFst(String uri, String method, DataWrapper<K> data, final HttpResult<T> defaultValue) 
	throws Exception
	{
		return httpRestFst(uri, method, data, defaultFstType, defaultFstType, defaultValue,null);
	}
	public static <T, K> HttpResult<T> httpRestFst
	(
			String uri, String method, DataWrapper<K> data, 
			final HttpResult<T> defaultValue,FluentCaseInsensitiveStringsMap headers
	) throws Exception
	{
		return httpRestFst(uri, method, data, defaultFstType, defaultFstType, defaultValue,headers);
	}
	//protostuff调用函数///////////////////////////////////////////////////////////
	/**
	 * 将传入参数K进行ProtoStuff序列化，并调用REST，返回HttpResult<T>类型数据 
	 * 默认UTF-8编码，Content-Type/Accept application/x-protostuff，ssl=false
	 * 
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param cls jackson定义的JaveType类型，用于复杂对象类型需进行JavaType的构造
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestStuff
	(
			String uri, String method, DataWrapper<K> data, 
			String contextType, String acceptType,final HttpResult<T> defaultValue,
			FluentCaseInsensitiveStringsMap headers
	) 
	throws Exception
	{
		return httpRestStuff(uri,method,data,contextType,acceptType,defaultValue,headers,null);
	}
	/**
	 * 将传入参数K进行ProtoStuff序列化，并调用REST，返回HttpResult<T>类型数据 
	 * 默认UTF-8编码，Content-Type/Accept application/x-protostuff，ssl=false
	 * 
	 * @param URI
	 * @param method
	 * @param data
	 * @param charset
	 * @param contextType
	 * @param acceptType
	 * @param isSSL
	 * @param cls jackson定义的JaveType类型，用于复杂对象类型需进行JavaType的构造
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestStuff
	(
			String uri, String method, DataWrapper<K> data, 
			String contextType, String acceptType,final HttpResult<T> defaultValue,
			FluentCaseInsensitiveStringsMap headers,HashMap<String,Object> rheaders
	) 
	throws Exception
	{
		byte[] bts = Usual.mEmptyBytes;
		if (data != null)
		{
			bts = FSTUtil.encodeStuff(data,data.getClass());
		}

		/**
		 * 接收异步异常
		 */
		class  SetableThrowable{
			/**
			 * 获取或设置服务器server自定义异常:404,400,500等...
			 */
			private String errorMesg=null;
			public void setT(String _t)
			{
				errorMesg=_t;
			}
			public String getT()
			{
				return errorMesg;
			}
			
			/**
			 * 获取WebApi自定义Header:errStatus，例如999
			 */
			private int errStatus=0;
			public int getErrStatus()
			{
				return errStatus;
			}
			public void setErrStatus(int errStatus)
			{
				this.errStatus = errStatus;
			}

		 };
		 
	   /**
	    * 接收异步异常
	    */
	   final SetableThrowable setablet=new SetableThrowable();
	   
	   HttpAsynUtil4 httpAsynUtil=new HttpAsynUtil4();
		byte[] mBytes = httpAsynUtil.httpRestfulByte
		(
				uri, 
				method, 
				bts, 
				Usual.mCharset_utf8,
				contextType,
				acceptType,
				headers,
				new AsyncCompletionHandler<Response>()
				{
					@Override
					public Response onCompleted(Response response) throws Exception
					{
						//如果返回StatuesCodee不为200,服务器接口api异常
						if(response.getStatusCode()!=200)
						{
							setablet.setT("访问接口异常,"+ response.getStatusCode()+"");
						}
						else
						{
							String mErrStatus=response.getHeader("errStatus");
							if(!Usual.isNullOrWhiteSpace(mErrStatus)){
								setablet.setErrStatus(Integer.parseInt(mErrStatus));
							}
						}
						return response;
					}
					@Override
					public void onThrowable(Throwable t)
					{
						setablet.setT(t.getMessage());
					}
				},
				rheaders
		);
		
		HttpResult<T> result=new HttpResult<>();
		if(!Usual.isNullOrWhiteSpace(setablet.getT()))//有WebServcie返回的服务器定义异常
		{
			result.setErrorMessage(setablet.getT());
			result.setSuccess(false);
		}
		else
		{
			if(!Usual.isNullOrZeroBytes(mBytes)){
				result = FSTUtil.<HttpResult<T>>decodeStuff(mBytes,defaultValue.getClass());
				mBytes=Usual.mEmptyBytes;
			}
			//判断服务器返回header:errStatus信息
			if(result.isSuccess() && setablet.getErrStatus()>0){
				result.setSuccess(false);
			}
		}
		return result;
	}
	/**
	 * 将传入参数K进行ProtoStuff序列化，并调用REST，返回HttpResult<T>类型数据 
	 * 默认UTF-8编码，Content-Type/Accept application/x-protostuff，ssl=false
	 * 
	 * @param uri
	 * @param method
	 * @param data
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static <T, K> HttpResult<T> httpRestStuff(String uri, String method, DataWrapper<K> data, final HttpResult<T> defaultValue) 
	throws Exception
	{
		return httpRestStuff(uri, method, data, defaultProtoStuffType, defaultProtoStuffType, defaultValue,null);
	}
	public static <T, K> HttpResult<T> httpRestStuff
	(
			String uri, String method, DataWrapper<K> data, 
			final HttpResult<T> defaultValue,FluentCaseInsensitiveStringsMap headers
	) throws Exception
	{
		return httpRestStuff(uri, method, data, defaultProtoStuffType, defaultProtoStuffType, defaultValue,headers);
	}
	//私有函数/////////////////////////////////////////////////////////////////////
	/**
	 * 返回指定格式ObjectMapper
	 * @param mineType
	 * @return
	 */
	private static ObjectMapper jsonMaper(String mineType){
		ObjectMapper mapper = SerializeUtils.getObjectMapper();
		if(mineType.equals(defaultJsonJSType))
		{
			mapper.setDateFormat(Usual.mfAll);
		}
		else if(mineType.equals(defaultJsonType))
		{
			mapper.setDateFormat(Usual.mfAllMS);
		}
		else
		{
			mapper.setDateFormat(Usual.mfAllMS);
		}
		return mapper;
	}
	
	private static FluentCaseInsensitiveStringsMap headers;
	/**
	 * 初始化压缩
	 */
	public static FluentCaseInsensitiveStringsMap getHeaders() {
		FluentCaseInsensitiveStringsMap headers = new FluentCaseInsensitiveStringsMap();
		// 这个等过滤器做完可以使用
		// 传入数据自动gzip后传输
		if (!headers.containsKey("Content-Encoding")) {
			headers.add("Content-Encoding", "gzip");
		}
		// 返回数据自动gzip解压缩
		if (!headers.containsKey("Accept-Encoding")) {
			headers.add("Accept-Encoding", "gzip");
		}
		// 处理在Wildfly服务器在控制台Debug提示的一个不影响使用的异常.
		if (!headers.containsKey("Connection")) {
			headers.add("Connection", "Close");
		}
		return headers;
	}
	
	public static void main(String[] args) {
		test02();
	}
	/**
	 * 调用验证码例子
	 */
	private static void test02(){
		
		HttpAsynUtil httpAsyn=new HttpAsynUtil();
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
					"text/html",
					"text/html",
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
			List<Cookie> list=(List<Cookie>)rheaders.get("cookiess");
			for(int i=0;i<list.size();i++){
				if(list.get(i).getName().equals("JSESSIONID")){
					System.out.println(list.get(i).getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Test01 
	 */
	private static void test01(){
		//模拟DataWrapper传输参数
				DataWrapper<ArrayList<HashMap<String,Object>>> mValue=new DataWrapper<>();
				ArrayList<HashMap<String,Object>> mList=new ArrayList<>();
				HashMap<String,Object> mMap=new HashMap<>();
				mMap.put("Insert", "新增测试数据");
				mList.add(mMap);
				mValue.data=mList;
				
				//正式环境使用
//				String url=SysConfig.getApiUrl()+"test/single";
				//模拟请求路径
				String url="http://test.ypjyun.com:4447/auap-biz-web/mapi/test/single";
				
				//建立返回对象
				HttpResult<ArrayList<HashMap<String,Object>>> defaultValue=new HttpResult<>();
				try {
					defaultValue=HttpAsyn.httpRestFst(url,HttpMethod.POST, mValue,defaultValue,getHeaders());
					String xx="";
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
}
