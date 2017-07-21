package org.slingerxv.limitart.rpcx.providerx.config;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

		public ProviderXConfig build() {
			return new ProviderXConfig(this);
		}

		public ProviderXConfigBuilder myIp(String myIp) {
			this.myIp = Objects.requireNonNull(myIp, "myIp");
			return this;
		}

		public ProviderXConfigBuilder myPort(int myPort) {
			if (myPort < 1024) {
				throw new IllegalArgumentException("port must >=1024");
			}
			this.myPort = myPort;
			return this;
		}

		public ProviderXConfigBuilder serviceCenterIp(String serviceCenterIp) {
			this.serviceCenterIp = Objects.requireNonNull(serviceCenterIp, "serviceCenterIp");
			return this;
		}

		public ProviderXConfigBuilder serviceCenterPort(int serviceCenterPort) {
			if (serviceCenterPort < 1024) {
				throw new IllegalArgumentException("serviceCenterPort must >=1024");
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

		public ProviderXConfigBuilder addServicePackage(String... servicePackages) {
			for (String temp : Objects.requireNonNull(servicePackages, "servicePackages")) {
				this.servicePackage.add(Objects.requireNonNull(temp, "temp"));
			}
			return this;
		}

		public ProviderXConfigBuilder serviceImplPackages(String serviceImplPackage) {
			this.serviceImplPackages = Objects.requireNonNull(serviceImplPackage, "serviceImplPackage");
			return this;
		}
	}
}
