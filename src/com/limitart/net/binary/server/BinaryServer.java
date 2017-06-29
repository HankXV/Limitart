package com.limitart.net.binary.server;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.math.util.RandomUtil;
import com.limitart.net.binary.codec.ByteDecoder;
import com.limitart.net.binary.handler.IHandler;
import com.limitart.net.binary.listener.SendMessageListener;
import com.limitart.net.binary.message.Message;
import com.limitart.net.binary.message.MessageFactory;
import com.limitart.net.binary.message.constant.InnerMessageEnum;
import com.limitart.net.binary.message.impl.validate.ConnectionValidateClientMessage;
import com.limitart.net.binary.message.impl.validate.ConnectionValidateServerMessage;
import com.limitart.net.binary.message.impl.validate.ConnectionValidateSuccessServerMessage;
import com.limitart.net.binary.server.config.BinaryServerConfig;
import com.limitart.net.binary.server.listener.BinaryServerEventListener;
import com.limitart.net.binary.util.SendMessageUtil;
import com.limitart.util.SymmetricEncryptionUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 二进制通信服务器
 * 
 * @author Hank
 *
 */
@Sharable
public class BinaryServer extends ChannelInboundHandlerAdapter {
	private static Logger log = LogManager.getLogger();
	private ServerBootstrap boot;
	private Channel channel;
	private static EventLoopGroup bossGroup;
	private static EventLoopGroup workerGroup;
	private BinaryServerConfig config;
	protected MessageFactory messageFactory;
	protected BinaryServerEventListener serverEventListener;
	private ConcurrentHashMap<String, SessionValidateData> tempChannels = new ConcurrentHashMap<>();
	private SymmetricEncryptionUtil encrypUtil;
	static {
		if (Epoll.isAvailable()) {
			bossGroup = new EpollEventLoopGroup();
			workerGroup = new EpollEventLoopGroup();
		} else {
			bossGroup = new NioEventLoopGroup();
			workerGroup = new NioEventLoopGroup();
		}
	}

