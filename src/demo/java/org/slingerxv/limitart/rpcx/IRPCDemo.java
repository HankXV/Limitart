package org.slingerxv.limitart.rpcx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.slingerxv.limitart.rpcx.bean.PersonInfo;
import org.slingerxv.limitart.rpcx.define.ServiceX;


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
