/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.com.aperfect.auap.external.nettyRpcModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:RpcSerialize.java
 * @description:RpcSerialize功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public interface RpcSerialize {

    void serialize(OutputStream output, Object object) throws IOException;

    Object deserialize(InputStream input) throws IOException;
}

