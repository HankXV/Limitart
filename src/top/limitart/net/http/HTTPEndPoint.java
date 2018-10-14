/*
 *
 *  * Copyright (c) 2016-present The Limitart Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package top.limitart.net.http;

import com.sun.javafx.collections.MappingChange;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.*;
import top.limitart.net.NettyEndPoint;
import top.limitart.net.Session;
import top.limitart.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;


/**
 * HTTP端点实现
 * Created by Hank on 2018/10/13
 */
public class HTTPEndPoint extends NettyEndPoint<HttpMessage, HttpMessage> {
    private static Logger LOGGER = LoggerFactory.getLogger(HTTPEndPoint.class);
    private static int MESSAGE_MAX_SIZE = 1024 * 1024;
    private Proc2<Session<HttpMessage, EventLoop>, HttpMessage> onMessageOverSize;
    private Func2<Session<HttpMessage, EventLoop>, HTTPRequest, byte[]> onMessageIn;
    private Proc2<Session<HttpMessage, EventLoop>, Boolean> onConnected;
    private Proc1<Session<HttpMessage, EventLoop>> onBind;
    private Proc2<Session<HttpMessage, EventLoop>, Throwable> onExceptionThrown;

    public static Builder builder() {
        return new Builder();
    }

    public HTTPEndPoint(Builder builder) {
        super(builder.name, true, 0);
        this.onMessageOverSize = builder.onMessageOverSize;
        this.onMessageIn = builder.onMessageIn;
        this.onConnected = builder.onConnected;
        this.onBind = builder.onBind;
        this.onExceptionThrown = builder.onExceptionThrown;
    }

    @Override
    protected Session<HttpMessage, EventLoop> createSession(Channel channel) {
        return new HTTPSession(channel);
    }

