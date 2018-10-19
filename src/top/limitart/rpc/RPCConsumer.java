package top.limitart.rpc;

import top.limitart.net.AddressPair;

import java.io.IOException;

/**
 * @author hank
 * @version 2018/10/19 0019 16:54
 */
public class RPCConsumer extends AbstractRPCConsumer {
    private RPCZKClient client;

    public RPCConsumer(boolean directOrCenter, AddressPair host, String... packages) throws IOException, ReflectiveOperationException, PRCServiceProxyException {
        super(packages);
    }

    @Override
    protected void sendRPCRequest(RPCRequest request) {

    }

    @Override
    protected boolean canTransferedType(Class<?> paramType) {
        return true;
    }
}
