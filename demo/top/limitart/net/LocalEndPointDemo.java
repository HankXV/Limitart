package top.limitart.net;

import top.limitart.mapping.Router;
import top.limitart.net.binary.BinaryEndPoint;
import top.limitart.net.binary.BinaryMessage;
import top.limitart.net.binary.BinaryRequestParam;

/**
 * @author hank
 * @version 2018/10/15 0015 14:14
 */
public class LocalEndPointDemo {

    public static void main(String[] args)
            throws Exception {
        //server
        BinaryEndPoint.builder(NettyEndPointType.SERVER_LOCAL)
                .router(Router.empty(BinaryMessage.class, BinaryRequestParam.class).registerMapperClass(MessageMapper.class)).build().start(AddressPair.withLocalHost(1));
        //client
        BinaryEndPoint.builder(NettyEndPointType.CLIENT_LOCAL)
                .router(Router.empty(BinaryMessage.class, BinaryRequestParam.class).registerMapperClass(MessageMapper.class)).onConnected((s, state) -> {
            if (state) {
                try {
                    s.writeNow(new BinaryMessageDemo());
                } catch (Exception ignored) {
                }
            }
        }).build().start(AddressPair.withLocalHost(1));
    }
}
