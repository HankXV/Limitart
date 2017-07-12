package com.limitart.db.log.tablecheck;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.db.log.anotation.LogColumn;
import com.limitart.db.log.define.AbstractLog;
import com.limitart.db.log.define.ILog;
import com.limitart.db.log.define.SqlColumnType;
import com.limitart.db.log.struct.ColumnInfo;
import com.limitart.db.log.struct.TableInfo;
import com.limitart.db.log.util.LogDBUtil;
import com.limitart.reflectasm.FieldAccess;
import com.limitart.util.ReflectionUtil;
import com.limitart.util.filter.FieldFilter;

/**
 * 日志表结构变动检查器
 * 
 * @author hank
 *
 */
public class LogStructChecker {
	private static Logger log = LogManager.getLogger();
	private HashMap<String, Class<? extends ILog>> tables = new HashMap<>();
	private static Map<SqlColumnType, Set<SqlColumnType>> CHANGE_ALLOW_MAP = new HashMap<>();
	static {
		// bigint可变动列表
		Set<SqlColumnType> bigintlist = new HashSet<>();
		CHANGE_ALLOW_MAP.put(SqlColumnType.MYSQL_bigint, bigintlist);
		bigintlist.add(SqlColumnType.MYSQL_varchar);
		bigintlist.add(SqlColumnType.MYSQL_longtext);
		bigintlist.add(SqlColumnType.MYSQL_text);
		bigintlist.add(SqlColumnType.MYSQL_bigint);
		// bit可变动列表
		Set<SqlColumnType> bitlist = new HashSet<>();
		CHANGE_ALLOW_MAP.put(SqlColumnType.MYSQL_bit, bitlist);
		bitlist.add(SqlColumnType.MYSQL_longtext);
		bitlist.add(SqlColumnType.MYSQL_varchar);
		bitlist.add(SqlColumnType.MYSQL_text);
		bitlist.add(SqlColumnType.MYSQL_bigint);
		bitlist.add(SqlColumnType.MYSQL_integer);
		bitlist.add(SqlColumnType.MYSQL_int);
		bitlist.add(SqlColumnType.MYSQL_bit);
		// int可变动列表
		Set<SqlColumnType> intlist = new HashSet<>();
		CHANGE_ALLOW_MAP.put(SqlColumnType.MYSQL_int, intlist);
		CHANGE_ALLOW_MAP.put(SqlColumnType.MYSQL_integer, intlist);
		intlist.add(SqlColumnType.MYSQL_longtext);
		intlist.add(SqlColumnType.MYSQL_varchar);
		intlist.add(SqlColumnType.MYSQL_text);
		intlist.add(SqlColumnType.MYSQL_bigint);
		intlist.add(SqlColumnType.MYSQL_integer);
		intlist.add(SqlColumnType.MYSQL_int);
		// short可变动列表
		Set<SqlColumnType> shortlist = new HashSet<>();
		CHANGE_ALLOW_MAP.put(SqlColumnType.MYSQL_tinyint, shortlist);
		shortlist.add(SqlColumnType.MYSQL_longtext);
		shortlist.add(SqlColumnType.MYSQL_varchar);
		shortlist.add(SqlColumnType.MYSQL_text);
		shortlist.add(SqlColumnType.MYSQL_bigint);
		shortlist.add(SqlColumnType.MYSQL_int);
		shortlist.add(SqlColumnType.MYSQL_integer);
		shortlist.add(SqlColumnType.MYSQL_tinyint);
		// varchar变动列表
		Set<SqlColumnType> varcharlist = new HashSet<>();
		varcharlist.add(SqlColumnType.MYSQL_longtext);
		varcharlist.add(SqlColumnType.MYSQL_varchar);
		varcharlist.add(SqlColumnType.MYSQL_text);
		varcharlist.add(SqlColumnType.MYSQL_int);
		varcharlist.add(SqlColumnType.MYSQL_bigint);
		CHANGE_ALLOW_MAP.put(SqlColumnType.MYSQL_varchar, varcharlist);
		// text变动列表
		Set<SqlColumnType> text = new HashSet<>();
		CHANGE_ALLOW_MAP.put(SqlColumnType.MYSQL_text, text);
		text.add(SqlColumnType.MYSQL_longtext);
		text.add(SqlColumnType.MYSQL_text);
		text.add(SqlColumnType.MYSQL_varchar);
		// longtext变动列表
		Set<SqlColumnType> longtextlist = new HashSet<>();
		CHANGE_ALLOW_MAP.put(SqlColumnType.MYSQL_longtext, longtextlist);
		longtextlist.add(SqlColumnType.MYSQL_longtext);

	}

