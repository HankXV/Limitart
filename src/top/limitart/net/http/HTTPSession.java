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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import top.limitart.net.NettySession;

/**
 * Created by Hank on 2018/10/14
 */
public class HTTPSession extends NettySession<HttpMessage> {
    public HTTPSession(Channel channel) {
        super(channel);
    }

    public void response(HttpResponseStatus resultCode, String result, boolean isClose) {
        response(resultCode, ContentTypes.text_plain, result.getBytes(CharsetUtil.UTF_8), isClose);
    }

    public void response(HttpResponseStatus resultCode, ContentTypes contentType,
                         byte[] bytes, boolean isClose) {
        response(resultCode, contentType, Unpooled.wrappedBuffer(bytes).retain(), isClose);
    }

    public void response(HttpResponseStatus resultCode, ContentTypes contentType,
                         ByteBuf result, boolean isClose) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, resultCode, result);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, contentType.getValue());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes() + "");
        writeNow(response, (b, t) -> {
            if (isClose) {
                close();
            }
        });
    }

    public void response(HttpResponseStatus resultCode, String result) {
        response(resultCode, result, true);
    }
}
