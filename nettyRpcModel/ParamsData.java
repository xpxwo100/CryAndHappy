package cn.com.aperfect.auap.external.nettyRpcModel;

import java.io.Serializable;

public class ParamsData implements Serializable {
	private static final long serialVersionUID = 2015534444051785L;
	  public String beanName; // required
	  public String methodName; // required
	  public Object params;
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object getParams() {
		return params;
	}
	public void setParams(Object params) {
		this.params = params;
	}
	
	
}
