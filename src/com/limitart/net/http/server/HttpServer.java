package com.limitart.net.http.server;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.collections.ConstraintMap;
import com.limitart.net.http.codec.QueryStringDecoderV2;
import com.limitart.net.http.constant.QueryMethod;
import com.limitart.net.http.constant.RequestErrorCode;
import com.limitart.net.http.handler.HttpHandler;
import com.limitart.net.http.message.UrlMessage;
import com.limitart.net.http.message.UrlMessageFactory;
import com.limitart.net.http.server.config.HttpServerConfig;
import com.limitart.net.http.server.event.HttpServerEventListener;
import com.limitart.net.http.server.filter.HttpObjectAggregatorCustom;
import com.limitart.util.HttpUtil;
import com.limitart.util.StringUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Http服务器
 * 
 * @author Hank
 *
 */
@Sharable
public class HttpServer extends SimpleChannelInboundHandler<FullHttpRequest> {
	private static Logger log = LogManager.getLogger();
	private static EventLoopGroup bossGroup;
	private static EventLoopGroup workerGroup;
	private HttpServerEventListener serverEventListener;
	private UrlMessageFactory urlFactory;
	private HttpServerConfig config;
	private Channel channel;
	static {
		if (Epoll.isAvailable()) {
			bossGroup = new EpollEventLoopGroup();
			workerGroup = new EpollEventLoopGroup();
		} else {
			bossGroup = new NioEventLoopGroup();
			workerGroup = new NioEventLoopGroup();
		}
	}

	public HttpServer(HttpServerEventListener serverEventListener, HttpServerConfig config,
			UrlMessageFactory urlFactory) {
		if (serverEventListener == null || config == null || urlFactory == null) {
			throw new NullPointerException("init error!");
		}
		this.serverEventListener = serverEventListener;
		this.config = config;
		this.urlFactory = urlFactory;
	}

	public HttpServer bind() {
		try {
			ServerBootstrap boot = new ServerBootstrap();
			if (Epoll.isAvailable()) {
				boot.channel(EpollServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024);
				log.info(this.config.getServerName() + " epoll init");
			} else {
				boot.channel(NioServerSocketChannel.class);
				log.info(this.config.getServerName() + " nio init");
			}
			boot.group(bossGroup, workerGroup).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new HttpRequestDecoder())
									.addLast(new HttpObjectAggregatorCustom(config.getHttpObjectAggregatorMax(),
											serverEventListener))
									.addLast(new HttpContentCompressor()).addLast(new HttpContentDecompressor())
									.addLast(new HttpResponseEncoder()).addLast(new ChunkedWriteHandler())
									.addLast(HttpServer.this);
						}
					}).bind(this.config.getPort()).addListener(new ChannelFutureListener() {

						@Override
						public void operationComplete(ChannelFuture channelFuture) throws Exception {
							if (channelFuture.isSuccess()) {
								channel = channelFuture.channel();
								log.info(config.getServerName() + " bind at port:" + config.getPort());
								HttpServer.this.serverEventListener.onServerBind(channel);
							}
						}
					}).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error(e, e);
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		return this;
	}

	public HttpServer stop() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		if (channel != null) {
			channel.close();
			channel = null;
		}
		return this;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.serverEventListener.onChannelActive(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.serverEventListener.onChannelInactive(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		this.serverEventListener.onExceptionCaught(ctx.channel(), cause);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		this.serverEventListener.onChannelRegistered(ctx.channel());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		this.serverEventListener.onChannelUnregistered(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		if (!msg.decoderResult().isSuccess()) {
			HttpUtil.sendResponseError(ctx.channel(), msg, RequestErrorCode.ERROR_DECODE_FAIL);
			return;
		}
		if (StringUtil.isEmptyOrNull(msg.uri())) {
			HttpUtil.sendResponseError(ctx.channel(), msg, RequestErrorCode.ERROR_URL_EMPTY);
			return;
		}
		String url = null;
		ConstraintMap<String> params = new ConstraintMap<String>();
		if (msg.method() == GET) {
			QueryStringDecoderV2 queryStringDecoder = new QueryStringDecoderV2(msg.uri());
			url = queryStringDecoder.path();
			params = queryStringDecoder.parameters();
		} else if (msg.method() == POST) {
			url = msg.uri();
		} else {
			HttpUtil.sendResponseError(ctx.channel(), msg, RequestErrorCode.ERROR_METHOD_FORBBIDEN);
			return;
		}
		if (url.equals("/2016info")) {
			HttpUtil.sendResponse(ctx.channel(), HttpResponseStatus.OK, "hello~stupid!", true);
			return;
		}
		UrlMessage<String> message = this.urlFactory.getMessage(url);
		if (message == null) {
			log.error("消息不存在:" + url);
			HttpUtil.sendResponseError(ctx.channel(), msg, RequestErrorCode.ERROR_URL_FORBBIDEN);
			return;
		}
		if (message.getMethod() == null) {
			HttpUtil.sendResponseError(ctx.channel(), msg, RequestErrorCode.ERROR_METHOD_FORBBIDEN);
			return;
		}
		// 如果为POST，那么只能POST,如果是Get，那么都可以
		if (message.getMethod() == QueryMethod.POST && msg.method() != POST) {
			HttpUtil.sendResponseError(ctx.channel(), msg, RequestErrorCode.ERROR_METHOD_ERROR);
			return;
		}
		HttpHandler handler = this.urlFactory.getHandler(url);
		if (handler == null) {
			HttpUtil.sendResponseError(ctx.channel(), msg, RequestErrorCode.ERROR_URL_FORBBIDEN);
			return;
		}
		message.setChannel(ctx.channel());
		message.setRequest(msg);
		// 如果是POST，最后再来解析参数
		if (msg.method() == POST) {
			try {
				HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(msg);
				List<InterfaceHttpData> postData = postDecoder.getBodyHttpDatas();
				for (InterfaceHttpData data : postData) {
					if (data instanceof Attribute) {
						Attribute at = (Attribute) data;
						String name = at.getName();
						String value = at.getValue();
						params.putObject(name, value);
					} else if (data instanceof FileUpload) {
						FileUpload fileUpload = (FileUpload) data;
						int readableBytes = fileUpload.content().readableBytes();
						// 没内容的文件GG掉
						if (readableBytes > 0) {
							String name = fileUpload.getFilename();
							byte[] file = fileUpload.content().array();
							message.getFiles().put(name, file);
						}
					} else {
					}
				}
			} catch (Exception e) {
				log.error(e, e);
				HttpUtil.sendResponseError(ctx.channel(), msg, RequestErrorCode.ERROR_POST_ERROR);
				return;
			}
		}
		try {
			message.readMessage(params);
		} catch (Exception e) {
			HttpUtil.sendResponseError(ctx.channel(), msg, RequestErrorCode.ERROR_MESSAGE_PARSE, e.getMessage());
			return;
		}
		this.serverEventListener.dispatchMessage(message, handler, params);
	}
}
