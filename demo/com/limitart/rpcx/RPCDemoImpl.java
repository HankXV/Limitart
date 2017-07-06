package com.limitart.rpcx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.limitart.rpcx.bean.PersonInfo;


public class RPCDemoImpl implements IRPCDemo {

	@Override
	public String helloRpcX() throws Exception {
		String result = "helloRpcX";
		System.out.println(result);
		return result;
	}

	@Override
	public String[] helloRpcXS() throws Exception {
		return new String[] { "1", "2" };
	}

	@Override
	public PersonInfo helloPerson() throws Exception {
		PersonInfo a = new PersonInfo();
		a.setAge(1);
		a.setName("a");
		return a;
	}

	@Override
	public List<PersonInfo> helloPersonList() throws Exception {
		return null;
	}

	@Override
	public PersonInfo[] helloPersons() throws Exception {
		PersonInfo[] result = new PersonInfo[2];
		PersonInfo a = new PersonInfo();
		a.setAge(1);
		a.setName("a");
		PersonInfo b = new PersonInfo();
		b.setAge(2);
		b.setName("b");
		result[0] = a;
		result[1] = b;
		return result;
	}

	@Override
	public HashMap<String, PersonInfo> getPersonMap() throws Exception {
		HashMap<String, PersonInfo> person = new HashMap<>();
		person.put("test", new PersonInfo());
		return person;
	}

	@Override
	public HashSet<PersonInfo> getPersonSet() throws Exception {
		HashSet<PersonInfo> set = new HashSet<>();
		set.add(new PersonInfo());
		return set;
	}

}
