package top.limitart.rpc;

/**
 * @author hank
 * @version 2018/10/18 0018 22:11
 */
@RPCService("limitart")
public class HelloRPCImpl implements HelloRPC {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
