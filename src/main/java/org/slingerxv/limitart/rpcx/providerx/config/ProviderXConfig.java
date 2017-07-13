package org.slingerxv.limitart.rpcx.providerx.config;

import java.util.HashSet;
import java.util.Set;

import org.slingerxv.limitart.util.StringUtil;

/**
 * RPC服务提供者配置
 * 
 * @author hank
 *
 */
public final class ProviderXConfig {
	private String myIp;
	private int myPort;
	private String serviceCenterIp;
	private int serviceCenterPort;
	private int providerUID;
	private String[] servicePackages;
	private String serviceImplPackages;

	private ProviderXConfig(ProviderXConfigBuilder builder) {
		this.myIp = builder.myIp;
		this.myPort = builder.myPort;
		this.serviceCenterIp = builder.serviceCenterIp;
		this.serviceCenterPort = builder.serviceCenterPort;
		if (builder.providerUID <= 0) {
			throw new IllegalArgumentException("providerUID must greater than 0");
		}
		this.providerUID = builder.providerUID;
		this.servicePackages = builder.servicePackage.toArray(new String[0]);
		this.serviceImplPackages = builder.serviceImplPackages;
	}

	public int getProviderUID() {
		return providerUID;
	}

	public String getMyIp() {
		return myIp;
	}

	public int getMyPort() {
		return myPort;
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

	public String getServiceImplPackages() {
		return serviceImplPackages;
	}

	public static class ProviderXConfigBuilder {
		private String myIp;
		private int myPort;
		private String serviceCenterIp;
		private int serviceCenterPort;
		private int providerUID;
		private Set<String> servicePackage = new HashSet<>();
		private String serviceImplPackages;

		public ProviderXConfigBuilder() {
		}

		public ProviderXConfig build() {
			return new ProviderXConfig(this);
		}

		public ProviderXConfigBuilder myIp(String myIp) {
			if (StringUtil.isEmptyOrNull(myIp)) {
				throw new NullPointerException("myIp");
			}
			this.myIp = myIp;
			return this;
		}

		public ProviderXConfigBuilder myPort(int myPort) {
			if (myPort < 1024) {
				throw new NullPointerException("port must >=1024");
			}
			this.myPort = myPort;
			return this;
		}

		public ProviderXConfigBuilder serviceCenterIp(String serviceCenterIp) {
			if (StringUtil.isEmptyOrNull(serviceCenterIp)) {
				throw new NullPointerException("serviceCenterIp");
			}
			this.serviceCenterIp = serviceCenterIp;
			return this;
		}

		public ProviderXConfigBuilder serviceCenterPort(int serviceCenterPort) {
			if (serviceCenterPort < 1024) {
				throw new NullPointerException("serviceCenterPort must >=1024");
			}
			this.serviceCenterPort = serviceCenterPort;
			return this;
		}

		public ProviderXConfigBuilder providerUID(int providerUID) {
			if (providerUID <= 0) {
				throw new IllegalArgumentException("providerUID must greater than 0");
			}
			this.providerUID = providerUID;
			return this;
		}

		public ProviderXConfigBuilder addServicePackage(String servicePackage) {
			if (StringUtil.isEmptyOrNull(servicePackage)) {
				throw new NullPointerException("servicePackage");
			}
			this.servicePackage.add(servicePackage);
			return this;
		}

		public ProviderXConfigBuilder serviceImplPackages(String serviceImplPackages) {
			this.serviceImplPackages = serviceImplPackages;
			return this;
		}
	}
}
