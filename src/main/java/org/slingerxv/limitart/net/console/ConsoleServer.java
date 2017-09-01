/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.net.console;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Proc4;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.net.define.AbstractNettyServer;
import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.util.Beta;
import org.slingerxv.limitart.util.SecurityUtil;
import org.slingerxv.limitart.util.StringUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

/**
 * 控制台服务器
 * 
 * @author hank
 *
 */
@Beta
public class ConsoleServer extends AbstractNettyServer implements IServer {
	private static Logger log = LoggerFactory.getLogger(ConsoleServer.class);
	private static AttributeKey<String> USERNAME_KEY = AttributeKey.newInstance("USERNAME_KEY");

	private static AttributeKey<String> USERNAME_TEMP_KEY = AttributeKey.newInstance("USERNAME_TEMP_KEY");
	private String serverName;
	// 端口
	private int port;
	private Set<String> whiteList;
	private Map<String, ConsoleUser> users = new ConcurrentHashMap<>();
	private Map<String, Proc3<ConsoleUser, String, String[]>> commands = new ConcurrentHashMap<>();
	private Proc2<Channel, Throwable> onExceptionCaught;
	private Proc1<Channel> onServerBind;
	private Proc1<ConsoleUser> onUserLogin;
	private Proc1<ConsoleUser> onUserLogout;
	private Proc4<ConsoleUser, String, String[], Proc3<ConsoleUser, String, String[]>> dispatchMessage;

	private ConsoleServer(ConsoleServerBuilder builder) {
		super(builder.serverName);
		this.serverName = builder.serverName;
		this.port = builder.port;
		this.users = builder.users;
		this.commands = builder.commands;
		this.whiteList = builder.whiteList;
		this.onUserLogout = builder.onUserLogout;
		this.onExceptionCaught = builder.onExceptionCaught;
		this.onServerBind = builder.onServerBind;
		this.onUserLogin = builder.onUserLogin;
		this.dispatchMessage = builder.dispatchMessage;
	}

	@Override
	public void startServer() {
		bind(port, onServerBind);
	}

	@Override
	public void stopServer() {
		unbind();
	}

