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
package org.slingerxv.limitart.rpcx.center;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.collections.ConcurrentHashSet;
import org.slingerxv.limitart.net.AddressPair;
import org.slingerxv.limitart.net.binary.BinaryServer;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.binary.message.exception.MessageCodecException;
import org.slingerxv.limitart.rpcx.center.config.ServiceCenterXConfig;
import org.slingerxv.limitart.rpcx.center.schedule.ScheduleTask;
import org.slingerxv.limitart.rpcx.center.struct.ServiceXClientSession;
import org.slingerxv.limitart.rpcx.center.struct.ServiceXServerSession;
import org.slingerxv.limitart.rpcx.message.schedule.AddScheduleToServiceCenterProviderMessage;
import org.slingerxv.limitart.rpcx.message.service.NoticeProviderDisconnectedServiceCenterMessage;
import org.slingerxv.limitart.rpcx.message.service.PushServiceToServiceCenterProviderMessage;
import org.slingerxv.limitart.rpcx.message.service.SubscribeServiceFromServiceCenterConsumerMessage;
import org.slingerxv.limitart.rpcx.message.service.SubscribeServiceResultServiceCenterMessage;
import org.slingerxv.limitart.rpcx.message.service.meta.ProviderHostMeta;
import org.slingerxv.limitart.rpcx.message.service.meta.ProviderServiceMeta;
import org.slingerxv.limitart.util.SchedulerUtil;
import org.slingerxv.limitart.util.StringUtil;
import org.slingerxv.limitart.util.TimeUtil;

import io.netty.channel.Channel;

/**
 * 服务中心
 * 
 * @author Hank
 *
 */
public class ServiceCenterX {
	private static Logger log = LoggerFactory.getLogger(ServiceCenterX.class);
	private ServiceCenterXConfig config;
	private BinaryServer binaryServer;
	// RPC服务器组<提供者Id,session>
	private Map<Integer, ServiceXServerSession> rpcServers = new ConcurrentHashMap<>();
	// RPC客户端组<客户端ChannelId,session>
	private Map<String, ServiceXClientSession> rpcClients = new ConcurrentHashMap<>();
	// 服务名称-服务器集合(ProviderId)
	private Map<String, Set<Integer>> service2Providers = new ConcurrentHashMap<>();
	// 定时任务集合
	private Map<String, Set<Integer>> schedules = new ConcurrentHashMap<>();

	public ServiceCenterX(ServiceCenterXConfig config) throws Exception {
		Objects.requireNonNull(config, "config");
		this.config = config;
		binaryServer = new BinaryServer.BinaryServerBuilder().addressPair(new AddressPair(config.getPort()))
				.factory(new MessageFactory().registerMsg(new SubscribeServiceFromServiceCenterConsumerHandler())
						.registerMsg(new PushServiceToServiceCenterProviderHandler())
						.registerMsg(new AddScheduleToServiceCenterProviderHandler()))
				.onChannelStateChanged((channel, active) -> {
					if (!active) {
						onDisconnect(channel);
					}
				}).dispatchMessage((message, handler) -> {
					message.setExtra(this);
					try {
						handler.handle(message);
					} catch (Exception e) {
						log.error("handle error", e);
					}
				}).build();
	}

	public ServiceCenterX bind() throws Exception {
		binaryServer.startServer();
		return this;
	}

	public ServiceCenterX stop() {
		binaryServer.stopServer();
		return this;
	}

	public ServiceCenterXConfig getConfig() {
		return config;
	}

