package top.limitart.net.flashssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.*;
import top.limitart.net.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Flash Socket安全通信确认
 *
 * @author hank
 * @version 2018/10/15 0015 21:14
 */
public class FlashSSLEndPoint extends NettyEndPoint<ByteBuf, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlashSSLEndPoint.class);
    private static final byte[] CROSS_DOMAIN =
            "<?xml version=\"1.0\"?><cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>\0"
                    .getBytes(StandardCharsets.UTF_8);
    private Proc2<Session<byte[], EventLoop>, Boolean> onConnected;
    private Proc1<Session<byte[], EventLoop>> onBind;
    private Proc2<Session<byte[], EventLoop>, Throwable> onExceptionThrown;

    public static FlashSSLEndPoint.Builder builder() {
        return new FlashSSLEndPoint.Builder();
    }

    public FlashSSLEndPoint(FlashSSLEndPoint.Builder builder) {
        super(builder.name, NettyEndPointType.SERVER_REMOTE, 0);
        this.onConnected = builder.onConnected;
        this.onBind = builder.onBind;
        this.onExceptionThrown = builder.onExceptionThrown;
    }

    @Override
    protected void beforeTranslatorPipeline(ChannelPipeline pipeline) {

    }

    @Override
    protected void afterTranslatorPipeline(ChannelPipeline pipeline) {

    }

    @Override
    protected void exceptionThrown(Session<byte[], EventLoop> session, Throwable cause) throws Exception {

    }

    @Override
    protected void sessionActive(Session<byte[], EventLoop> session, boolean activeOrNot) throws Exception {

    }

    @Override
    protected void messageReceived(Session<byte[], EventLoop> session, Object msg) throws Exception {
        session.writeNow(CROSS_DOMAIN, (b, t) -> session.close());
    }

    @Override
    public EndPoint start(AddressPair addressPair, Proc3<Session<byte[], EventLoop>, Boolean, Throwable> listener) {
        super.start(addressPair, (s, b, t) -> {
            Procs.invoke(listener, s, b, t);
            if (!b) {
                LOGGER.error("flash ssl port occupied,retry after 1 minute...");
                bossGroup.schedule(() -> start(addressPair, listener), 1, TimeUnit.MINUTES);
            }
        });
        return this;
    }

    @Override
    public byte[] toOutputFinal(ByteBuf byteBuf) throws Exception {
        byte[] b = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(b);
        return b;
    }

    @Override
    public ByteBuf toInputFinal(byte[] byteBuf) throws Exception {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(byteBuf);
        return buffer;
    }

    public static class Builder {
        private String name;
        private Proc2<Session<byte[], EventLoop>, Boolean> onConnected;
        private Proc1<Session<byte[], EventLoop>> onBind;
        private Proc2<Session<byte[], EventLoop>, Throwable> onExceptionThrown;

        public Builder() {
            this.name = "Limitart-Flash-SSL";
        }

        /**
         * 构建服务器
         *
         * @return
         * @throws Exception
         */
        public FlashSSLEndPoint build() {
            return new FlashSSLEndPoint(this);
        }


        /**
         * 名称
         *
         * @param name
         * @return
         */
        @Optional
        public FlashSSLEndPoint.Builder name(String name) {
            this.name = name;
            return this;
        }


        /**
         * 链接创建处理
         *
         * @param onConnected
         * @return
         */
        @Optional
        public FlashSSLEndPoint.Builder onConnected(Proc2<Session<byte[], EventLoop>, Boolean> onConnected) {
            this.onConnected = onConnected;
            return this;
        }

        /**
         * 服务器绑定处理
         *
         * @param onBind
         * @return
         */
        @Optional
        public FlashSSLEndPoint.Builder onBind(Proc1<Session<byte[], EventLoop>> onBind) {
            this.onBind = onBind;
            return this;
        }


        /**
         * 服务器抛异常处理
         *
         * @param onExceptionThrown
         * @return
         */
        @Optional
        public FlashSSLEndPoint.Builder onExceptionThrown(Proc2<Session<byte[], EventLoop>, Throwable> onExceptionThrown) {
            this.onExceptionThrown = onExceptionThrown;
            return this;
        }

    }
}
