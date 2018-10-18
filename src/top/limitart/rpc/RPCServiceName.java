package top.limitart.rpc;

import java.util.Objects;

/**
 * RPC服务全名称
 *
 * @author hank
 * @version 2018/10/18 0018 20:47
 */
public class RPCServiceName {
    //提供商名称(可理解未命名空间)
    private String providerName;
    //模块名称(可理解为类名)
    private String moduleName;
    //方法名称(可理解为类里的方法)
    private String methodName;
    //模块的版本(客户端和服务器版本不同不能调用)
    private int version;

    public RPCServiceName(String providerName, String moduleName, String methodName, int version) {
        this.providerName = providerName;
        this.moduleName = moduleName;
        this.methodName = methodName;
        this.version = version;
    }

    @Override
    public String toString() {
        return "/" + providerName + "/" + moduleName + "/" + methodName + "/" + version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RPCServiceName that = (RPCServiceName) o;
        return version == that.version &&
                Objects.equals(providerName, that.providerName) &&
                Objects.equals(moduleName, that.moduleName) &&
                Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerName, moduleName, methodName, version);
    }
}
