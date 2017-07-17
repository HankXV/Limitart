package org.slingerxv.limitart.db.struct;

import java.util.HashMap;
import java.util.HashSet;

public class Table {
	private HashMap<String, Column> columnInfos = new HashMap<>();
	private HashSet<String> primaryKeys = new HashSet<>();

	public HashMap<String, Column> getColumnInfos() {
		return columnInfos;
	}

	public void setColumnInfos(HashMap<String, Column> columnInfos) {
		this.columnInfos = columnInfos;
	}

	public HashSet<String> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(HashSet<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

}
