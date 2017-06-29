package com.limitart.db.log.define;

import com.limitart.db.log.anotation.LogColumn;
import com.limitart.net.binary.message.MessageMeta;

public abstract class AbstractMessageLog extends MessageMeta implements ILog {
	@LogColumn(type = SqlColumnType.MYSQL_bigint, comment = "记录时间")
	public long createTime = System.currentTimeMillis();
}
