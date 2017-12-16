package org.slingerxv.limitart.rpcx;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slingerxv.limitart.rpcx.bean.PersonInfo;
import org.slingerxv.limitart.rpcx.consumerx.ConsumerX;
import org.slingerxv.limitart.rpcx.consumerx.config.ConsumerXConfig.ConsumerXConfigBuilder;


public class ConsumerXDemo {
	public static void main(String[] args) throws Exception {
		ConsumerXConfigBuilder builder = new ConsumerXConfigBuilder();
		builder.addServicePackage("com.limitart.rpcx");
		// builder.appendProviderRemote(new ProviderRemote("192.168.3.100",
		// 10000));
		builder.serviceCenterIp("192.168.31.175").serviceCenterPort(5555);
		ConsumerX consumer = new ConsumerX(builder.build());
		consumer.init();
		IRPCDemo createProxy = consumer.createProxy(IRPCDemo.class);
		while (true) {
			Thread.sleep(1000);
			try {
				System.out.println(createProxy.helloRpcX());
				for (String temp : createProxy.helloRpcXS()) {
					System.out.println(temp);
				}
				PersonInfo helloPerson2 = createProxy.helloPerson();
				System.out.println(helloPerson2);
				PersonInfo[] helloPerson = createProxy.helloPersons();
				for (PersonInfo info : helloPerson) {
					System.out.println(info);
				}
				List<PersonInfo> helloPersonList = createProxy.helloPersonList();
				System.out.println(helloPersonList);
				Map<String, PersonInfo> personMap = createProxy.getPersonMap();
				System.out.println(personMap.get("test"));
				Set<PersonInfo> personSet = createProxy.getPersonSet();
				System.out.println(personSet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
