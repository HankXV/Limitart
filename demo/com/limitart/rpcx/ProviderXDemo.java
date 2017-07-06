package com.limitart.rpcx;

import com.limitart.rpcx.providerx.ProviderX;
import com.limitart.rpcx.providerx.config.ProviderXConfig.ProviderXConfigBuilder;
import com.limitart.rpcx.providerx.listener.IProviderListener;
import com.limitart.rpcx.providerx.schedule.IProviderScheduleListener;
import com.limitart.rpcx.providerx.schedule.ProviderJob;
import com.limitart.rpcx.providerx.schedule.ProviderJob.ProviderJobBuilder;

public class ProviderXDemo {
	public static void main(String[] args) throws Exception {
		ProviderXConfigBuilder builder = new ProviderXConfigBuilder();
		builder.myIp("192.168.31.175").myPort(10002).addServicePackage("com.limitart.rpcx").providerUID(3)
				.serviceCenterIp("192.168.31.175").serviceCenterPort(5555);
		ProviderX provider = new ProviderX(builder.build(), new IProviderListener() {

			@Override
			public void onServiceCenterConnected(ProviderX provider) {
				ProviderJob job = new ProviderJobBuilder().jobName("limitart-schedule").intervalInSeconds(5)
						.repeatForever().listener(new IProviderScheduleListener() {

							@Override
							public void action() {
								System.out.println("jowiejfow");
							}
						}).build();
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
