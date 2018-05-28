namespace java com.service.rpcthrift
service RemoteProxyByThrift{
    ReSult load(1:DataWrapper dataWrapper)
}


struct DataWrapper {
	1:  string beanName 
	2:  string methodName
	3:  map<string,list<byte>> paramsMap
	4:  list<byte> byteParamsList
	5:  binary params
}

struct ReSult {
	1: bool isSuccess
	2: string errorMessage
	3: map<string,list<byte>> relData
	4: list<byte> byteRelDataList
	5: binary value
}