	public void clearTables() {
		tables.clear();
	}

	/**
	 * 注册一个bean，table名称默认为bean的简单名称小写
	 * 
	 * @param bean
	 *            JavaBean
	 * @throws Exception
	 */
	public void registTable(Class<? extends ILog> bean) throws Exception {
		String lowerCase = bean.getSimpleName().toLowerCase();
		if (tables.containsKey(lowerCase)) {
			throw new Exception("table name: " + lowerCase + " duplicated!");
		}
		tables.put(lowerCase, bean);
	}

	/**
	 * 扫描包内AbstractLog的类型
	 * 
	 * @see AbstractLog
	 * @param packageName
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void registTable(String packageName) throws Exception {
		List<Class<?>> classes = new ArrayList<>();
		try {
			classes = ReflectionUtil.getClassesByPackage(packageName, ILog.class);
		} catch (Exception e) {
			log.error(e, e);
		}
		log.debug("包：{}，共扫描类：{}个。", packageName, classes.size());
		for (Class<?> temp : classes) {
			registTable((Class<? extends ILog>) temp);
		}
	}

	/**
	 * 开始执行检查
	 * 
	 * @param con
	 * @throws Exception
	 */
	public void executeCheck(Connection con) throws Exception {
		log.info("开始检查所有日志表结构...");
		for (Class<? extends ILog> clss : tables.values()) {
			executeCheck(con, clss);
		}
		log.info("检查所有日志表结构完毕。");
	}

