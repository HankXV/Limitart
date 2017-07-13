package org.slingerxv.limitart.db.define;

/**
 * 数据库保存Dao接口
 * 
 * @author Hank
 *
 */
public interface ISQLSaveDao {
	/**
	 * 插入
	 * 
	 * @param bean
	 * @return
	 */
	int insert(ISQLBean bean);

	/**
	 * 更新
	 * 
	 * @param bean
	 * @return
	 */
	int update(ISQLBean bean);
}
