package top.limitart.rpc;


import java.util.Arrays;

/**
 * @author hank
 * @version 2018/10/18 0018 22:12
 */
public class RPCDemo {
    public static void main(String[] args) throws Exception {
        AbstractRPCConsumer client = new AbstractRPCConsumer() {
            @Override
            protected void sendRPCRequest(RPCRequest request) {
                triggerResponse(request.getRequestID(), 0, 2);
            }

            @Override
            protected boolean canTransferedType(Class<?> paramType) {
                return true;
            }
        };
        HelloRPC proxy = client.createProxy(HelloRPC.class);
        System.out.println(proxy.add(1, 2));
        AbstractRPCProvider server = new AbstractRPCProvider() {
            @Override
            protected boolean canTransferedType(Class<?> returnType) {
                return true;
            }
        };
        server.executeRPC(1, new RPCServiceName(new RPCModuleName("limitart", "HelloRPC"), "add(int,int)", 1), Arrays.asList(1, 2), r -> System.out.println(r.getReturnVal()));
    }
}