	public BinaryServer(BinaryServerConfig config, BinaryServerEventListener serverEventListener,
			MessageFactory msgFactory) {
		if (config == null) {
			throw new NullPointerException("BinaryServerConfig");
		}
		if (serverEventListener == null) {
			throw new NullPointerException("BinaryServerEventListener");
		}
		if (msgFactory == null) {
			throw new NullPointerException("MessageFactory");
		}
		this.serverEventListener = serverEventListener;
		this.config = config;
		// 初始化内部消息
		this.messageFactory = msgFactory.registerMsg(InnerMessageEnum.ConnectionValidateClientMessage.getValue(),
				ConnectionValidateClientMessage.class, new ConnectionValidateClientHandler());
		// 初始化加密工具
		try {
			encrypUtil = SymmetricEncryptionUtil.getEncodeInstance(BinaryServer.this.config.getConnectionPass(),
					"20170106");
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException e) {
			log.error(e, e);
		}
		boot = new ServerBootstrap();
		if (Epoll.isAvailable()) {
			bossGroup = new EpollEventLoopGroup();
			workerGroup = new EpollEventLoopGroup();
			boot.option(ChannelOption.SO_BACKLOG, 1024).channel(EpollServerSocketChannel.class)
					.childOption(ChannelOption.SO_LINGER, 0).childOption(ChannelOption.SO_REUSEADDR, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
					.childOption(ChannelOption.SO_RCVBUF, 32 * 1024);
			log.info(config.getServerName() + " epoll init");
		} else {
			bossGroup = new NioEventLoopGroup();
			workerGroup = new NioEventLoopGroup();
			boot.channel(NioServerSocketChannel.class);
			log.info(config.getServerName() + " nio init");
		}
		boot.group(bossGroup, workerGroup).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializerImpl(this));
		schedule(new Runnable() {

			@Override
			public void run() {
				clearUnvalidatedConnection();
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	public void schedule(Runnable command, long delay, TimeUnit unit) {
		workerGroup.schedule(command, delay, unit);
	}

	public void schedule(Runnable command, long delay, long period, TimeUnit unit) {
		workerGroup.scheduleAtFixedRate(command, delay, period, unit);
	}

	public void bind() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					boot.bind(config.getPort()).addListener(new ChannelFutureListener() {

						@Override
						public void operationComplete(ChannelFuture arg0) throws Exception {
							if (arg0.isSuccess()) {
								channel = arg0.channel();
								log.info(config.getServerName() + " bind at port:" + config.getPort());
								serverEventListener.onServerBind(arg0.channel());
							}
						}
					}).sync().channel().closeFuture().sync();
				} catch (InterruptedException e) {
					log.error(e, e);
				} finally {
					bossGroup.shutdownGracefully();
					workerGroup.shutdownGracefully();
				}
			}
		}, config.getServerName() + "-Binder").start();
	}

	private class ChannelInitializerImpl extends ChannelInitializer<SocketChannel> {
		private BinaryServer server;

		private ChannelInitializerImpl(BinaryServer server) {
			this.server = server;
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new ByteDecoder(config.getDataMaxLength())).addLast(this.server);
		}
	}

	public BinaryServer stop() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		if (channel != null) {
			channel.close();
		}
		return this;
	}

	/**
	 * 开始验证链接
	 * 
	 * @param context
	 */
	private void startConnectionValidate(Channel channel) {
		SessionValidateData data = new SessionValidateData(channel, System.currentTimeMillis(),
				RandomUtil.randomInt(0, 10000));
		// 增加到临时会话集合
		tempChannels.put(data.channel.id().asLongText(), data);
		// 通知客户端
		ConnectionValidateServerMessage msg = new ConnectionValidateServerMessage();
		String encode;
		try {
			encode = encrypUtil.encode(data.validateRandom + "");
		} catch (Exception e) {
			log.error(e, e);
			channel.close();
			log.info(config.getServerName() + " remote connection " + data.channel.remoteAddress()
					+ " discarded，server encryp util error！");
			return;
		}
		msg.setValidateStr(encode);
		try {
			SendMessageUtil.sendMessage(channel, msg, new SendMessageListener() {

				@Override
				public void onComplete(boolean isSuccess, Throwable cause, Channel channel) {
					if (isSuccess) {
						log.info(config.getServerName() + " send client " + channel.remoteAddress() + " validate token:"
								+ encode + "success！");
					} else {
						log.error(config.getServerName() + " send client " + channel.remoteAddress()
								+ " validate token:" + encode + "fail！", cause);
					}
				}
			});
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * 清理没通过验证的链接
	 */
	private void clearUnvalidatedConnection() {
		if (tempChannels.isEmpty()) {
			return;
		}
		long now = System.currentTimeMillis();
		Iterator<SessionValidateData> iterator = tempChannels.values().iterator();
		for (; iterator.hasNext();) {
			SessionValidateData data = iterator.next();
			long startValidateTime = data.startValidateTime;
			if (now - startValidateTime > this.config.getConnectionValidateTimeInSec() * 1000) {
				iterator.remove();
				data.channel.close();
				// 移除链接
				log.error(config.getServerName() + " connection " + data.channel.remoteAddress()
						+ " discarded，validate time out,wait validate size:" + tempChannels.size());
			}
		}
	}

	/**
	 * 客户端发送密码解析结果
	 * 
	 * @param context
	 * @param validateRandom
	 */
	private void onClientConnectionValidate(Channel channel, int validateRandom) {
		// 查找临时缓存
		String asLongText = channel.id().asLongText();
		SessionValidateData sessionValidateData = tempChannels.get(asLongText);
		if (sessionValidateData == null) {
			channel.close();
			// 移除链接
			log.info(config.getServerName() + " remote connection " + channel.remoteAddress()
					+ " discarded，validate time out！");
			return;
		}
		// 对比结果
		if (sessionValidateData.validateRandom != validateRandom) {
			// 移除链接
			log.info(config.getServerName() + " remote connection " + channel.remoteAddress()
					+ " discarded，validate wrong！");
			return;
		}
		tempChannels.remove(asLongText);
		log.info(config.getServerName() + " remote connection " + channel.remoteAddress() + " validate success!");
		// 通知客户端成功
		try {
			SendMessageUtil.sendMessage(channel, new ConnectionValidateSuccessServerMessage(), null);
		} catch (Exception e) {
			log.error(e, e);
		}
		this.serverEventListener.onConnectionEffective(channel);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object arg) throws Exception {
		ByteBuf buffer = (ByteBuf) arg;
		try {
			// 消息id
			short messageId = buffer.readShort();
			Message msg = messageFactory.getMessage(messageId);
			if (msg == null) {
				throw new Exception(config.getServerName() + " message empty,id:" + messageId);
			}
			msg.buffer(buffer);
			msg.decode();
			IHandler handler = messageFactory.getHandler(messageId);
			if (handler == null) {
				throw new Exception(config.getServerName() + " can not find handler for message,id:" + messageId);
			}
			msg.setHandler(handler);
			msg.setChannel(ctx.channel());
			msg.setServer(this);
			// 如果是内部消息，则自己消化
			if (InnerMessageEnum.getTypeByValue(messageId) != null) {
				handler.handle(msg);
			} else {
				this.serverEventListener.dispatchMessage(msg);
			}
		} finally {
			buffer.release();
		}
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
		this.startConnectionValidate(ctx.channel());
		this.serverEventListener.onChannelRegistered(ctx.channel());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		log.info(ctx.channel().remoteAddress() + " disconnected！");
		this.serverEventListener.onChannelUnregistered(ctx.channel());
	}

	private class SessionValidateData {
		private Channel channel;
		private long startValidateTime;
		private int validateRandom;

		private SessionValidateData(Channel channel, long startValidateTime, int validateRandom) {
			this.channel = channel;
			this.startValidateTime = startValidateTime;
			this.validateRandom = validateRandom;
		}
	}

	private class ConnectionValidateClientHandler implements IHandler {

		@Override
		public void handle(Message message) {
			ConnectionValidateClientMessage msg = (ConnectionValidateClientMessage) message;
			int validateRandom = msg.getValidateRandom();
			msg.getServer().onClientConnectionValidate(message.getChannel(), validateRandom);
		}
	}
}
