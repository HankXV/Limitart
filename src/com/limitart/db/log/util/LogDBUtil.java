package com.limitart.db.log.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.db.log.anotation.LogColumn;
import com.limitart.db.log.define.ILog;
import com.limitart.db.log.define.LogRollType;
import com.limitart.db.log.define.SqlColumnType;
import com.limitart.db.log.struct.ColumnInfo;
import com.limitart.db.log.struct.TableInfo;
import com.limitart.reflectasm.FieldAccess;
import com.limitart.util.StringUtil;
import com.limitart.util.filter.FieldFilter;

/**
 * 日志辅助类
 * 
 * @author hank
 *
 */
public class LogDBUtil {
	private static Logger log = LogManager.getLogger();
	private static String PRIMARY_KEY_FIELD_NAME = "`pk_id`";

	private static ConcurrentHashMap<Class<? extends ILog>, FieldAccess> logFieldCache = new ConcurrentHashMap<>();

	public static String log2JSON(ILog alog) {
		return StringUtil.toJSONWithClassInfo(alog);
	}

	/**
	 * 获取日志头信息
	 * 
	 * @param clss
	 *            日志类
	 * @return [字段名，字段解释]
	 */
	public static List<String[]> getLogHeader(Class<? extends ILog> clss) {
		List<String[]> result = new ArrayList<>();
		FieldAccess fieldAccessV2 = getLogFields(clss);
		for (int index = 0; index < fieldAccessV2.getFieldCount(); ++index) {
			Field field = fieldAccessV2.getFields()[index];
			LogColumn annotation = field.getAnnotation(LogColumn.class);
			if (annotation == null) {
				continue;
			}
			String[] temp = new String[] { fieldAccessV2.getFieldNames()[index], annotation.comment() };
			result.add(temp);
		}
		return result;
	}

	/**
	 * 通过字段获取表字段名
	 * 
	 * @param fieldName
	 *            字段名称
	 * @return 表列名
	 */
	public static String getTableFieldName(String fieldName) {
		return fieldName;
	}

	/**
	 * 从数据库获取表名
	 * 
	 * @param conn
	 *            数据库链接
	 * @return 数据库表名列表
	 * @throws SQLException
	 */
	public static List<String> getTableNames(Connection conn) throws SQLException {
		ResultSet tableRet = conn.getMetaData().getTables(null, "%", "%", null);
		List<String> tablenames = new ArrayList<String>();
		while (tableRet.next()) {
			tablenames.add(tableRet.getString("TABLE_NAME"));
		}
		return tablenames;
	}

	/**
	 * 从数据库获取列定义
	 * 
	 * @param conn
	 *            数据库链接
	 * @param tableName
	 *            数据库表名
	 * @return 表信息
	 * @throws SQLException
	 */
	public static TableInfo getColumnDefine(Connection conn, String tableName) throws SQLException {
		TableInfo tableInfo = new TableInfo();
		DatabaseMetaData metaData = conn.getMetaData();
		ResultSet columns = metaData.getColumns(null, "%", tableName, "%");
		ResultSet primaryKey = metaData.getPrimaryKeys(null, "%", tableName);
		while (primaryKey.next()) {
			tableInfo.getPrimaryKeys().add(primaryKey.getString(4));
		}
		while (columns.next()) {
			ColumnInfo info = new ColumnInfo();
			info.setTableFieldName(columns.getString("COLUMN_NAME"));
			info.setType(columns.getString("TYPE_NAME").toLowerCase());
			info.setSize(columns.getInt("COLUMN_SIZE"));
			info.setNullable(columns.getBoolean("IS_NULLABLE"));
			tableInfo.getColumnInfos().put(info.getTableFieldName(), info);
		}
		return tableInfo;
	}

	/**
	 * 获取此种日志当前带日期的名称
	 * 
	 * @param alog
	 * @return
	 */
	public static String getLogTableName(ILog alog, long millTime) {
		LogRollType logRollType = alog.getLogRollType();
		String tableName = alog.getClass().getSimpleName().toLowerCase();
		switch (logRollType) {
		case DAY_ROLL:
			tableName = tableName + new SimpleDateFormat("yyyyMMdd").format(new Date(millTime));
			break;
		case MONTH_ROLL:
			tableName = tableName + new SimpleDateFormat("yyyyMM").format(new Date(millTime));
			break;
		case YEAR_ROLL:
			tableName = tableName + new SimpleDateFormat("yyyy").format(new Date(millTime));
			break;
		case NEVER_ROLL:
			break;
		}
		return tableName;
	}

