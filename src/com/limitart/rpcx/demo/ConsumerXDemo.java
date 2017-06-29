package com.limitart.rpcx.demo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.limitart.rpcx.consumerx.ConsumerX;
import com.limitart.rpcx.consumerx.config.ConsumerXConfig.ConsumerXConfigBuilder;
import com.limitart.rpcx.demo.bean.PersonInfo;


public class ConsumerXDemo {
	public static void main(String[] args) throws Exception {
		ConsumerXConfigBuilder builder = new ConsumerXConfigBuilder();
		builder.addServicePackage("limitart.core.rpcx.demo");
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
				HashMap<String, PersonInfo> personMap = createProxy.getPersonMap();
				System.out.println(personMap.get("test"));
				HashSet<PersonInfo> personSet = createProxy.getPersonSet();
				System.out.println(personSet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
