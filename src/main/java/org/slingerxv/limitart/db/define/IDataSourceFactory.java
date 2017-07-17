package org.slingerxv.limitart.db.define;

import javax.sql.DataSource;

/**
 * 获取数据库连接
 * 
 * @author hank
 *
 */
public interface IDataSourceFactory {
	DataSource getDataSource();
}