	/**
	 * 通过开始时间和结束时间查找相关表
	 * 
	 * @param alog
	 * @param start
	 * @param end
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static Set<String> getRelativeTableNames(Class<? extends ILog> alog, long start, long end)
			throws InstantiationException, IllegalAccessException {
		Calendar startCal = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
		startCal.setTimeInMillis(start);
		startCal.set(Calendar.DAY_OF_MONTH, 1);
		startCal.clear(Calendar.HOUR_OF_DAY);
		startCal.clear(Calendar.MINUTE);
		startCal.clear(Calendar.SECOND);
		startCal.clear(Calendar.MILLISECOND);
		Set<String> result = new HashSet<>();
		ILog newInstance = alog.newInstance();
		do {
			String logTableName = getLogTableName(newInstance, startCal.getTimeInMillis());
			LogRollType logRollType = newInstance.getLogRollType();
			if (logRollType == LogRollType.DAY_ROLL) {
				if (startCal.getTimeInMillis() >= start) {
					result.add(logTableName);
				}
				startCal.add(Calendar.DAY_OF_YEAR, 1);
			} else if (logRollType == LogRollType.MONTH_ROLL) {
				result.add(logTableName);
				startCal.add(Calendar.MONTH, 1);
			} else if (logRollType == LogRollType.YEAR_ROLL) {
				result.add(logTableName);
				startCal.add(Calendar.YEAR, 1);
			} else if (logRollType == LogRollType.NEVER_ROLL) {
				result.add(logTableName);
				break;
			} else {
				break;
			}
		} while (startCal.getTimeInMillis() <= end);
		return result;
	}

	/**
	 * 构建表是否存在检测语句
	 * 
	 * @param tableName
	 *            数据库表名
	 * @return
	 */
	public static String buildExistTableSql_MYSQL(String tableName) {
		String sql = "SHOW TABLES  LIKE '" + tableName + "'";
		log.debug(sql);
		return sql;
	}

	/**
	 * 构建查找数量SQL
	 * 
	 * @param builder
	 *            构造器
	 * @return sql语句
	 * @throws Exception
	 */
	public static String buildSelectCountTableSql_MYSQL(QueryConditionBuilder builder) throws Exception {
		String build = builder.build();
		log.debug(build);
		return build;
	}

	/**
	 * 构建表查询语句
	 * 
	 * @param tableName
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public static String buildSelectTableSql_MYSQL(QueryConditionBuilder builder) throws Exception {
		String build = builder.build();
		log.debug(build);
		return build;
	}

	/**
	 * 创建建表Sql
	 * 
	 * @param alog
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String buildCreateTableSql_MYSQL(ILog alog, String dbEngine, String charset)
			throws UnsupportedEncodingException {
		StringBuilder createTableBuffer = new StringBuilder();
		String tableName = getLogTableName(alog, System.currentTimeMillis());
		FieldAccess fieldAccessV2 = getLogFields(alog.getClass());
		createTableBuffer.append("create table if not exists ").append(tableName).append(" (").append(line());
		createTableBuffer.append(PRIMARY_KEY_FIELD_NAME + " int primary key not null auto_increment");
		for (int index = 0; index < fieldAccessV2.getFieldCount(); ++index) {
			Field field = fieldAccessV2.getFields()[index];
			LogColumn annotation = field.getAnnotation(LogColumn.class);
			if (annotation == null) {
				continue;
			}
			SqlColumnType type = annotation.type();
			int size = annotation.size();
			if (type == SqlColumnType.MYSQL_varchar) {
				if (size <= 0) {
					size = 255;
				}
			}
			String sqlType = type.getValue();
			String sizeStr = size > 0 ? "(" + size + ")" : "";
			String comment = annotation.comment();
			String tableFieldName = "`" + LogDBUtil.getTableFieldName(fieldAccessV2.getFieldNames()[index]) + "`";
			createTableBuffer.append(",").append(line()).append(tableFieldName).append(" ").append(sqlType)
					.append(sizeStr).append(" null comment ").append("'").append(comment).append("'");
		}
		createTableBuffer.append(")");
		createTableBuffer.append("engine=" + dbEngine + " auto_increment=1 default charset=" + charset + " comment '")
				.append(alog.getClass().getSimpleName()).append("'");
		String sql = createTableBuffer.toString();
		log.debug(sql);
		return sql;
	}

	/**
	 * 创建插入Sql
	 * 
	 * @param aLog
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static String buildInsertTableSql_MYSQL(ILog alog) throws IllegalArgumentException, IllegalAccessException {
		StringBuilder fieldBuffer = new StringBuilder();
		StringBuilder valueBuffer = new StringBuilder();
		String tableName = getLogTableName(alog, System.currentTimeMillis());
		FieldAccess fieldAccessV2 = getLogFields(alog.getClass());
		for (int i = 0; i < fieldAccessV2.getFieldCount(); ++i) {
			String tableFieldName = "`" + LogDBUtil.getTableFieldName(fieldAccessV2.getFieldNames()[i]) + "`";
			Object object = fieldAccessV2.get(alog, i);
			String parseFieldValueType = parseLogField2String(object);
			fieldBuffer.append(tableFieldName).append(",");
			valueBuffer.append(parseFieldValueType).append(",");
		}
		fieldBuffer.deleteCharAt(fieldBuffer.length() - 1);
		valueBuffer.deleteCharAt(valueBuffer.length() - 1);
		StringBuilder insertTableBuffer = new StringBuilder();
		insertTableBuffer.append("insert into `").append(tableName).append("`(").append(fieldBuffer)
				.append(") values (").append(valueBuffer).append(")");
		String sql = insertTableBuffer.toString();
		log.debug(sql);
		return sql;
	}

	/**
	 * 创建列增加Sql
	 * 
	 * @param tableName
	 * @param fieldNameAndType
	 * @return
	 */
	public static String buildColumnIncreaseSql_MYSQL(String tableName, String fieldName, String type, int size,
			String comment) {
		String sql = "alter table `" + tableName + "` add column `" + fieldName + "` " + type
				+ (size > 0 ? "(" + size + ")" : type.equals("varchar") ? "(255)" : "") + " comment '" + comment + "';";
		log.debug(sql);
		return sql;
	}

