package cn.com.aperfect.auap.external.nettyClient;

import cn.com.aperfect.auap.external.nettyRpcModel.ParamsData;
import cn.com.aperfect.auap.external.nettyRpcModel.RpcResponse;


public interface NettyRpcLoad {
	public RpcResponse load(ParamsData paramsData);
}
