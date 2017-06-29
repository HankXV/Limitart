package com.limitart.db.log.struct;

import java.util.HashMap;
import java.util.HashSet;

public class TableInfo {
	private HashMap<String, ColumnInfo> columnInfos = new HashMap<>();
	private HashSet<String> primaryKeys = new HashSet<>();

	public HashMap<String, ColumnInfo> getColumnInfos() {
		return columnInfos;
	}

	public void setColumnInfos(HashMap<String, ColumnInfo> columnInfos) {
		this.columnInfos = columnInfos;
	}

	public HashSet<String> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(HashSet<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

}
