package org.slingerxv.limitart.rpcx.consumerx.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slingerxv.limitart.rpcx.consumerx.selector.define.IProviderSelector;
import org.slingerxv.limitart.rpcx.consumerx.struct.ProviderRemote;
import org.slingerxv.limitart.util.StringUtil;

/**
 * RPC服务消费者配置
 * 
 * @author hank
 *
 */
public final class ConsumerXConfig {
	private ProviderRemote[] providerRemotes;
	private String serviceCenterIp;
	private int serviceCenterPort;
	private String[] servicePackages;
	private int rpcExecuteTimeoutInMills;
	private int rpcCallBackMaxLength;
	private IProviderSelector selector;
	private int autoConnectInterval;

	private ConsumerXConfig(ConsumerXConfigBuilder builder) {
		if (builder.providerRemotes != null && !builder.providerRemotes.isEmpty()) {
			providerRemotes = builder.providerRemotes.toArray(new ProviderRemote[0]);
		}
		this.serviceCenterIp = builder.serviceCenterIp;
		this.serviceCenterPort = builder.serviceCenterPort;
		this.servicePackages = builder.servicePackages.toArray(new String[0]);
		this.rpcExecuteTimeoutInMills = builder.rpcExecuteTimeoutInMills;
		this.rpcCallBackMaxLength = builder.rpcCallBackMaxLength;
		this.selector = builder.selector;
		this.autoConnectInterval = builder.autoConnectInterval;
	}

	public int getRpcCallBackMaxLength() {
		return rpcCallBackMaxLength;
	}

	public int getRpcExecuteTimeoutInMills() {
		return rpcExecuteTimeoutInMills;
	}

	public IProviderSelector getSelector() {
		return selector;
	}

	public ProviderRemote[] getProviderRemotes() {
		return providerRemotes;
	}

	public String getServiceCenterIp() {
		return serviceCenterIp;
	}

	public int getServiceCenterPort() {
		return serviceCenterPort;
	}

	public String[] getServicePackages() {
		return servicePackages;
	}

	public int getAutoConnectInterval() {
		return autoConnectInterval;
	}

	public static class ConsumerXConfigBuilder {
		private List<ProviderRemote> providerRemotes;
		private String serviceCenterIp;
		private int serviceCenterPort;
		private Set<String> servicePackages = new HashSet<>();
		private int rpcExecuteTimeoutInMills;
		private int rpcCallBackMaxLength;
		private IProviderSelector selector;
		private int autoConnectInterval;

		public ConsumerXConfigBuilder() {
			rpcExecuteTimeoutInMills = 60 * 1000;
			rpcCallBackMaxLength = 1000;
			selector = IProviderSelector.DEFAULT;
			autoConnectInterval = 0;
		}

		public ConsumerXConfig build() {
			return new ConsumerXConfig(this);
		}

		public ConsumerXConfigBuilder appendProviderRemote(ProviderRemote remote) {
			if (remote == null) {
				throw new NullPointerException("ProviderRemote");
			}
			if (StringUtil.isEmptyOrNull(remote.getProviderIp())) {
				throw new NullPointerException("providerIp");
			}
			if (remote.getProviderPort() < 1024) {
				throw new NullPointerException("providerPort must >=1024");
			}
			if (providerRemotes == null) {
				providerRemotes = new ArrayList<>();
			}
			providerRemotes.add(remote);
			return this;
		}

		public ConsumerXConfigBuilder serviceCenterIp(String serviceCenterIp) {
			if (StringUtil.isEmptyOrNull(serviceCenterIp)) {
				throw new NullPointerException("serviceCenterIp");
			}
			this.serviceCenterIp = serviceCenterIp;
			return this;
		}

		public ConsumerXConfigBuilder serviceCenterPort(int serviceCenterPort) {
			if (serviceCenterPort < 1024) {
				throw new NullPointerException("serviceCenterPort must >=1024");
			}
			this.serviceCenterPort = serviceCenterPort;
			return this;
		}

		public ConsumerXConfigBuilder addServicePackage(String servicePackage) {
			if (StringUtil.isEmptyOrNull(servicePackage)) {
				throw new NullPointerException("servicePackage");
			}
			this.servicePackages.add(servicePackage);
			return this;
		}

		public ConsumerXConfigBuilder selector(IProviderSelector selector) {
			if (selector == null) {
				throw new NullPointerException("IProviderSelector");
			}
			this.selector = selector;
			return this;
		}

		public ConsumerXConfigBuilder rpcExecuteTimeoutInMills(int rpcExecuteTimeoutInMills) {
			this.rpcExecuteTimeoutInMills = rpcExecuteTimeoutInMills;
			return this;
		}

		public ConsumerXConfigBuilder rpcCallBackMaxLength(int rpcCallBackMaxLength) {
			this.rpcCallBackMaxLength = rpcCallBackMaxLength;
			return this;
		}

		public ConsumerXConfigBuilder autoConnectInterval(int autoConnectInterval) {
			this.autoConnectInterval = autoConnectInterval;
			return this;
		}
	}
}
