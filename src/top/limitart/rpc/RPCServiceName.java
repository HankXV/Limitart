package top.limitart.rpc;

import java.util.Objects;

/**
 * RPC服务全名称
 *
 * @author hank
 * @version 2018/10/18 0018 20:47
 */
public class RPCServiceName {
    private String providerName;
    private String methodName;
    private int version;

    public RPCServiceName(String providerName, String methodName, int version) {
        this.providerName = providerName;
        this.methodName = methodName;
        this.version = version;
    }

    @Override
    public String toString() {
        return "/" + providerName + "/" + methodName + "/" + version;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        RPCServiceName that = (RPCServiceName) object;
        return version == that.version &&
                Objects.equals(providerName, that.providerName) &&
                Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerName, methodName, version);
    }
}
