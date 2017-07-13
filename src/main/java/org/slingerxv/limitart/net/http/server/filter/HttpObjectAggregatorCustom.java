package org.slingerxv.limitart.net.http.server.filter;

import org.slingerxv.limitart.net.http.server.event.HttpServerEventListener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * Http消息聚合器
 * 
 * @author hank
 *
 */
public class HttpObjectAggregatorCustom extends HttpObjectAggregator {
	private HttpServerEventListener listener;

	public HttpObjectAggregatorCustom(int maxContentLength, HttpServerEventListener listener) {
		super(maxContentLength);
		this.listener = listener;
	}

	@Override
	protected void handleOversizedMessage(ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
		super.handleOversizedMessage(ctx, oversized);
		// 客户端发送的消息超标了，就会走到这
		listener.onMessageOverSize(ctx.channel(), oversized);
	}
}