	/**
	 * 接收来自Provider发布的服务
	 * 
	 * @param session
	 * @param providerPort
	 * @param providerIp
	 * @param servicesName
	 */
	private void onProviderPublicServices(Channel channel, int providerUID, String providerIp, int providerPort,
			List<String> servicesName) {
		log.info("生产者：" + providerUID + "，ip：" + channel.remoteAddress() + "开始发布服务...");
		for (String serviceName : servicesName) {
			Set<Integer> rpcServiceLBData = service2Providers.get(serviceName);
			if (rpcServiceLBData == null) {
				rpcServiceLBData = new ConcurrentHashSet<>();
				Set<Integer> putIfAbsent = service2Providers.putIfAbsent(serviceName, rpcServiceLBData);
				if (putIfAbsent != null) {
					rpcServiceLBData = putIfAbsent;
				}
			}
			rpcServiceLBData.add(providerUID);
			log.info("生产者：" + providerUID + "，ip：" + channel.remoteAddress() + "发布服务：" + serviceName);
		}
		registerServerSession(channel, providerIp, providerPort, providerUID);
		log.info("生产者：" + providerUID + "，ip：" + channel.remoteAddress() + "发布服务完毕！");
		// 广播消费者去订阅
		broadcastSingleServiceProviderInfo(providerUID);
	}

	/**
	 * 广播单个服务提供者到所有客户端
	 * 
	 * @param providerId
	 */
	private void broadcastSingleServiceProviderInfo(int providerId) {
		if (service2Providers.isEmpty()) {
			return;
		}
		SubscribeServiceResultServiceCenterMessage msg = new SubscribeServiceResultServiceCenterMessage();
		for (Entry<String, Set<Integer>> entry : service2Providers.entrySet()) {
			Set<Integer> data = entry.getValue();
			if (!data.contains(providerId)) {
				continue;
			}
			ProviderServiceMeta serviceMeta = new ProviderServiceMeta();
			serviceMeta.serviceName = entry.getKey();
			for (int tpid : data) {
				ServiceXServerSession serviceXServerSession = rpcServers.get(tpid);
				if (serviceXServerSession != null) {
					ProviderHostMeta hostMeta = new ProviderHostMeta();
					hostMeta.setIp(serviceXServerSession.getServerIp());
					hostMeta.setPort(serviceXServerSession.getServerPort());
					hostMeta.setProviderId(serviceXServerSession.getProviderId());
					serviceMeta.hostInfos.add(hostMeta);
				}
			}
			msg.services.add(serviceMeta);
		}
		// 查找所有客户端
		for (ServiceXClientSession session : rpcClients.values()) {
			try {
				binaryServer.sendMessage(session.getSession(), msg);
			} catch (MessageCodecException e) {
				log.error("send error", e);
			}
			log.info("服务中心广播[" + providerId + "]的服务到" + session.getSession().remoteAddress());
		}
	}

	/**
	 * 发送所有服务提供者到单个消费者
	 * 
	 * @param channelHandlerContext
	 */
	private void sendAllServiceProviderInfo2Consumer(Channel channel) {
		if (service2Providers.isEmpty()) {
			return;
		}
		SubscribeServiceResultServiceCenterMessage msg = new SubscribeServiceResultServiceCenterMessage();
		for (Entry<String, Set<Integer>> entry : service2Providers.entrySet()) {
			String serviceName = entry.getKey();
			Set<Integer> data = entry.getValue();
			ProviderServiceMeta info = new ProviderServiceMeta();
			msg.services.add(info);
			info.serviceName = serviceName;
			for (int providerId : data) {
				ServiceXServerSession serviceXServerSession = rpcServers.get(providerId);
				if (serviceXServerSession != null) {
					ProviderHostMeta hostMeta = new ProviderHostMeta();
					hostMeta.setIp(serviceXServerSession.getServerIp());
					hostMeta.setPort(serviceXServerSession.getServerPort());
					hostMeta.setProviderId(serviceXServerSession.getProviderId());
					info.hostInfos.add(hostMeta);
				}
			}
		}
		channel.writeAndFlush(msg);
	}