	/**
	 * 创建列删除Sql
	 * 
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	public static String buildColumnDecreaseSql_MYSQL(String tableName, String fieldName) {
		String sql = "alter table `" + tableName + "` drop column `" + fieldName + "`;";
		log.debug(sql);
		return sql;
	}

	/**
	 * 创建列更改Sql
	 * 
	 * @param tableName
	 * @param fieldNameAndType
	 * @return
	 */
	public static String buildColumnModifySql_MYSQL(String tableName, String fieldName, String type, int size,
			String comment) {
		String sql = "alter table `" + tableName + "` modify column `" + fieldName + "` " + type
				+ (size > 0 ? "(" + size + ")" : type.equals("varchar") ? "(255)" : "") + " comment '" + comment + "';";
		log.debug(sql);
		return sql;
	}

	/**
	 * 把日志相应的字段转换为字符串
	 * 
	 * @param object
	 * @return
	 */
	public static String parseLogField2String(Object object) {
		if (object == null) {
			return "null";
		}
		if (object instanceof Date) {
			return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(object) + "'";
		}
		if ((object instanceof Integer) || (object instanceof String) || (object instanceof Long)
				|| (object instanceof Short)) {
			return "'" + object.toString() + "'";
		}
		String result = "'" + StringUtil.toJSONWithClassInfo(object) + "'";
		log.debug(result);
		return result;
	}

	private static String line() {
		return System.getProperty("line.separator");
	}

	public static FieldAccess getLogFields(Class<? extends ILog> logClass) {
		if (logFieldCache.containsKey(logClass)) {
			return logFieldCache.get(logClass);
		}
		FieldAccess fieldAccessV2 = FieldAccess.get(logClass, true, new FieldFilter() {

			@Override
			public boolean filter(Field field) {
				if (FieldFilter.isStatic(field)) {
					return false;
				}
                return field.getAnnotation(LogColumn.class) != null;
            }
		});
		logFieldCache.put(logClass, fieldAccessV2);
		return fieldAccessV2;
	}

	public static int getCacheLogFieldsSize() {
		return logFieldCache.size();
	}

	/**
	 * 日志查询构造器
	 * 
	 * @author hank
	 *
	 */
	public static final class QueryConditionBuilder {
		private StringBuilder selections = new StringBuilder();
		private StringBuilder tableNames = new StringBuilder();
		private String where;
		private StringBuilder orderBySb = new StringBuilder();
		private StringBuilder groupBySb = new StringBuilder();
		private String limit;
		private List<QueryConditionBuilder> unions = new ArrayList<>();

		public QueryConditionBuilder unionAll(QueryConditionBuilder builder) throws Exception {
			if (builder.hashCode() == hashCode()) {
				throw new Exception("can not add self!");
			}
			unions.add(builder);
			return this;
		}

		public QueryConditionBuilder select(String value) {
			selections.append(value).append(",");
			return this;
		}

		public QueryConditionBuilder tables(Collection<String> tables) {
			if (tables != null) {
				for (String tb : tables) {
					tableNames.append(tb).append(",");
				}
			}
			return this;
		}

		public QueryConditionBuilder tables(QueryConditionBuilder table) throws Exception {
			if (table.hashCode() == hashCode()) {
				throw new Exception("can not add self!");
			}
			tableNames.append("(" + table.build() + ") as atlas_" + Integer.toHexString(table.hashCode())).append(",");
			return this;
		}

