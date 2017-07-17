package org.slingerxv.limitart.net.http.server;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.collections.ConstraintMap;
import org.slingerxv.limitart.net.define.AbstractNettyServer;
import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.net.http.codec.QueryStringDecoderV2;
import org.slingerxv.limitart.net.http.constant.QueryMethod;
import org.slingerxv.limitart.net.http.constant.RequestErrorCode;
import org.slingerxv.limitart.net.http.handler.HttpHandler;
import org.slingerxv.limitart.net.http.message.UrlMessage;
import org.slingerxv.limitart.net.http.server.config.HttpServerConfig;
import org.slingerxv.limitart.net.http.server.event.HttpServerEventListener;
import org.slingerxv.limitart.net.http.server.filter.HttpObjectAggregatorCustom;
import org.slingerxv.limitart.net.http.util.HttpUtil;
import org.slingerxv.limitart.util.StringUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
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
public class HttpServer extends AbstractNettyServer implements IServer {
	private static Logger log = LogManager.getLogger();
	private ServerBootstrap boot;
	private HttpServerEventListener serverEventListener;
	private HttpServerConfig config;
	private Channel channel;

	public HttpServer(HttpServerConfig config, HttpServerEventListener serverEventListener) {
		if (serverEventListener == null || config == null) {
			throw new NullPointerException("init error!");
		}
		this.serverEventListener = serverEventListener;
		this.config = config;
		boot = new ServerBootstrap();
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
								.addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {

									@Override
									protected void channelRead0(ChannelHandlerContext arg0, FullHttpRequest arg1)
											throws Exception {
										channelRead00(arg0, arg1);
									}

									@Override
									public void channelActive(ChannelHandlerContext ctx) throws Exception {
										channelActive0(ctx);
									}

									@Override
									public void channelInactive(ChannelHandlerContext ctx) throws Exception {
										channelInactive0(ctx);
									}

									@Override
									public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
											throws Exception {
										exceptionCaught0(ctx, cause);
									}
								});
					}
				});
	}

	@Override
	public void startServer() {
		new Thread(() -> {
			try {
				boot.bind(this.config.getPort()).addListener((ChannelFutureListener) channelFuture -> {
					if (channelFuture.isSuccess()) {
						channel = channelFuture.channel();
						log.info(config.getServerName() + " bind at port:" + config.getPort());
						HttpServer.this.serverEventListener.onServerBind(channel);
					}
				}).sync().channel().closeFuture().sync();
			} catch (InterruptedException e) {
				log.error(e, e);
			}
		}, config.getServerName() + "-Binder").start();
	}

	@Override
	public void stopServer() {
		if (channel != null) {
			channel.close();
			channel = null;
		}
	}

	private void channelActive0(ChannelHandlerContext ctx) throws Exception {
		HashSet<String> whiteList = config.getWhiteList();
		if (whiteList != null && !config.getWhiteList().isEmpty()) {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			String remoteAddress = insocket.getAddress().getHostAddress();
			if (!whiteList.contains(remoteAddress)) {
				ctx.channel().close();
				log.error("ip: " + remoteAddress + " rejected link!");
				return;
			}
		}
		this.serverEventListener.onChannelStateChanged(ctx.channel(), true);
	}

	private void channelInactive0(ChannelHandlerContext ctx) throws Exception {
		this.serverEventListener.onChannelStateChanged(ctx.channel(), false);
	}

	private void exceptionCaught0(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		this.serverEventListener.onExceptionCaught(ctx.channel(), cause);
	}

	private void channelRead00(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		if (!msg.decoderResult().isSuccess()) {
			HttpUtil.sendResponseError(ctx.channel(), RequestErrorCode.ERROR_DECODE_FAIL);
			return;
		}
		if (StringUtil.isEmptyOrNull(msg.uri())) {
			HttpUtil.sendResponseError(ctx.channel(), RequestErrorCode.ERROR_URL_EMPTY);
			return;
		}
		String url;
		ConstraintMap<String> params = new ConstraintMap<>();
		if (msg.method() == GET) {
			QueryStringDecoderV2 queryStringDecoder = new QueryStringDecoderV2(msg.uri());
			url = queryStringDecoder.path();
			params = queryStringDecoder.parameters();
		} else if (msg.method() == POST) {
			url = msg.uri();
		} else {
			HttpUtil.sendResponseError(ctx.channel(), RequestErrorCode.ERROR_METHOD_FORBBIDEN);
			return;
		}
		if (url.equals("/2016info")) {
			HttpUtil.sendResponse(ctx.channel(), HttpResponseStatus.OK, "hello~stupid!", true);
			return;
		}
		UrlMessage message = this.config.getFacotry().getMessage(url);
		if (message == null) {
			log.error("消息不存在:" + url);
			HttpUtil.sendResponseError(ctx.channel(), RequestErrorCode.ERROR_URL_FORBBIDEN);
			return;
		}
		if (message.getMethod() == null) {
			HttpUtil.sendResponseError(ctx.channel(), RequestErrorCode.ERROR_METHOD_FORBBIDEN);
			return;
		}
		// 如果为POST，那么只能POST,如果是Get，那么都可以
		if (message.getMethod() == QueryMethod.POST && msg.method() != POST) {
			HttpUtil.sendResponseError(ctx.channel(), RequestErrorCode.ERROR_METHOD_ERROR);
			return;
		}
		@SuppressWarnings("unchecked")
		HttpHandler<UrlMessage> handler = (HttpHandler<UrlMessage>) this.config.getFacotry().getHandler(url);
		if (handler == null) {
			HttpUtil.sendResponseError(ctx.channel(), RequestErrorCode.ERROR_URL_FORBBIDEN);
			return;
		}
		message.setChannel(ctx.channel());
		message.setHandler(handler);
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
					}
				}
			} catch (Exception e) {
				log.error(e, e);
				HttpUtil.sendResponseError(ctx.channel(), RequestErrorCode.ERROR_POST_ERROR);
				return;
			}
		}
		try {
			Field[] declaredFields = message.getClass().getDeclaredFields();
			for (Field field : declaredFields) {
				if (Modifier.isTransient(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);
				Object object = params.getObject(field.getName());
				if (object != null) {
					field.set(message, object);
				}
			}
		} catch (Exception e) {
			HttpUtil.sendResponseError(ctx.channel(), RequestErrorCode.ERROR_MESSAGE_PARSE, e.getMessage());
			return;
		}
		this.serverEventListener.dispatchMessage(message, params);
	}
}
