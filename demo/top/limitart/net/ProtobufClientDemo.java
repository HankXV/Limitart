package top.limitart.net;

import com.google.protobuf.Message;
import top.limitart.mapping.Router;
import top.limitart.net.binary.BinaryMessage;
import top.limitart.net.binary.BinaryRequestParam;
import top.limitart.net.protobuf.ProtobufEndPoint;
import top.limitart.net.protobuf.ProtobufRequestParam;

/**
 * @author hank
 * @version 2018/10/12 0012 21:29
 */
public class ProtobufClientDemo {
    public static void main(String[] args) throws Exception {
        ProtobufEndPoint.builder(false)
                .router(Router.empty(Message.class, ProtobufRequestParam.class).registerMapperClass(MessageMapper.class)).onConnected((s, state) -> {
            if (state) {
                try {
                    s.writeNow(ProtobufMessageDemo.Demo2.newBuilder().setId(1111).build());
                } catch (Exception ignored) {
                }
            }
        }).build().start(AddressPair.withIP("127.0.0.1", 7878));
    }
}
