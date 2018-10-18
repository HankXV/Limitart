package top.limitart.rpc;

import java.io.IOException;

/**
 * @author hank
 * @version 2018/10/18 0018 22:12
 */
public class RPCDemo {
    public static void main(String[] args) throws ReflectiveOperationException, IOException, PRCServiceProxyException {
        AbstractRPCConsumer client = new AbstractRPCConsumer() {
            @Override
            protected boolean checkParamType(Class<?> paramType) {
                return true;
            }
        };
        client.loadPackage(new String[]{""});
    }
}
