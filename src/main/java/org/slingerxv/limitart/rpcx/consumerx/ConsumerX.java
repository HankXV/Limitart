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
package org.slingerxv.limitart.rpcx.consumerx;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.collections.ConcurrentHashSet;
import org.slingerxv.limitart.net.AddressPair;
import org.slingerxv.limitart.net.binary.BinaryClient;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.rpcx.consumerx.config.ConsumerXConfig;
import org.slingerxv.limitart.rpcx.consumerx.define.IServiceAsyncCallback;
import org.slingerxv.limitart.rpcx.consumerx.listener.IConsumerListener;
import org.slingerxv.limitart.rpcx.consumerx.selector.define.IProviderSelector;
import org.slingerxv.limitart.rpcx.consumerx.struct.ProviderRemote;
import org.slingerxv.limitart.rpcx.consumerx.struct.RemoteFuture;
import org.slingerxv.limitart.rpcx.define.ServiceX;
import org.slingerxv.limitart.rpcx.exception.ServiceError;
import org.slingerxv.limitart.rpcx.exception.ServiceXExecuteException;
import org.slingerxv.limitart.rpcx.exception.ServiceXIOException;
import org.slingerxv.limitart.rpcx.exception.ServiceXProxyException;
import org.slingerxv.limitart.rpcx.message.service.DirectFetchProviderServicesMessage;
import org.slingerxv.limitart.rpcx.message.service.DirectFetchProviderServicesResultMessage;
import org.slingerxv.limitart.rpcx.message.service.NoticeProviderDisconnectedServiceCenterMessage;
import org.slingerxv.limitart.rpcx.message.service.RpcExecuteClientMessage;
import org.slingerxv.limitart.rpcx.message.service.RpcResultServerMessage;
import org.slingerxv.limitart.rpcx.message.service.SubscribeServiceFromServiceCenterConsumerMessage;
import org.slingerxv.limitart.rpcx.message.service.SubscribeServiceResultServiceCenterMessage;
import org.slingerxv.limitart.rpcx.message.service.meta.ProviderHostMeta;
import org.slingerxv.limitart.rpcx.message.service.meta.ProviderServiceMeta;
import org.slingerxv.limitart.rpcx.struct.RpcProviderName;
import org.slingerxv.limitart.rpcx.util.RpcUtil;
import org.slingerxv.limitart.util.ReflectionUtil;
import org.slingerxv.limitart.util.StringUtil;

/**
 * RPC客户端
 * 
 * @author hank
 *
 */
public class ConsumerX {
	private static Logger log = LoggerFactory.getLogger(ConsumerX.class);
	// Rpc客户端到服务器链接集合,服务器分配Id
	private Map<Integer, BinaryClient> clients = new ConcurrentHashMap<>();
	private BinaryClient serviceCenterClient;
	private ConsumerXConfig config;
	// 动态代理集合
	private Map<Class<?>, Object> clientProxys = new HashMap<>();
	// 服务对应的本地代理接口集合
	private Map<String, Class<?>> serviceProxyClasses = new HashMap<>();
	// 服务对应的服务器ID集合
	private Map<String, Set<Integer>> serviceServers = new ConcurrentHashMap<>();
	// RequestId生成器
	private AtomicInteger requestIdCreater = new AtomicInteger(0);
	// RPC调用回调集合
	private Map<Integer, RemoteFuture> futures = new ConcurrentHashMap<>();
	private LongAdder dropNum = new LongAdder();

	private IConsumerListener listener;
	private boolean isDirectLink = false;

	public ConsumerX(ConsumerXConfig config) {
		this(config, null);
	}

	public ConsumerX(ConsumerXConfig config, IConsumerListener listener) {
		if (config == null) {
			throw new NullPointerException("ConsumerXConfig");
		}
		this.listener = listener;
		this.config = config;
	}

