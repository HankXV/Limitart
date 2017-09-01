package org.slingerxv.limitart.rpcx;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slingerxv.limitart.rpcx.bean.PersonInfo;
import org.slingerxv.limitart.rpcx.define.ServiceX;


@ServiceX(provider = "limitart", module = "RPCDemo")
public interface IRPCDemo {
	String helloRpcX() throws Exception;

	String[] helloRpcXS() throws Exception;

	PersonInfo helloPerson() throws Exception;

	PersonInfo[] helloPersons() throws Exception;

	List<PersonInfo> helloPersonList() throws Exception;

	Map<String, PersonInfo> getPersonMap() throws Exception;

	Set<PersonInfo> getPersonSet() throws Exception;
}