		public QueryConditionBuilder tables(String... tables) {
			if (tables != null) {
				for (String table : tables) {
					if (table != null) {
						tableNames.append(table).append(",");
					}
				}
			}
			return this;
		}

		public QueryConditionBuilder where(WhereConditionBuilder condition) throws Exception {
			where = condition.build();
			return this;
		}

		public QueryConditionBuilder limit(int start, int size) {
			limit = "limit " + start + "," + size;
			return this;
		}

		public QueryConditionBuilder orderBy(String fieldName, boolean desc) {
			orderBySb.append(fieldName).append(desc ? " desc" : " asc").append(",");
			return this;
		}

		public QueryConditionBuilder groupBy(String fieldName) {
			groupBySb.append(fieldName).append(",");
			return this;
		}

		private String build() throws Exception {
			String source = "select {0} from {1} {2} {3} {4} {5}";
			if (selections.length() == 0) {
				throw new Exception("no selection item!");
			}
			StringBuilder selectionsCopy = new StringBuilder(selections.toString());
			selectionsCopy.deleteCharAt(selectionsCopy.length() - 1);
			if (tableNames.length() == 0) {
				throw new Exception("no table item!");
			}
			StringBuilder tableNamesCopy = new StringBuilder(tableNames.toString());
			tableNamesCopy.deleteCharAt(tableNamesCopy.length() - 1);
			StringBuilder groupBySbCopy = new StringBuilder(groupBySb.toString());
			if (groupBySb.length() != 0) {
				groupBySbCopy.deleteCharAt(groupBySbCopy.length() - 1);
			}
			StringBuilder orderBySbCopy = new StringBuilder(orderBySb.toString());
			if (orderBySb.length() != 0) {
				orderBySbCopy.deleteCharAt(orderBySbCopy.length() - 1);
			}
			String format = MessageFormat.format(source, selectionsCopy.toString(), tableNamesCopy.toString(),
					where == null ? "" : "where " + where,
					groupBySbCopy.length() == 0 ? "" : "group by" + groupBySbCopy.toString(),
					orderBySbCopy.length() == 0 ? "" : "order by " + orderBySbCopy.toString(),
					limit == null ? "" : limit);
			if (!unions.isEmpty()) {
				for (QueryConditionBuilder temp : unions) {
					format += " union all " + temp.build();
				}
			}
			return format;
		}

		public static final class WhereConditionBuilder {
			private StringBuilder sb = new StringBuilder();
			private int qouteSignal = 0;
			private int contactSignal = 0;

			public WhereConditionBuilder qouteStart() {
				sb.append("(");
				++qouteSignal;
				return this;
			}

			public WhereConditionBuilder qouteEnd() {
				sb.append(")");
				--qouteSignal;
				return this;
			}

			public WhereConditionBuilder and() throws Exception {
				if (contactSignal != 0) {
					throw new Exception("there is more contact exists!");
				}
				sb.append(" and ");
				++contactSignal;
				return this;
			}

			public WhereConditionBuilder or() throws Exception {
				if (contactSignal != 0) {
					throw new Exception("there is more contact exists!");
				}
				sb.append(" or ");
				++contactSignal;
				return this;
			}

			public WhereConditionBuilder lt(String fieldName, Object value, boolean isClosure) {
				sb.append(fieldName).append(" <").append(isClosure ? "= " : " ").append(value.toString());
				if (contactSignal > 0) {
					--contactSignal;
				}
				return this;
			}

			public WhereConditionBuilder gt(String fieldName, Object value, boolean isClosure) {
				sb.append(fieldName).append(" >").append(isClosure ? "= " : " ").append(value.toString());
				if (contactSignal > 0) {
					--contactSignal;
				}
				return this;
			}

			public WhereConditionBuilder eq(String fieldName, Object value) {
				sb.append(fieldName).append(" = ").append(value.toString());
				if (contactSignal > 0) {
					--contactSignal;
				}
				return this;
			}

			public WhereConditionBuilder notEq(String fieldName, Object value) {
				sb.append(fieldName).append(" != ").append(value.toString());
				if (contactSignal > 0) {
					--contactSignal;
				}
				return this;
			}

			public WhereConditionBuilder like(String fieldName, Object value, boolean left, boolean right) {
				sb.append(fieldName).append(" like '").append(left ? "%" : "")
						.append(value == null ? "" : value.toString()).append(right ? "%" : "").append("'");
				if (contactSignal > 0) {
					--contactSignal;
				}
				return this;
			}

			private String build() throws Exception {
				if (qouteSignal != 0) {
					throw new Exception("qoute count error," + qouteSignal);
				}
				if (contactSignal != 0) {
					throw new Exception("contant count error," + qouteSignal);
				}
				if (sb.length() == 0) {
					return "";
				}
				return sb.toString();
			}
		}
	}
}
