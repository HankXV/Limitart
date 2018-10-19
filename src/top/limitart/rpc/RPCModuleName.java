/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.limitart.rpc;

import java.util.Objects;

/**
 * RPC服务全名称
 *
 * @author hank
 * @version 2018/10/18 0018 20:47
 */
public class RPCModuleName {
    //提供商名称(可理解未命名空间)
    private String providerName;
    //模块名称(可理解为类名)
    private String moduleName;

    public RPCModuleName(String providerName, Class<?> clzz) {
        this(providerName, clzz.getSimpleName());
    }

    public RPCModuleName(String providerName, String moduleName) {
        this.providerName = providerName;
        this.moduleName = moduleName;
    }

    @Override
    public String toString() {
        return "/" + providerName + "/" + moduleName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        RPCModuleName that = (RPCModuleName) object;
        return Objects.equals(providerName, that.providerName) &&
                Objects.equals(moduleName, that.moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerName, moduleName);
    }
}