	@Override
	protected void initPipeline(ChannelPipeline pipeline) {
		pipeline.addLast(new LineBasedFrameDecoder(256)).addLast(new StringDecoder(CharsetUtil.UTF_8))
				.addLast(new StringEncoder(CharsetUtil.UTF_8)).addLast(new ChannelInboundHandlerAdapter() {
					@Override
					public boolean isSharable() {
						return true;
					}

					@Override
					public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
						log.error(ctx.channel() + " cause:", cause);
						Procs.invoke(onExceptionCaught, ctx.channel(), cause);
					}

					@Override
					public void channelActive(ChannelHandlerContext ctx) {
						log.info(ctx.channel().remoteAddress() + " connected！");
						if (whiteList != null && !whiteList.isEmpty()) {
							InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
							String remoteAddress = insocket.getAddress().getHostAddress();
							if (!whiteList.contains(remoteAddress)) {
								ctx.channel().close();
								log.info("ip: " + remoteAddress + " rejected link!");
								return;
							}
						}
						sendMessage(ctx.channel(), "====Welcome To " + serverName + " !====");
						sendMessage(ctx.channel(), "username:");
					}

					@Override
					public void channelInactive(ChannelHandlerContext ctx) {
						log.info(ctx.channel().remoteAddress() + " disconnected！");
						ConsoleUser consoleUser = getConsoleUser(ctx.channel());
						if (consoleUser != null) {
							consoleUser.setChannel(null);
							Procs.invoke(onUserLogout, consoleUser);
						}
					}

					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) {
						String command = (String) msg;
						if (StringUtil.isEmptyOrNull(command)) {
							return;
						}
						Channel ch = ctx.channel();
						ConsoleUser consoleUser = getConsoleUser(ch);
						// 未通过验证的用户
						if (consoleUser == null) {
							String tempUsername = ch.attr(USERNAME_TEMP_KEY).get();
							if (tempUsername == null) {
								// 未输入过用户名的用户
								ConsoleUser temp = users.get(command);
								if (temp == null) {
									sendMessage(ch, "username not exist!");
									return;
								} else {
									ch.attr(USERNAME_TEMP_KEY).set(command);
									log.info("remote:" + ch.remoteAddress() + " ready to login on:" + command);
									sendMessage(ch, "password:");
								}
							} else {
								ConsoleUser temp = users.get(tempUsername);
								try {
									if (!SecurityUtil.isPasswordValid(temp.getPass(), command, temp.getUsername())) {
										sendMessage(ch, "password error!");
										return;
									}
								} catch (NoSuchAlgorithmException e) {
									log.error(e.getMessage(), e);
								}
								Channel oldChannel = temp.getChannel();
								if (oldChannel != null) {
									sendMessage(oldChannel, "another client login from:" + ch.remoteAddress(),
											(t1, t2, t3) -> {
												oldChannel.close();
											});
								}
								temp.setChannel(ch);
								ch.attr(USERNAME_KEY).set(temp.getUsername());
								log.info("remote:" + ch.remoteAddress() + " login on:" + ch.attr(USERNAME_KEY).get()
										+ " success!");
								sendMessage(ctx.channel(), "====Login " + serverName + " Success!====");
								Procs.invoke(onUserLogin, temp);
							}
						} else {
							String[] split = command.split(" ");
							String[] params = new String[] {};
							String cmd = split[0];
							if (split.length > 1) {
								params = new String[split.length - 1];
								System.arraycopy(split, 1, params, 0, params.length);
							}
							Proc3<ConsoleUser, String, String[]> handler = commands.get(cmd);
							if (handler == null) {
								sendMessage(ch, "'" + cmd + "'is not a command!");
								return;
							}
							log.info("remote:" + ch.remoteAddress() + " login on:" + ch.attr(USERNAME_KEY).get()
									+ " execute cmd:" + cmd);
							Procs.invoke(dispatchMessage, consoleUser, cmd, params, handler);
						}
					}
				});
	}

	public void sendMessage(Channel channel, String msg) {
		sendMessage(channel, msg, null);
	}

	public void sendMessage(Channel channel, String msg, Proc3<Boolean, Throwable, Channel> listener) {
		String info = "->" + msg + "\r\n";
		channel.writeAndFlush(info).addListener((ChannelFutureListener) arg0 -> {
			Procs.invoke(listener, arg0.isSuccess(), arg0.cause(), arg0.channel());
		});
	}

	private ConsoleUser getConsoleUser(Channel channel) {
		Attribute<String> attr = channel.attr(USERNAME_KEY);
		if (attr == null) {
			return null;
		}
		String username = attr.get();
		if (username == null) {
			return null;
		}
		return users.get(username);
	}

	public static class ConsoleServerBuilder {
		private String serverName;
		private int port;
		private Set<String> whiteList = new HashSet<>();
		private Map<String, ConsoleUser> users = new ConcurrentHashMap<>();
		private Map<String, Proc3<ConsoleUser, String, String[]>> commands = new ConcurrentHashMap<>();
		private Proc2<Channel, Throwable> onExceptionCaught;
		private Proc1<Channel> onServerBind;
		private Proc1<ConsoleUser> onUserLogin;
		private Proc1<ConsoleUser> onUserLogout;
		private Proc4<ConsoleUser, String, String[], Proc3<ConsoleUser, String, String[]>> dispatchMessage;

		public ConsoleServerBuilder() {
			this.serverName = "Console-Server";
			this.port = 7023;
			this.dispatchMessage = new Proc4<ConsoleUser, String, String[], Proc3<ConsoleUser, String, String[]>>() {

				@Override
				public void run(ConsoleUser t1, String t2, String[] t3, Proc3<ConsoleUser, String, String[]> t4) {
					t4.run(t1, t2, t3);
				}
			};
		}

		/**
		 * 构建服务器
		 * 
		 * @return
		 * @throws Exception
		 */
		public ConsoleServer build() {
			return new ConsoleServer(this);
		}

		public ConsoleServerBuilder serverName(String serverName) {
			this.serverName = serverName;
			return this;
		}

		/**
		 * 绑定端口
		 * 
		 * @param port
		 * @return
		 */
		public ConsoleServerBuilder port(int port) {
			this.port = port;
			return this;
		}

		public ConsoleServerBuilder whiteList(String... remoteAddress) {
			for (String ip : remoteAddress) {
				if (StringUtil.isIp4(ip)) {
					this.whiteList.add(ip);
				}
			}
			return this;
		}

		public ConsoleServerBuilder user(ConsoleUser... users)
				throws NoSuchAlgorithmException, ConsoleUserDuplicatedException {
			for (ConsoleUser temp : users) {
				ConsoleUser newUser = new ConsoleUser();
				newUser.setUsername(Objects.requireNonNull(temp.getUsername(), "username"));
				newUser.setPass(SecurityUtil.encodePassword(Objects.requireNonNull(temp.getPass(), "pass"),
						temp.getUsername()));
				if (this.users.containsKey(newUser.getUsername())) {
					throw new ConsoleUserDuplicatedException(newUser.getUsername());
				}
				this.users.put(newUser.getUsername(), newUser);
			}
			return this;
		}

		public ConsoleServerBuilder cmd(String cmd, Proc3<ConsoleUser, String, String[]> handler)
				throws CommandDuplicatedException {
			if (commands.containsKey(cmd)) {
				throw new CommandDuplicatedException(cmd);
			}
			commands.put(cmd, handler);
			return this;
		}

		public ConsoleServerBuilder onExceptionCaught(Proc2<Channel, Throwable> onExceptionCaught) {
			this.onExceptionCaught = onExceptionCaught;
			return this;
		}

		public ConsoleServerBuilder onServerBind(Proc1<Channel> onServerBind) {
			this.onServerBind = onServerBind;
			return this;
		}

		public ConsoleServerBuilder dispatchMessage(
				Proc4<ConsoleUser, String, String[], Proc3<ConsoleUser, String, String[]>> dispatchMessage) {
			this.dispatchMessage = dispatchMessage;
			return this;
		}

		public ConsoleServerBuilder onUserLogin(Proc1<ConsoleUser> onUserLogin) {
			this.onUserLogin = onUserLogin;
			return this;
		}

		public ConsoleServerBuilder onUserLogout(Proc1<ConsoleUser> onUserLogout) {
			this.onUserLogout = onUserLogout;
			return this;
		}
	}
}
