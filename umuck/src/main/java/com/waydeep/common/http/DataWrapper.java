package com.waydeep.common.http;

import java.io.Serializable;

/**
 * 数据包装主要为了支持protobuf的复杂对象包装
 * 
 * @param <T>
 */
@SuppressWarnings("serial")
public class DataWrapper<T> implements Serializable {
	
	/**
	 * 构造函数
	 */
	public DataWrapper(){
		
	}
	/**
	 * 构造函数
	 * @param inData
	 */
	public DataWrapper(T inData){
		data=inData;
	}
	
	public T data;
}
