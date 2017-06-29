package com.limitart.rpcx.demo;

import com.limitart.rpcx.center.ServiceCenterX;
import com.limitart.rpcx.center.config.ServiceCenterXConfig.ServiceCenterXConfigBuilder;

public class ServiceCenterXDemo {
	public static void main(String[] args) throws Exception {
		ServiceCenterX center = new ServiceCenterX(new ServiceCenterXConfigBuilder().port(5555).build());
		center.bind();
	}
}
