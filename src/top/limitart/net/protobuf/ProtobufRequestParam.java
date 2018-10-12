package top.limitart.net.protobuf;

import com.google.protobuf.Message;
import io.netty.channel.EventLoop;
import top.limitart.base.Conditions;
import top.limitart.base.NotNull;
import top.limitart.mapping.RequestContext;
import top.limitart.net.Session;

/**
 * Protobuf端点路由参数
 *
 * @author hank
 * @version 2018/10/12 0012 21:05
 */
public class ProtobufRequestParam extends RequestContext<Message> {
    private final Session<Message, EventLoop> session;
    private Object extra;

    public ProtobufRequestParam(@NotNull Session session, @NotNull Message msg) {
        super(msg);
        Conditions.notNull(session, "session");
        Conditions.notNull(msg, "msg");
        this.session = session;
    }


    public @NotNull
    Session<Message, EventLoop> session() {
        return this.session;
    }

    /**
     * @return the extra
     */
    public Object extra() {
        return extra;
    }

    /**
     * @param extra the extra to set
     */
    public void extra(Object extra) {
        this.extra = extra;
    }
}
