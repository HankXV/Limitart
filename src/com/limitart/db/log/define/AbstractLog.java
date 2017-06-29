package com.limitart.db.log.define;

import com.limitart.db.log.anotation.LogColumn;

/**
 * 基础日志
 * 
 * @author hank
 *
 */
public abstract class AbstractLog implements ILog {
	@LogColumn(type = SqlColumnType.MYSQL_bigint, comment = "记录时间")
	public long createTime = System.currentTimeMillis();
}
