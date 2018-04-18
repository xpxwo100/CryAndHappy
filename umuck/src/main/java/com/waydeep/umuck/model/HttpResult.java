package com.waydeep.umuck.model;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * http请求结果对象
 */
@SuppressWarnings("serial")
public class HttpResult<T> implements Serializable {

	public HttpResult() {
	}

	/**
	 * 创建一个返回值，建议改用该方法进行返回值的初始化
	 * 仅提供给WebService Api使用
	 * @param isSuccess
	 * @param result
	 * @return
	 */
	public static <T> HttpResult<T> newResult(Boolean isSuccess, T result, HttpServletResponse response) {
		return HttpResult.newResult(isSuccess, result, null, response);
	}

	/**
	 * 创建一个返回值，建议改用该方法进行返回值的初始化
	 * 仅提供给WebService Api使用
	 * @param isSuccess
	 * @param result
	 * @return
	 */
	public static <T> HttpResult<T> newResult(Boolean isSuccess, T result, String errorMessage, HttpServletResponse response) {
		HttpResult<T> httpResult = new HttpResult<T>();
		httpResult.result = result;
		httpResult.isSuccess = isSuccess;
		httpResult.errorMessage = errorMessage;
//		if (!isSuccess) {
//			//通过自定义返回header标识服务器是否处理成功
//			response.setHeader("errStatus", "999");
//		}
		return httpResult;
	}

	/**
	 * 返回结果
	 */
	private T result;

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	/**
	 * 调用是否成功
	 */
	private boolean isSuccess;

	/**
	 * 调用失败后的错误信息，如果调用成功，该值为空，但并不表示为空的时候调用成功 调用是否成功请根据{@link #isSuccess}进行判断
	 */
	private String errorMessage;
	/**
	 * 调用成功后需要传递的状态编码
	 * 999:自定义sso过滤器认证失败编码
	 */
	private int stateCode;
	/**
	 * 调用成功后需要传递的消息信息
	 */
	public String stateMessage;
	public int httpCode;
	public String httpMessage;
	
	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getStateCode() {
		return stateCode;
	}

	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}

	public String getStateMessage() {
		return stateMessage;
	}

	public void setStateMessage(String stateMessage) {
		this.stateMessage = stateMessage;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public String getHttpMessage() {
		return httpMessage;
	}

	public void setHttpMessage(String httpMessage) {
		this.httpMessage = httpMessage;
	}
	
	
}
