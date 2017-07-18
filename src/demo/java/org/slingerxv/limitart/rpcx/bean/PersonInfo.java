package org.slingerxv.limitart.rpcx.bean;

import org.slingerxv.limitart.net.binary.message.MessageMeta;

public class PersonInfo extends MessageMeta {
	public int age;
	public String name;

	@Override
	public String toString() {
		return "PersonInfo [age=" + age + ", name=" + name + "]";
	}
}
