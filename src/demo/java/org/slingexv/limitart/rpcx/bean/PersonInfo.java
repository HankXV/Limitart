package org.slingerxv.limitart.rpcx.bean;

import org.slingerxv.limitart.net.binary.message.MessageMeta;

public class PersonInfo extends MessageMeta {
	private int age;
	private String name;

	@Override
	public String toString() {
		return "PersonInfo [age=" + age + ", name=" + name + "]";
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void encode() throws Exception {
		putInt(this.age);
		putString(this.name);
	}

	@Override
	public void decode() throws Exception {
		this.age = getInt();
		this.name = getString();
	}

}