	public void init() throws Exception {
		initRpcProxys();
		// 判断是连接服务中心还是直连RPC服务器
		ProviderRemote[] providerRemotes = config.getProviderRemotes();
		if (providerRemotes != null) {
			isDirectLink = true;
			// 直连模式
			for (ProviderRemote remote : providerRemotes) {
				createRpcClient(remote.getProviderIp(), remote.getProviderPort()).connect();
			}
		} else {
			// 服务中心模式
			String serviceCenterIp = config.getServiceCenterIp();
			if (StringUtil.isEmptyOrNull(serviceCenterIp)) {
				throw new ServiceXIOException("need service center's Ip or direct provider remote Ip!");
			}
			MessageFactory centryFactory = new MessageFactory();
			centryFactory.registerMsg(new SubscribeServiceResultServiceCenterHandler());
			centryFactory.registerMsg(new NoticeProviderDisconnectedServiceCenterHandler());
			serviceCenterClient = new BinaryClient.BinaryClientBuilder().autoReconnect(config.getAutoConnectInterval())
					.remoteAddress(new AddressPair(config.getServiceCenterIp(), config.getServiceCenterPort(), null))
					.clientName("RPC-Consumer").factory(centryFactory).onConnectionEffective(client -> {
						if (listener != null) {
							listener.onServiceCenterConnected(ConsumerX.this);
						}
						// 订阅服务
						subscribeServicesFromServiceCenter();
					}).dispatchMessage((message, handler) -> {
						message.setExtra(ConsumerX.this);
						try {
							handler.handle(message);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}).build();
			serviceCenterClient.connect();
		}
	}

	private BinaryClient createRpcClient(String providerIp, int providerPort) throws Exception {
		MessageFactory rpcMessageFacotry = new MessageFactory();
		rpcMessageFacotry.registerMsg(new RpcResultServerHandler());
		rpcMessageFacotry.registerMsg(new DirectFetchProviderServicesResultHandler());
		BinaryClient client = new BinaryClient.BinaryClientBuilder()
				.remoteAddress(new AddressPair(providerIp, providerPort)).autoReconnect(config.getAutoConnectInterval())
				.factory(rpcMessageFacotry).onChannelStateChanged((binaryClient, active) -> {
					if (!active) {
						clearOnDisconnected(binaryClient);
					}
				}).onConnectionEffective(binaryClient -> {
					if (isDirectLink) {
						// 当链接生效时，拉取对应服务器服务列表
						directFetchProverServices(binaryClient);
					}
					if (this.listener != null) {
						this.listener.onConsumerConnected(binaryClient);
					}
				}).dispatchMessage((message, handler) -> {
					message.setExtra(this);
					try {
						handler.handle(message);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}).build();
		return client;
	}

	/**
	 * 直接拉取RPC服务器服务列表
	 * 
	 * @param channel
	 */
	private void directFetchProverServices(BinaryClient client) {
		try {
			client.sendMessage(new DirectFetchProviderServicesMessage(), null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 当RPC服务列表返回
	 * 
	 * @param channelHandlerContext
	 * 
	 * @param services
	 */
	private void onDirectFetchProviderServices(BinaryClient bc, int providerId, List<String> services) {
		// 检查服务是否完全匹配
		Set<String> notMatchList = new HashSet<>(serviceProxyClasses.keySet());
		for (String remoteService : services) {
			if (!serviceProxyClasses.containsKey(remoteService)) {
				continue;
			}
			// 将此服务器加入到服务列表中
			notMatchList.remove(remoteService);
			Set<Integer> list = serviceServers.get(remoteService);
			if (list == null) {
				list = new ConcurrentHashSet<>();
				Set<Integer> putIfAbsent = serviceServers.putIfAbsent(remoteService, list);
				if (putIfAbsent != null) {
					list = putIfAbsent;
				}
			}
			list.add(providerId);
			BinaryClient tc = clients.get(providerId);
			if (tc == null) {
				// BinaryClient的加入
				BinaryClient putIfAbsent = clients.putIfAbsent(providerId, bc);
				if (putIfAbsent != null) {
					tc = putIfAbsent;
				}
			}
		}
		if (!notMatchList.isEmpty()) {
			log.error("本地服务尚有：" + notMatchList.size() + "条没有匹配远程服务器，请检查！");
			for (String notMatch : notMatchList) {
				log.info("未匹配的服务：" + notMatch);
			}
		}
	}

	/**
	 * 向服务中心订阅服务获取服务器列表
	 */
	private void subscribeServicesFromServiceCenter() {
		try {
			serviceCenterClient.sendMessage(new SubscribeServiceFromServiceCenterConsumerMessage(), null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 服务中心推送所有服务过来
	 * 
	 * @param services
	 */
	private void onSubsribeServicesCome(List<ProviderServiceMeta> services) {
		log.info("接收到服务中心推送过来的服务，开始筛选和处理...");
		for (ProviderServiceMeta info : services) {
			// 筛选自己有用的服务
			String serviceName = info.serviceName;
			List<ProviderHostMeta> hostInfos = info.hostInfos;
			if (!serviceProxyClasses.containsKey(serviceName)) {
				// 不需要的服务
				continue;
			}
			Set<Integer> list = serviceServers.get(serviceName);
			if (list == null) {
				list = new ConcurrentHashSet<>();
				Set<Integer> putIfAbsent = serviceServers.putIfAbsent(serviceName, list);
				if (putIfAbsent != null) {
					list = putIfAbsent;
				}
			}
			for (ProviderHostMeta temp : hostInfos) {
				int providerId = temp.getProviderId();
				list.add(providerId);
				log.info("开始订阅RPC" + providerId + "服务器[" + temp.getIp() + ":" + temp.getPort() + "]的服务：" + serviceName);
				BinaryClient binaryClient = clients.get(providerId);
				if (binaryClient == null) {
					try {
						binaryClient = createRpcClient(temp.getIp(), temp.getPort());
						BinaryClient putIfAbsent = clients.putIfAbsent(providerId, binaryClient);
						if (putIfAbsent == null) {
							binaryClient.connect();
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
		log.info("筛选和处理服务中心推送过来的服务完毕。");
	}

	/**
	 * 当断开链接时清理链接相关的服务
	 * 
	 * @param ctx
	 */
	private void clearOnDisconnected(BinaryClient client) {
		Iterator<Entry<Integer, BinaryClient>> iterator = clients.entrySet().iterator();
		for (; iterator.hasNext();) {
			Entry<Integer, BinaryClient> next = iterator.next();
			int providerId = next.getKey();
			BinaryClient otherClient = next.getValue();
			if (client.channel().id().asLongText().equals(otherClient.channel().id().asLongText())) {
				iterator.remove();
				log.info("RPC服务器断开链接，providerId:" + providerId + "，地址：" + client.remoteAddress());
				// 删除服务
				for (Entry<String, Set<Integer>> entry : serviceServers.entrySet()) {
					String serviceName = entry.getKey();
					Set<Integer> provideIds = entry.getValue();
					Iterator<Integer> pit = provideIds.iterator();
					for (; pit.hasNext();) {
						Integer nextProviderId = pit.next();
						if (nextProviderId == providerId) {
							pit.remove();
							log.info("删除提供者：" + nextProviderId + "提供的服务：" + serviceName);
						}
					}
				}
			}
		}
	}

	/**
	 * 服务中心通知服务提供者断开链接
	 * 
	 * @param providerUID
	 */
	private void onNoticeProviderDisconnected(int providerUID) {
		BinaryClient binaryClient = clients.get(providerUID);
		if (binaryClient != null) {
			binaryClient.disConnect();
		}
	}

	/**
	 * 创建RPC同步调用代理
	 * 
	 * @param interfaceClss
	 * @return
	 * @throws RPCIOException
	 */
	@SuppressWarnings("unchecked")
	public <T> T createProxy(Class<T> interfaceClss) throws ServiceXProxyException {
		Object proxyObject = this.clientProxys.get(interfaceClss);
		if (proxyObject == null) {
			throw new ServiceXProxyException(interfaceClss.getName() + "不是一个RPC服务！");
		}
		return (T) proxyObject;
	}

	/**
	 * 远程异步调用
	 * 
	 * @param providerName
	 * @param serviceClass
	 * @param method
	 * @param args
	 * @param providerSelector
	 * @return
	 * @throws ServiceXExecuteException
	 * @throws ServiceXIOException
	 * @throws InterruptedException
	 * @throws ServiceXProxyException
	 */
	public Object remoteCall(RpcProviderName providerName, Class<?> serviceClass, Method method, Object[] args,
			IProviderSelector providerSelector, IServiceAsyncCallback callback)
			throws ServiceXExecuteException, ServiceXIOException, InterruptedException, ServiceXProxyException {
		return proxyExecute(RpcUtil.getServiceName(providerName, serviceClass), null, method.getName(),
				ReflectionUtil.getMethodOverloadName(method), args, providerSelector, callback);
	}

	/**
	 * 获取本消费者链接到的所有提供者
	 * 
	 * @return
	 */
	public List<Integer> getProviderIds() {
		return new ArrayList<>(this.clients.keySet());
	}

	/**
	 * 获取服务相关的所有提供者
	 * 
	 * @param serviceName
	 * @return
	 * @throws ServiceXProxyException
	 */
	public List<Integer> getProviderIds(RpcProviderName providerName, Class<?> clazz) throws ServiceXProxyException {
		List<Integer> list = new ArrayList<>();
		Set<Integer> set = this.serviceServers.get(RpcUtil.getServiceName(providerName, clazz));
		if (set != null) {
			list.addAll(set);
		}
		return list;
	}

	/**
	 * 初始化本地服务代理
	 * 
	 * @param packageName
	 * @throws IOException
	 * @throws ReflectiveOperationException
	 * @throws Exception
	 */
	private void initRpcProxys() throws ServiceXProxyException, IOException, ReflectiveOperationException {
		clientProxys.clear();
		List<Class<?>> classesByPackage = new ArrayList<>();
		for (String temp : this.config.getServicePackages()) {
			classesByPackage.addAll(ReflectionUtil.getClassesByPackage(temp, Object.class));
		}
		for (Class<?> clazz : classesByPackage) {
			ServiceX annotation = clazz.getAnnotation(ServiceX.class);
			if (annotation == null) {
				continue;
			}
			if (!clazz.isInterface()) {
				throw new ServiceXProxyException(clazz.getName() + "RPC服务器必须是一个接口！");
			}
			// 检查参数是否符合标准
			String provider = annotation.provider();
			if (StringUtil.isEmptyOrNull(provider)) {
				throw new ServiceXProxyException("服务：" + clazz.getName() + "的提供商为空！");
			}
			String serviceName = RpcUtil.getServiceName(new RpcProviderName(provider), clazz);
			// 检查方法
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				String methodOverloadName = ReflectionUtil.getMethodOverloadName(method);
				// 检查参数
				Class<?>[] parameterTypes = method.getParameterTypes();
				for (Class<?> paramsType : parameterTypes) {
					RpcUtil.checkParamType(paramsType);
				}
				// 检查返回参数是否合法
				RpcUtil.checkParamType(method.getReturnType());
				// 异常抛出检查
				Class<?>[] exceptionTypes = method.getExceptionTypes();
				if (exceptionTypes == null || exceptionTypes.length < 1) {
					throw new ServiceXProxyException("类" + clazz.getName() + "的方法" + methodOverloadName + "必须要抛出异常："
							+ Exception.class.getName());
				}
				boolean exOk = false;
				for (Class<?> ex : exceptionTypes) {
					if (ex == Exception.class) {
						exOk = true;
					}
				}
				if (!exOk) {
					throw new ServiceXProxyException("类" + clazz.getName() + "的方法" + methodOverloadName + "的异常抛出必须有："
							+ Exception.class.getName());
				}
			}
			// 创建动态代理类
			Object newProxyInstance = ReflectionUtil.newProxy(clazz,
					(InvocationHandler) (proxy, method, args) -> proxyExecute(serviceName, proxy, method.getName(),
							ReflectionUtil.getMethodOverloadName(method), args, ConsumerX.this.config.getSelector(),
							null));
			if (serviceProxyClasses.containsKey(serviceName)) {
				throw new ServiceXProxyException("服务名重复:" + serviceName);
			}
			serviceProxyClasses.put(serviceName, clazz);
			clientProxys.put(clazz, newProxyInstance);
			log.info("创建服务动态代理：" + serviceName + "，服务提供商：" + provider + "，代理实例：" + newProxyInstance);
		}
	}

	/**
	 * rpc动态代理方法
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * @throws RPCIOException
	 * @throws InterruptedException
	 * @throws ServiceXIOException
	 */
	private Object proxyExecute(String serviceName, Object proxy, String methodName, String methodOverloadName,
			Object[] args, IProviderSelector providerSelector, IServiceAsyncCallback callback)
			throws ServiceXExecuteException, InterruptedException, ServiceXIOException {
		if (proxy != null) {
			if ("equals".equals(methodName)) {
				return proxy == args[0];
			} else if ("hashCode".equals(methodName)) {
				return System.identityHashCode(proxy);
			} else if ("toString".equals(methodName)) {
				return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy));
			}
		}
		if (futures.size() > ConsumerX.this.config.getRpcCallBackMaxLength()) {
			dropNum.increment();
			throw new ServiceXExecuteException("回调列表超过限制：" + ConsumerX.this.config.getRpcCallBackMaxLength()
					+ ",不进行任何处理！,已抛弃数量：" + dropNum.longValue());
		}
		RemoteFuture future = rpcSend(serviceName, methodOverloadName, args, providerSelector);
		future.setCallback(callback);
		int providerId = future.getProviderId();
		if (future.getResponseResult() == null) {
			// 无条件线程等待
			boolean await = future.getCountDownLatch().await(ConsumerX.this.config.getRpcExecuteTimeoutInMills(),
					TimeUnit.MILLISECONDS);
			futures.remove(future.getRequestId());
			if (!await) {
				throw new ServiceXExecuteException(
						"动态代理方法：" + methodOverloadName + "，服务器：" + providerId + "超时，回调ID：" + future.getRequestId());
			}
		}
		// 等待完成设置回调完成，服务器处理完毕后就不用唤醒此线程
		RpcResultServerMessage response = future.getResponseResult();
		int errorCode = response.getErrorCode();
		if (errorCode == ServiceError.SUCCESS) {
			return response.getReturnVal();
		} else if (errorCode == ServiceError.SERVER_HAS_NO_MODULE) {
			throw new ServiceXExecuteException("服务器：" + providerId + "没有服务名为：" + serviceName);
		} else if (errorCode == ServiceError.SERVER_HAS_NO_METHOD) {
			throw new ServiceXExecuteException(
					"服务器：" + providerId + "没有服务名为：" + serviceName + "的" + methodOverloadName + "方法！");
		} else {
			throw new ServiceXExecuteException("服务器：" + providerId + "服务名：" + serviceName + "返回未知错误码！");
		}
	}

	/**
	 * 发送到RPC请求到服务器
	 * 
	 * @param serviceName
	 * @param methodOverloadName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private RemoteFuture rpcSend(String serviceName, String methodOverloadName, Object[] args,
			IProviderSelector providerSelector) throws ServiceXIOException {
		Set<Integer> list = serviceServers.get(serviceName);
		if (list == null) {
			throw new ServiceXIOException("服务：" + serviceName + "找不到可用服务器列表");
		}
		Integer selectServer = providerSelector.selectServer(serviceName, methodOverloadName, args,
				new ArrayList<>(list));
		if (selectServer == null) {
			throw new ServiceXIOException(serviceName + "找不到可用服务器，可能是服务器选择器出错，方法：" + methodOverloadName);
		}
		BinaryClient binaryClient = this.clients.get(selectServer);
		if (binaryClient == null) {
			throw new ServiceXIOException("严重错误，找不到服务提供者：" + selectServer + "的链接实例！");
		}
		// 开始构造消息
		RpcExecuteClientMessage msg = new RpcExecuteClientMessage();
		msg.requestId = requestIdCreater.incrementAndGet();
		msg.moduleName = serviceName;
		msg.methodName = methodOverloadName;
		if (args != null && args.length > 0) {
			for (Object obj : args) {
				if (obj == null) {
					msg.paramTypes.add(null);
					msg.params.add(null);
				} else {
					msg.paramTypes.add(obj.getClass().getName());
					msg.params.add(obj);
				}
			}
		}
		RemoteFuture future = new RemoteFuture();
		future.setProviderId(selectServer);
		future.setRequestId(msg.requestId);
		futures.put(msg.requestId, future);
		if (futures.size() > 100) {
			log.error("警告！开始动态代理方法：" + methodOverloadName + "，服务器：" + selectServer + "，回调列表长度：" + futures.size()
					+ "(并发量)，id：" + msg.requestId);
		}
		// 发送消息
		try {
			binaryClient.sendMessage(msg, (isSuccess, cause, channel) -> {
				if (!isSuccess) {
					futures.remove(msg.requestId);
					log.error("动态代理方法：" + methodOverloadName + "，服务器：" + selectServer + "失败！网络未连接！" + "，id："
							+ msg.requestId);
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return future;
	}

	/**
	 * 当RPC服务器回应RPC服务结果
	 * 
	 * @param requestId
	 * @param errorCode
	 * @param returnVal
	 * @throws RPCIOException
	 */
	private void onRPCResonse(RpcResultServerMessage msg) {
		int requestId = msg.getRequestId();
		RemoteFuture rpcFuture = futures.get(requestId);
		if (rpcFuture == null) {
			log.error("requestId:" + requestId + "找不到回调！");
			return;
		}
		rpcFuture.setResponseResult(msg);
		// 不是异步回调
		if (rpcFuture.getCallback() == null) {
			// 唤醒线程
			rpcFuture.getCountDownLatch().countDown();
		} else {
			futures.remove(requestId);
			int errorCode = msg.getErrorCode();
			if (errorCode == ServiceError.SUCCESS) {
				rpcFuture.getCallback().action(msg.getReturnVal());
			}
		}
	}

	public class RpcResultServerHandler implements IHandler<RpcResultServerMessage> {

		@Override
		public void handle(RpcResultServerMessage msg) {
			((ConsumerX) msg.getExtra()).onRPCResonse(msg);
		}

	}

	public class DirectFetchProviderServicesResultHandler
			implements IHandler<DirectFetchProviderServicesResultMessage> {

		@Override
		public void handle(DirectFetchProviderServicesResultMessage msg) {
			int providerId = msg.providerId;
			List<String> services = msg.services;
			((ConsumerX) msg.getExtra()).onDirectFetchProviderServices(msg.getClient(), providerId, services);
		}
	}

	public class SubscribeServiceResultServiceCenterHandler
			implements IHandler<SubscribeServiceResultServiceCenterMessage> {

		@Override
		public void handle(SubscribeServiceResultServiceCenterMessage msg) {
			List<ProviderServiceMeta> services = msg.services;
			((ConsumerX) msg.getExtra()).onSubsribeServicesCome(services);
		}

	}

	public class NoticeProviderDisconnectedServiceCenterHandler
			implements IHandler<NoticeProviderDisconnectedServiceCenterMessage> {

		@Override
		public void handle(NoticeProviderDisconnectedServiceCenterMessage msg) {
			((ConsumerX) msg.getExtra()).onNoticeProviderDisconnected(msg.getProviderUID());
		}

	}
}
