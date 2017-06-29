package com.limitart.db.log.define;

/**
 * 日志滚动类型
 * 
 * @author hank
 *
 */
public enum LogRollType {
	/**
	 * 日表
	 */
	DAY_ROLL(1),
	/**
	 * 月表
	 */
	MONTH_ROLL(2),
	/**
	 * 年表
	 */
	YEAR_ROLL(3),
	/**
	 * 固定
	 */
	NEVER_ROLL(4);
	private int value;

	LogRollType(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
