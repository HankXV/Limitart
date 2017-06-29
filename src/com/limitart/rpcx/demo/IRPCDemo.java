package com.limitart.rpcx.demo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.limitart.rpcx.define.ServiceX;
import com.limitart.rpcx.demo.bean.PersonInfo;


@ServiceX(provider = "limitart", module = "RPCDemo")
public interface IRPCDemo {
	String helloRpcX() throws Exception;

	String[] helloRpcXS() throws Exception;

	PersonInfo helloPerson() throws Exception;

	PersonInfo[] helloPersons() throws Exception;

	List<PersonInfo> helloPersonList() throws Exception;

	HashMap<String, PersonInfo> getPersonMap() throws Exception;

	HashSet<PersonInfo> getPersonSet() throws Exception;
}
