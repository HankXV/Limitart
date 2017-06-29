package com.limitart.db.log.define;

public interface ILog {
	/**
	 * 设置日志滚动类型
	 * 
	 * @return
	 */
	public abstract LogRollType getLogRollType();
}