	/**
	 * 当提供者断开链接，删除其注册的服务
	 * 
	 * @param providerId
	 */
	private void onProviderDisconnected(int providerId) {
		for (Entry<String, Set<Integer>> entry : service2Providers.entrySet()) {
			String serviceName = entry.getKey();
			Set<Integer> data = entry.getValue();
			Iterator<Integer> iterator = data.iterator();
			for (; iterator.hasNext();) {
				Integer pid = iterator.next();
				if (pid == providerId) {
					iterator.remove();
					log.info("删除提供者" + providerId + "，的服务：" + serviceName);
					break;
				}
			}
		}
	}

	/**
	 * 注册RPC生产者Session
	 * 
	 * @param ctx
	 * @param providerName
	 * @param rpcServerPort
	 * @param rpcServerIp
	 * @return
	 */
	private ServiceXServerSession registerServerSession(Channel channel, String rpcServerIp, int rpcServerPort,
			int providerId) {
		if (rpcServers.containsKey(providerId)) {
			channel.close();
			log.error("服务提供者ID重复，断开链接，IP：" + channel.remoteAddress() + "，服务者ID：" + providerId);
			return null;
		}
		ServiceXServerSession session = new ServiceXServerSession();
		session.setSession(channel);
		session.setProviderId(providerId);
		session.setServerIp(rpcServerIp);
		session.setServerPort(rpcServerPort);
		rpcServers.put(session.getProviderId(), session);
		log.info("RPC生产者[" + providerId + "]注册到服务中心：" + channel.remoteAddress() + "，生产者中心大小：" + rpcServers.size());
		return session;
	}

	private ServiceXClientSession registerClientSession(Channel channel) {
		ServiceXClientSession session = new ServiceXClientSession();
		session.setSession(channel);
		rpcClients.put(channel.id().asLongText(), session);
		log.info("RPC消费者注册到服务中心：" + channel.remoteAddress() + "，消费者中心大小：" + rpcClients.size());
		return session;
	}

	/**
	 * 当断开链接时候处理的事情
	 * 
	 * @param ctx
	 */
	private void onDisconnect(Channel channel) {
		String asLongText = channel.id().asLongText();
		Iterator<ServiceXServerSession> serversIt = rpcServers.values().iterator();
		int providerId = 0;
		for (; serversIt.hasNext();) {
			ServiceXServerSession next = serversIt.next();
			if (next.getSession().id().asLongText().equals(asLongText)) {
				serversIt.remove();
				providerId = next.getProviderId();
				log.info("RPC生产者，providerId" + providerId + "，IP：" + channel.remoteAddress() + "断开链接，当前ProviderSize："
						+ rpcServers.size());
				// 删除注册的服务
				onProviderDisconnected(next.getProviderId());
				// 通知所有客户端
				NoticeProviderDisconnectedServiceCenterMessage msg = new NoticeProviderDisconnectedServiceCenterMessage();
				msg.setProviderUID(next.getProviderId());
				for (ServiceXClientSession session : rpcClients.values()) {
					log.info("通知客户端：" + session.getSession().remoteAddress() + "，Provider:" + providerId + "，"
							+ next.getServerIp() + ":" + next.getServerPort() + "断开链接！");
					session.getSession().writeAndFlush(msg);
				}
				break;
			}
		}
		Iterator<ServiceXClientSession> clientsIt = rpcClients.values().iterator();
		for (; clientsIt.hasNext();) {
			if (clientsIt.next().getSession().id().asLongText().equals(asLongText)) {
				log.info("删除session：" + asLongText + "，当前ClientSize：" + rpcClients.size());
				clientsIt.remove();
				break;
			}
		}
		// 清除定时任务
		Iterator<Entry<String, Set<Integer>>> iterator = schedules.entrySet().iterator();
		for (; iterator.hasNext();) {
			Entry<String, Set<Integer>> next = iterator.next();
			String scheduleName = next.getKey();
			Set<Integer> providers = next.getValue();
			if (providers.contains(providerId)) {
				providers.remove(providerId);
				log.info("断开链接，删除定时服务" + scheduleName + "的ProviderId：" + providerId + "，剩余执行者大小：" + providers.size());
			}
			if (providers.isEmpty()) {
				iterator.remove();
				try {
					if (SchedulerUtil.self().removeSchedule(scheduleName)) {
						log.info("删除定时服务：" + scheduleName + "，因为没有执行任务的provider");
					}
				} catch (SchedulerException e) {
					log.error("delete error", e);
				}
			}
		}
	}

