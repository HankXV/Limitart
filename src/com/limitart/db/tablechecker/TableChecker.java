package com.limitart.db.tablechecker;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.db.log.struct.ColumnInfo;
import com.limitart.db.log.struct.TableInfo;
import com.limitart.db.log.tablecheck.LogStructChecker;
import com.limitart.db.log.util.LogDBUtil;
import com.limitart.db.tablechecker.anotation.FieldCheck;
import com.limitart.db.tablechecker.anotation.TableCheck;
import com.limitart.util.ReflectionUtil;
import com.limitart.util.StringUtil;


/**
 * 数据库表结构检查工具
 * 
 * @author hank
 */
public class TableChecker {
	private static Logger log = LogManager.getLogger();
	private ConcurrentHashMap<String, Class<?>> tables = new ConcurrentHashMap<>();

	/**
	 * 从给定链接的数据库检查表结构
	 * 
	 * @category 根据有TableChecker注解的类来检查表，根据有FieldChecker注解的字段来检查表字段，如果注解上不指定表字段名
	 *           ，那么默认表字段名为类字段名
	 * @see TableCheck
	 * @see FieldCheck
	 * @param con
	 *            数据库持久链接
	 * @return 错误报告
	 * @throws SQLException
	 * 
	 */
	public TableCheckResult checkTables(Connection con, boolean checkBeanSuperClass) throws SQLException {
		TableCheckResult result = new TableCheckResult();
		StringBuilder sb = new StringBuilder();
		String line = System.getProperty("line.separator");
		sb.append(line);
		List<String> sqlTableNames = LogDBUtil.getTableNames(con);
		for (Entry<String, Class<?>> entry : tables.entrySet()) {
			String tableName = entry.getKey();
			log.info("检测表：" + tableName);
			Class<?> bean = entry.getValue();
			if (!sqlTableNames.contains(tableName)) {
				// 表缺失
				// log.error(tableName + "：表缺失！");
				sb.append(tableName + "[" + bean.getSimpleName() + "]" + "：表缺失！").append(line);
				result.error = true;
				continue;
			}
			TableInfo columnDefine = LogDBUtil.getColumnDefine(con, tableName);
			List<Field> fields = ReflectionUtil.getFields(bean, checkBeanSuperClass);
			for (Field field : fields) {
				FieldCheck annotation = field.getAnnotation(FieldCheck.class);
				if (annotation == null) {
					if (columnDefine.getColumnInfos().containsKey(field.getName())) {
						appendError(result, sb, "表：" + tableName + "[" + bean.getSimpleName() + "]" + "，字段："
								+ field.getName() + ",字段未设置检查标记");
					}
					continue;
				}
				String col = annotation.value();
				if (StringUtil.isEmptyOrNull(col)) {
					col = field.getName();
				}
				ColumnInfo now = new ColumnInfo();
				now.setTableFieldName(col);
				now.setType(annotation.type().getValue());
				now.setSize(annotation.size());
				now.setNullable(annotation.isNullable());
				if (!columnDefine.getColumnInfos().containsKey(col)) {
					// 字段缺失
					appendError(result, sb,
							"表：" + tableName + "[" + bean.getSimpleName() + "]" + "，字段：" + field.getName() + ",字段缺失");
					continue;
				}
				ColumnInfo columnInfo = columnDefine.getColumnInfos().get(col);
				if (!LogStructChecker.isSame(now, columnInfo)) {
					// 字段类型错误
					appendError(result, sb, "表：" + tableName + "[" + bean.getSimpleName() + "]" + "，字段："
							+ field.getName() + ",字段类型错误,'" + now + "'！");
					continue;
				}
				if ((annotation.primary() && !columnDefine.getPrimaryKeys().contains(col))
						|| (!annotation.primary() && columnDefine.getPrimaryKeys().contains(col))) {
					appendError(result, sb,
							"表：" + tableName + "[" + bean.getSimpleName() + "]" + "，字段：" + field.getName() + ",主键错误");
					continue;
				}
				if (columnInfo.isNullable() != now.isNullable()) {
					appendError(result, sb,
							"表：" + tableName + "[" + bean.getSimpleName() + "]" + "，字段：" + field.getName() + ",为空不一致");
					continue;
				}
			}
			for (String dbColumnName : columnDefine.getColumnInfos().keySet()) {
				boolean contains = false;
				for (Field field : fields) {
					if (field.getAnnotation(FieldCheck.class) != null && field.getName().equals(dbColumnName)) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					sb.append(tableName + "[" + bean.getSimpleName() + "]" + "：数据库多出字段：'" + dbColumnName + "'！")
							.append(line);
					result.error = true;
				}
			}
		}
		result.report = sb.toString();
		return result;
	}

	/**
	 * 清空表检查集合
	 */
	public void clearTables() {
		tables.clear();
	}

	/**
	 * 注册一个bean，table名称默认为bean名称
	 * 
	 * @param bean
	 *            JavaBean
	 * @throws Exception
	 */
	public void registTable(Class<?> bean) throws Exception {
		TableCheck annotation = bean.getAnnotation(TableCheck.class);
		if (annotation == null) {
			throw new Exception("bean need TableCheck annotation!");
		}
		String table = annotation.value();
		if (StringUtil.isEmptyOrNull(table)) {
			registTable(bean, bean.getSimpleName());
		} else {
			registTable(bean, table);
		}
	}

	/**
	 * 注册一个bean，table名为指定名称
	 * 
	 * @param bean
	 * @param tableName
	 * @throws Exception
	 */
	public void registTable(Class<?> bean, String tableName) throws Exception {
		if (tables.containsKey(tableName)) {
			throw new Exception("table name '" + tableName + "' duplicated!");
		}

		tables.put(tableName, bean);
		log.debug("添加到表检查集合:{}[{}]", tableName, bean.getSimpleName());
	}

	/**
	 * 扫描包内指定的类型（有TableChecker注解的）的bean，table名为bean名
	 * 
	 * @see TableCheck
	 * @param packageName
	 * @throws Exception
	 */
	public void registTable(String packageName, Class<?> superClass) throws Exception {
		List<Class<?>> classes = new ArrayList<>();
		try {
			classes = ReflectionUtil.getClassesByPackage(packageName, superClass);
		} catch (Exception e) {
			log.error(e, e);
		}
		log.debug("包：{}，共扫描类：{}个。", packageName, classes.size());
		for (Class<?> temp : classes) {
			TableCheck annotation = temp.getAnnotation(TableCheck.class);
			if (annotation == null) {
				log.error(new Exception("Table `" + temp.getSimpleName().toLowerCase()
						+ "` need checked!!! Use @TableCheck and @FieldCheck"));
				continue;
			}
			registTable(temp);
		}
	}

	private void appendError(TableCheckResult result, StringBuilder sb, String value) {
		sb.append(value).append(System.getProperty("line.separator"));
		result.error = true;
	}

	public class TableCheckResult {
		private boolean error = false;
		private String report;

		public boolean isError() {
			return error;
		}

		public String getReport() {
			return report;
		}
	}
}
