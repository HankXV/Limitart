package org.slingerxv.limitart.rpcx;

import org.slingerxv.limitart.rpcx.providerx.ProviderX;
import org.slingerxv.limitart.rpcx.providerx.config.ProviderXConfig.ProviderXConfigBuilder;
import org.slingerxv.limitart.rpcx.providerx.listener.IProviderListener;
import org.slingerxv.limitart.rpcx.providerx.schedule.ProviderJob;
import org.slingerxv.limitart.rpcx.providerx.schedule.ProviderJob.ProviderJobBuilder;

public class ProviderXDemo {
	public static void main(String[] args) throws Exception {
		ProviderXConfigBuilder builder = new ProviderXConfigBuilder();
		builder.myIp("192.168.31.175").myPort(10002).addServicePackage("com.limitart.rpcx").providerUID(3)
				.serviceCenterIp("192.168.31.175").serviceCenterPort(5555);
		ProviderX provider = new ProviderX(builder.build(), new IProviderListener() {

			@Override
			public void onServiceCenterConnected(ProviderX provider) {
				ProviderJob job = new ProviderJobBuilder().jobName("limitart-schedule").intervalInSeconds(5)
						.repeatForever().listener(() -> System.out.println("jowiejfow")).build();
				try {
					provider.schedule(job);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onProviderBind(ProviderX provider) {

			}
		});
		provider.bind();

	}
}