	private void onAddSchedule(String jobName, int providerId, String cronExpression, int intervalInHours,
			int intervalInMinutes, int intervalInSeconds, int intervalInMillis, int repeatCount) {
		Set<Integer> set = schedules.get(jobName);
		if (set == null) {
			set = new ConcurrentHashSet<>();
			Set<Integer> putIfAbsent = schedules.putIfAbsent(jobName, set);
			if (putIfAbsent != null) {
				set = putIfAbsent;
			}
		}
		if (set.contains(providerId)) {
			log.error("任务" + jobName + "的Provider" + providerId + "定时任务发布重复！");
			return;
		}
		set.add(providerId);
		log.info("Provider" + providerId + "注册了一个定时任务:" + jobName);
		try {
			if (SchedulerUtil.self().hasSchedule(jobName)) {
				return;
			}
		} catch (SchedulerException e) {
			log.error("check error", e);
		}
		JobDataMap map = new JobDataMap();
		map.put(ScheduleTask.RPCSERVERS, this.rpcServers);
		map.put(ScheduleTask.SCHEDULES, this.schedules);
		if (!StringUtil.isEmptyOrNull(cronExpression)) {
			try {
				Trigger addSchedule = SchedulerUtil.self().addSchedule(jobName, ScheduleTask.class, cronExpression,
						map);
				log.info("初始化定时任务，名称：" + jobName + "表达式：" + cronExpression + "，下次执行时间："
						+ TimeUtil.date2Str(addSchedule.getNextFireTime().getTime()));
			} catch (SchedulerException e) {
				log.error("init error", e);
			}
		} else {
			try {
				Trigger addSchedule = SchedulerUtil.self().addSchedule(jobName, ScheduleTask.class, intervalInHours,
						intervalInMinutes, intervalInSeconds, intervalInMillis, repeatCount, map);
				log.info("初始化定时任务，名称：" + jobName + "，时：" + intervalInHours + "，分：" + intervalInMinutes + "，秒："
						+ intervalInSeconds + "，毫秒：" + intervalInMillis + "，重复次数：" + repeatCount + "，下次执行时间："
						+ TimeUtil.date2Str(addSchedule.getNextFireTime().getTime()));
			} catch (SchedulerException e) {
				log.error("init error", e);
			}
		}
	}

	public class SubscribeServiceFromServiceCenterConsumerHandler
			implements IHandler<SubscribeServiceFromServiceCenterConsumerMessage> {

		@Override
		public void handle(SubscribeServiceFromServiceCenterConsumerMessage msg) {
			registerClientSession(msg.getChannel());
			((ServiceCenterX) msg.getExtra()).sendAllServiceProviderInfo2Consumer(msg.getChannel());
		}

	}

	public class PushServiceToServiceCenterProviderHandler
			implements IHandler<PushServiceToServiceCenterProviderMessage> {

		@Override
		public void handle(PushServiceToServiceCenterProviderMessage msg) {
			((ServiceCenterX) msg.getExtra()).onProviderPublicServices(msg.getChannel(), msg.providerUID, msg.myIp,
					msg.myPort, msg.services);
		}

	}

	public class AddScheduleToServiceCenterProviderHandler
			implements IHandler<AddScheduleToServiceCenterProviderMessage> {

		@Override
		public void handle(AddScheduleToServiceCenterProviderMessage msg) {
			((ServiceCenterX) msg.getExtra()).onAddSchedule(msg.jobName, msg.providerId, msg.cronExpression,
					msg.intervalInHours, msg.intervalInMinutes, msg.intervalInSeconds, msg.intervalInMillis,
					msg.repeatCount);
		}

	}
}
