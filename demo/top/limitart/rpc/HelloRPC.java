package top.limitart.rpc;

/**
 * @author hank
 * @version 2018/10/18 0018 22:10
 */
@RPCService("limitart")
public interface HelloRPC {

    int add(int a, int b) throws Exception;
}