	private void executeCheck(Connection con, Class<? extends ILog> clss) throws Exception {
		// if (!AbstractLog.class.isAssignableFrom(clss)) {
		// return;
		// }
		// 是否是虚拟类
		if (Modifier.isAbstract(clss.getModifiers())) {
			return;
		}
		// if (clss.isInterface()) {
		// return;
		// }
		List<String> tableNames = LogDBUtil.getTableNames(con);
		for (String logTableName : tableNames) {
			if (!logTableName.startsWith(clss.getSimpleName().toLowerCase())) {
				continue;
			}
			log.info("开始检查表结构：" + logTableName);
			TableInfo columnDefine = LogDBUtil.getColumnDefine(con, logTableName);
			List<ColumnInfo> increaseList = new ArrayList<>();
			List<String> decreaseList = new ArrayList<>();
			List<ColumnInfo> modifyList = new ArrayList<>();
			// 检查增加字段
			FieldAccess logFields = LogDBUtil.getLogFields(clss);
			for (int index = 0; index < logFields.getFieldCount(); ++index) {
				Field field = logFields.getFields()[index];
				LogColumn annotation = field.getAnnotation(LogColumn.class);
				if (annotation == null) {
					continue;
				}
				// 这里要检查一下字段是否是公共的，因为ReflectASM只能反射public的字段
				if (!FieldFilter.isPublic(field)) {
					throw new Exception("日志字段：" + field.getName() + "必须为公共字段!");
				}
				String tableFieldName = LogDBUtil.getTableFieldName(logFields.getFieldNames()[index]);
				ColumnInfo info = new ColumnInfo();
				info.setTableFieldName(tableFieldName);
				info.setType(annotation.type().getValue());
				info.setSize(annotation.size());
				info.setComment(annotation.comment());
				if (!columnDefine.getColumnInfos().containsKey(tableFieldName)) {
					increaseList.add(info);
				} else {
					// 检查变更字段
					ColumnInfo source = columnDefine.getColumnInfos().get(tableFieldName);
					if (!isSame(info, source)) {
						if (ableChange(info, source)) {
							modifyList.add(info);
						} else {
							throw new Exception("检测到变动但不允许变动,表名：" + logTableName + ",新的：" + info + ",旧的：" + source);
						}
					}
				}
			}
			// 检查删除字段
			for (ColumnInfo info : columnDefine.getColumnInfos().values()) {
				if (columnDefine.getPrimaryKeys().contains(info.getTableFieldName())) {
					continue;
				}
				boolean contains = false;
				for (int index = 0; index < logFields.getFieldCount(); ++index) {
					String tableName = LogDBUtil.getTableFieldName(logFields.getFieldNames()[index]);
					Field field = logFields.getFields()[index];
					if (field.getAnnotation(LogColumn.class) != null && tableName.equals(info.getTableFieldName())) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					decreaseList.add(info.getTableFieldName());
				}
			}

			for (ColumnInfo col : increaseList) {
				try (PreparedStatement prepareStatement = con.prepareStatement(LogDBUtil.buildColumnIncreaseSql_MYSQL(
						logTableName, col.getTableFieldName(), col.getType(), col.getSize(), col.getComment()));) {
					if (prepareStatement.executeUpdate() == 0) {
						SQLException sqlException = new SQLException("表列增加变动失败，表：" + logTableName + "-----列："
								+ col.getTableFieldName() + " " + col.getType() + " " + col.getSize());
						log.error(sqlException, sqlException);
					} else {
						log.info("表列增加变动成功，表：" + logTableName + "-----列：" + col.getTableFieldName() + " "
								+ col.getType() + " " + col.getSize());
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			}
			for (String colName : decreaseList) {
				try (PreparedStatement prepareStatement = con
						.prepareStatement(LogDBUtil.buildColumnDecreaseSql_MYSQL(logTableName, colName));) {
					if (prepareStatement.executeUpdate() == 0) {
						SQLException sqlException = new SQLException(
								"表列删除变动失败，表：" + logTableName + "-----列：" + colName);
						log.error(sqlException, sqlException);
					} else {
						log.info("表列删除变动成功，表：" + logTableName + "-----列：" + colName);
					}
				} catch (Exception e) {
					log.error(e, e);
				}

			}
			for (ColumnInfo col : modifyList) {
				try (PreparedStatement prepareStatement = con.prepareStatement(LogDBUtil.buildColumnModifySql_MYSQL(
						logTableName, col.getTableFieldName(), col.getType(), col.getSize(), col.getComment()));) {
					if (prepareStatement.executeUpdate() == 0) {
						SQLException sqlException = new SQLException("表列类型变动失败，表：" + logTableName + "-----列："
								+ col.getTableFieldName() + " " + col.getType() + " " + col.getSize());
						log.error(sqlException, sqlException);
					} else {
						log.info("表列类型变动成功，表：" + logTableName + "-----列：" + col.getTableFieldName() + " "
								+ col.getType() + " " + col.getSize());
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			}
			log.info("表结构：" + logTableName + "检查完成！");
		}
	}

	public static boolean isSame(ColumnInfo now, ColumnInfo old) {
		if ((((now.getType().equals(SqlColumnType.MYSQL_int.getValue()))
				|| (now.getType().equals(SqlColumnType.MYSQL_integer.getValue()))
				|| (now.getType().startsWith(SqlColumnType.MYSQL_int.getValue()))))
				&& (((old.getType().equals(SqlColumnType.MYSQL_integer.getValue()))
						|| (old.getType().equals(SqlColumnType.MYSQL_int.getValue()))
						|| (old.getType().startsWith(SqlColumnType.MYSQL_int.getValue()))))) {
			return true;
		}

		if ((now.getType().equals(SqlColumnType.MYSQL_bigint.getValue())) && (old.getType().equals(now.getType()))) {
			return true;
		}
		if ((now.getType().equals(SqlColumnType.MYSQL_text.getValue())) && (old.getType().equals(now.getType()))) {
			return true;
		}
		if ((now.getType().equals(SqlColumnType.MYSQL_longtext.getValue())) && (old.getType().equals(now.getType()))) {
			return true;
		}
		if ((now.getType().equals(SqlColumnType.MYSQL_bit.getValue())) && (old.getType().equals(now.getType()))) {
			return true;
		}
		if ((now.getType().equals(SqlColumnType.MYSQL_tinyint.getValue())) && (old.getType().equals(now.getType()))) {
			return true;
		}
		return (now.getType().equals(old.getType())) && (now.getSize() <= old.getSize());
	}

	private boolean ableChange(ColumnInfo info, ColumnInfo info2) {
		SqlColumnType typeByValue = SqlColumnType.getTypeByValue(info.getType());
		if (typeByValue == null) {
			return false;
		}
		Set<SqlColumnType> set = CHANGE_ALLOW_MAP.get(typeByValue);
		if (set == null) {
			return false;
		}
		SqlColumnType typeByValue2 = SqlColumnType.getTypeByValue(info2.getType());
		return set.contains(typeByValue2);
	}

	public Class<? extends ILog> getTableClass(String tableName) {
		return tables.get(tableName);
	}
}
