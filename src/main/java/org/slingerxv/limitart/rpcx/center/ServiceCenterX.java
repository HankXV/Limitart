package org.slingerxv.limitart.rpcx.center;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slingerxv.limitart.collections.ConcurrentHashSet;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.net.binary.message.MessageFactory;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.binary.server.BinaryServer;
import org.slingerxv.limitart.net.binary.server.config.BinaryServerConfig;
import org.slingerxv.limitart.net.binary.server.listener.BinaryServerEventListener;
import org.slingerxv.limitart.net.struct.AddressPair;
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
	private static Logger log = LogManager.getLogger();
	private ServiceCenterXConfig config;
	private BinaryServer binaryServer;
	// RPC服务器组<提供者Id,session>
	private ConcurrentHashMap<Integer, ServiceXServerSession> rpcServers = new ConcurrentHashMap<>();
	// RPC客户端组<客户端ChannelId,session>
	private ConcurrentHashMap<String, ServiceXClientSession> rpcClients = new ConcurrentHashMap<>();
	// 服务名称-服务器集合(ProviderId)
	private ConcurrentHashMap<String, ConcurrentHashSet<Integer>> service2Providers = new ConcurrentHashMap<>();
	// 定时任务集合
	private ConcurrentHashMap<String, ConcurrentHashSet<Integer>> schedules = new ConcurrentHashMap<>();

	public ServiceCenterX(ServiceCenterXConfig config)
			throws InstantiationException, IllegalAccessException, MessageIDDuplicatedException {
		if (config == null) {
			throw new NullPointerException("ServiceCenterXConfig");
		}
		this.config = config;
		BinaryServerConfig serverConfig = new BinaryServerConfig.BinaryServerConfigBuilder()
				.addressPair(new AddressPair(config.getPort()))
				.factory(new MessageFactory().registerMsg(new SubscribeServiceFromServiceCenterConsumerHandler())
						.registerMsg(new PushServiceToServiceCenterProviderHandler())
						.registerMsg(new AddScheduleToServiceCenterProviderHandler()))
				.build();
		binaryServer = new BinaryServer(serverConfig, new BinaryServerEventListener() {

			@Override
			public void onExceptionCaught(Channel channel, Throwable cause) {
				log.error(cause, cause);
			}

			@Override
			public void onChannelInactive(Channel channel) {
				onDisconnect(channel);
			}

			@Override
			public void onChannelActive(Channel channel) {

			}

			@Override
			public void dispatchMessage(Message message) {
				message.setExtra(this);
				message.getHandler().handle(message);
			}

			@Override
			public void onConnectionEffective(Channel channel) {
			}

			@Override
			public void onServerBind(Channel channel) {

			}
		});
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
			ConcurrentHashSet<Integer> rpcServiceLBData = service2Providers.get(serviceName);
			if (rpcServiceLBData == null) {
				rpcServiceLBData = new ConcurrentHashSet<>();
				ConcurrentHashSet<Integer> putIfAbsent = service2Providers.putIfAbsent(serviceName, rpcServiceLBData);
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
		for (Entry<String, ConcurrentHashSet<Integer>> entry : service2Providers.entrySet()) {
			ConcurrentHashSet<Integer> data = entry.getValue();
			if (!data.contains(providerId)) {
				continue;
			}
			ProviderServiceMeta serviceMeta = new ProviderServiceMeta();
			serviceMeta.setServiceName(entry.getKey());
			for (int tpid : data) {
				ServiceXServerSession serviceXServerSession = rpcServers.get(tpid);
				if (serviceXServerSession != null) {
					ProviderHostMeta hostMeta = new ProviderHostMeta();
					hostMeta.setIp(serviceXServerSession.getServerIp());
					hostMeta.setPort(serviceXServerSession.getServerPort());
					hostMeta.setProviderId(serviceXServerSession.getProviderId());
					serviceMeta.getHostInfos().add(hostMeta);
				}
			}
			msg.getServices().add(serviceMeta);
		}
		// 查找所有客户端
		for (ServiceXClientSession session : rpcClients.values()) {
			session.getSession().writeAndFlush(msg);
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
		for (Entry<String, ConcurrentHashSet<Integer>> entry : service2Providers.entrySet()) {
			String serviceName = entry.getKey();
			ConcurrentHashSet<Integer> data = entry.getValue();
			ProviderServiceMeta info = new ProviderServiceMeta();
			msg.getServices().add(info);
			info.setServiceName(serviceName);
			for (int providerId : data) {
				ServiceXServerSession serviceXServerSession = rpcServers.get(providerId);
				if (serviceXServerSession != null) {
					ProviderHostMeta hostMeta = new ProviderHostMeta();
					hostMeta.setIp(serviceXServerSession.getServerIp());
					hostMeta.setPort(serviceXServerSession.getServerPort());
					hostMeta.setProviderId(serviceXServerSession.getProviderId());
					info.getHostInfos().add(hostMeta);
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
		for (Entry<String, ConcurrentHashSet<Integer>> entry : service2Providers.entrySet()) {
			String serviceName = entry.getKey();
			ConcurrentHashSet<Integer> data = entry.getValue();
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
			log.error(new Exception("服务提供者ID重复，断开链接，IP：" + channel.remoteAddress() + "，服务者ID：" + providerId));
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
		Iterator<Entry<String, ConcurrentHashSet<Integer>>> iterator = schedules.entrySet().iterator();
		for (; iterator.hasNext();) {
			Entry<String, ConcurrentHashSet<Integer>> next = iterator.next();
			String scheduleName = next.getKey();
			ConcurrentHashSet<Integer> providers = next.getValue();
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
					log.error(e, e);
				}
			}
		}
	}

	private void onAddSchedule(String jobName, int providerId, String cronExpression, int intervalInHours,
			int intervalInMinutes, int intervalInSeconds, int intervalInMillis, int repeatCount) {
		ConcurrentHashSet<Integer> concurrentHashSet = schedules.get(jobName);
		if (concurrentHashSet == null) {
			concurrentHashSet = new ConcurrentHashSet<>();
			ConcurrentHashSet<Integer> putIfAbsent = schedules.putIfAbsent(jobName, concurrentHashSet);
			if (putIfAbsent != null) {
				concurrentHashSet = putIfAbsent;
			}
		}
		if (concurrentHashSet.contains(providerId)) {
			log.error("任务" + jobName + "的Provider" + providerId + "定时任务发布重复！");
			return;
		}
		concurrentHashSet.add(providerId);
		log.info("Provider" + providerId + "注册了一个定时任务:" + jobName);
		try {
			if (SchedulerUtil.self().hasSchedule(jobName)) {
				return;
			}
		} catch (SchedulerException e) {
			log.error(e, e);
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
				log.error(e, e);
			}
		} else {
			try {
				Trigger addSchedule = SchedulerUtil.self().addSchedule(jobName, ScheduleTask.class, intervalInHours,
						intervalInMinutes, intervalInSeconds, intervalInMillis, repeatCount, map);
				log.info("初始化定时任务，名称：" + jobName + "，时：" + intervalInHours + "，分：" + intervalInMinutes + "，秒："
						+ intervalInSeconds + "，毫秒：" + intervalInMillis + "，重复次数：" + repeatCount + "，下次执行时间："
						+ TimeUtil.date2Str(addSchedule.getNextFireTime().getTime()));
			} catch (SchedulerException e) {
				log.error(e, e);
			}
		}
	}

	private class SubscribeServiceFromServiceCenterConsumerHandler
			implements IHandler<SubscribeServiceFromServiceCenterConsumerMessage> {

		@Override
		public void handle(SubscribeServiceFromServiceCenterConsumerMessage msg) {
			registerClientSession(msg.getChannel());
			((ServiceCenterX) msg.getExtra()).sendAllServiceProviderInfo2Consumer(msg.getChannel());
		}

	}

	private class PushServiceToServiceCenterProviderHandler
			implements IHandler<PushServiceToServiceCenterProviderMessage> {

		@Override
		public void handle(PushServiceToServiceCenterProviderMessage msg) {
			((ServiceCenterX) msg.getExtra()).onProviderPublicServices(msg.getChannel(), msg.getProviderUID(),
					msg.getMyIp(), msg.getMyPort(), msg.getServices());
		}

	}

	private class AddScheduleToServiceCenterProviderHandler
			implements IHandler<AddScheduleToServiceCenterProviderMessage> {

		@Override
		public void handle(AddScheduleToServiceCenterProviderMessage msg) {
			((ServiceCenterX) msg.getExtra()).onAddSchedule(msg.getJobName(), msg.getProviderId(),
					msg.getCronExpression(), msg.getIntervalInHours(), msg.getIntervalInMinutes(),
					msg.getIntervalInSeconds(), msg.getIntervalInMillis(), msg.getRepeatCount());
		}

	}
}
