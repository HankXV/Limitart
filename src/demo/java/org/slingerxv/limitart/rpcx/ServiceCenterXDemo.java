package org.slingerxv.limitart.rpcx;

import org.slingerxv.limitart.rpcx.center.ServiceCenterX;
import org.slingerxv.limitart.rpcx.center.config.ServiceCenterXConfig.ServiceCenterXConfigBuilder;

public class ServiceCenterXDemo {
	public static void main(String[] args) throws Exception {
		ServiceCenterX center = new ServiceCenterX(new ServiceCenterXConfigBuilder().port(5555).build());
		center.bind();
	}
}
