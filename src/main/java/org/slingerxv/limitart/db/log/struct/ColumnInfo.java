package org.slingerxv.limitart.db.log.struct;

/**
 * 数据库列信息
 * 
 * @author hank
 *
 */
public class ColumnInfo {
	private String tableFieldName;
	private String type;
	private int size;
	private boolean nullable;
	private String comment;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getTableFieldName() {
		return tableFieldName;
	}

	public void setTableFieldName(String tableFieldName) {
		this.tableFieldName = tableFieldName;
	}

	@Override
	public String toString() {
		return "ColumnInfo [tableFieldName=" + tableFieldName + ", type=" + type + ", size=" + size + ", nullable="
				+ nullable + "]";
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}