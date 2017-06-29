package com.limitart.db.log.define;

import javax.sql.DataSource;

/**
 * 获取数据库连接
 * 
 * @author hank
 *
 */
public interface IDataSourceFactory {
	public DataSource getDataSource();
}