    @Override
    protected void beforeTranslatorPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec()).addLast(new HttpObjectAggregator(MESSAGE_MAX_SIZE) {
            @Override
            protected void handleOversizedMessage(ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
                LOGGER.error("{} message oversize :{},max :{}", ctx.channel(), oversized, MESSAGE_MAX_SIZE);
                Procs.invoke(onMessageOverSize, getSession(ctx.channel()), oversized);
            }
        }).addLast(new HttpContentCompressor());
    }

    @Override
    protected void afterTranslatorPipeline(ChannelPipeline pipeline) {
    }

    @Override
    protected void exceptionThrown(Session<HttpMessage, EventLoop> session, Throwable cause) throws Exception {
        Procs.invoke(onExceptionThrown, session, cause);
    }

    @Override
    protected void sessionActive(Session<HttpMessage, EventLoop> session, boolean activeOrNot) throws Exception {
        Procs.invoke(onConnected, session, activeOrNot);
    }

    @Override
    protected void messageReceived(Session<HttpMessage, EventLoop> s, Object arg) throws Exception {
        HTTPSession session = (HTTPSession) s;
        FullHttpRequest msg = (FullHttpRequest) arg;
        if (!msg.decoderResult().isSuccess()) {
            session.response(HttpResponseStatus.BAD_REQUEST, "bad request");
            return;
        }
        String url;
        Map<String, String> params = new HashMap<>();
        if (msg.method() == GET) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(msg.uri());
            url = queryStringDecoder.path();
            params.putAll(queryStringDecoder.parameters().entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().get(0))));
        } else if (msg.method() == POST) {
            url = msg.uri();
        } else {
            session.response(HttpResponseStatus.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        if ("/2016info".equals(url)) {
            session.response(HttpResponseStatus.OK, "hello~stupid!");
            return;
        }
        // 如果是POST，最后再来解析参数
        if (msg.method() == POST) {
            try {
                HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(msg);
                List<InterfaceHttpData> postData = postDecoder.getBodyHttpDatas();
                params = new HashMap<>();
                for (InterfaceHttpData data : postData) {
                    if (data instanceof Attribute) {
                        Attribute at = (Attribute) data;
                        String name = at.getName();
                        String value = at.getValue();
                        params.put(name, value);
                    } else if (data instanceof FileUpload) {
//                        FileUpload fileUpload = (FileUpload) data;
//                        int readableBytes = fileUpload.content().readableBytes();
//                        // 没内容的文件GG掉
//                        if (readableBytes > 0) {
//                            String name = fileUpload.getFilename();
//                            byte[] file = new byte[readableBytes];
//                            fileUpload.content().readBytes(file);
//                            message.getFiles().put(name, file);
//                        }
                        //不支持上传文件
                        session.response(HttpResponseStatus.FORBIDDEN, "file upload not allowed");
                        return;
                    }
                }
            } catch (Exception e) {
                LOGGER.error("decode http request error", e);
                session.response(HttpResponseStatus.INTERNAL_SERVER_ERROR, "decode error", true);
                return;
            }
        }
        HTTPRequest request = new HTTPRequest(msg.method(), url, params);
        if (onMessageIn != null) {
            byte[] run = onMessageIn.run(session, request);
            if (run == null) {
                session.response(HttpResponseStatus.NOT_FOUND, "no handler", true);
            } else {
                session.response(HttpResponseStatus.OK, ContentTypes.text_plain, run, true);
            }
        } else {
            session.response(HttpResponseStatus.NOT_FOUND, "no handler", true);
        }
    }

    @Override
    protected void onBind(Session<HttpMessage, EventLoop> session) {
        Procs.invoke(onBind, session);
    }


    @Override
    public HttpMessage toOutputFinal(HttpMessage message) throws Exception {
        return message;
    }

    @Override
    public HttpMessage toInputFinal(HttpMessage message) throws Exception {
        return message;
    }


    public static class Builder {
        private String name;
        private Proc2<Session<HttpMessage, EventLoop>, HttpMessage> onMessageOverSize;
        private Func2<Session<HttpMessage, EventLoop>, HTTPRequest, byte[]> onMessageIn;
        private Proc2<Session<HttpMessage, EventLoop>, Boolean> onConnected;
        private Proc1<Session<HttpMessage, EventLoop>> onBind;
        private Proc2<Session<HttpMessage, EventLoop>, Throwable> onExceptionThrown;

        public Builder() {
            this.name = "Limitart-HTTP";
        }

        /**
         * 构建服务器
         *
         * @return
         * @throws Exception
         */
        public HTTPEndPoint build() {
            return new HTTPEndPoint(this);
        }


        /**
         * 名称
         *
         * @param name
         * @return
         */
        @Optional
        public HTTPEndPoint.Builder name(String name) {
            this.name = name;
            return this;
        }


        /**
         * 消息接收处理
         *
         * @param onMessageIn
         * @return
         */
        @Optional
        public HTTPEndPoint.Builder onMessageIn(Func2<Session<HttpMessage, EventLoop>, HTTPRequest, byte[]> onMessageIn) {
            this.onMessageIn = onMessageIn;
            return this;
        }

        /**
         * 链接创建处理
         *
         * @param onConnected
         * @return
         */
        @Optional
        public HTTPEndPoint.Builder onConnected(Proc2<Session<HttpMessage, EventLoop>, Boolean> onConnected) {
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
        public HTTPEndPoint.Builder onBind(Proc1<Session<HttpMessage, EventLoop>> onBind) {
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
        public HTTPEndPoint.Builder onExceptionThrown(Proc2<Session<HttpMessage, EventLoop>, Throwable> onExceptionThrown) {
            this.onExceptionThrown = onExceptionThrown;
            return this;
        }

        @Optional
        public HTTPEndPoint.Builder onMessageOverSize(Proc2<Session<HttpMessage, EventLoop>, HttpMessage> onMessageOverSize) {
            this.onMessageOverSize = onMessageOverSize;
            return this;
        }
    }
}
