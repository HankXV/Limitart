package org.slingerxv.limitart.dblog;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.db.define.IDataSourceFactory;
import org.slingerxv.limitart.dblog.anotation.LogColumn;
import org.slingerxv.limitart.dblog.config.LogDBServerConfig;
import org.slingerxv.limitart.dblog.define.ILog;
import org.slingerxv.limitart.dblog.exception.LogDBServerAlreadyStopException;
import org.slingerxv.limitart.dblog.tablecheck.LogStructChecker;
import org.slingerxv.limitart.dblog.util.LogDBUtil;
import org.slingerxv.limitart.dblog.util.LogDBUtil.QueryConditionBuilder;
import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.reflectasm.FieldAccess;
import org.slingerxv.limitart.thread.NamedThreadFactory;

/**
 * 数据库日志记录服务器
 * 
 * @author hank
 *
 */
public class LogDBServer implements IServer {
	private static Logger log = LogManager.getLogger();
	private LogDBServerConfig config;
	private ThreadPoolExecutor threadPool;
	private BlockingQueue<Runnable> logTaskQueue;
	private IDataSourceFactory dataSourceFactory;
	private LogStructChecker checker = new LogStructChecker();
	private boolean isStop = true;
	private LongAdder doneLogNum = new LongAdder();
	private LongAdder lostLogNum = new LongAdder();

	public LogDBServer(LogDBServerConfig config, IDataSourceFactory dataSourceFactory) {
		this.config = config;
		this.dataSourceFactory = dataSourceFactory;
	}

	/**
	 * 执行一条日志记录的插入
	 * 
	 * @param baseLog
	 * @return
	 * @throws Exception
	 */
	public LogDBServer execute(ILog alog) throws Exception {
		if (isStop) {
			throw new Exception("server is stopped!");
		}

		if (alog != null) {
			if (getTaksCount() > this.config.getTaskMaxSize()) {
				increaseLostLogNum();
				throw new Exception("task count is overload,drop task:" + LogDBUtil.log2JSON(alog));
			}
			threadPool.execute(new LogInsertTask(alog));
		}
		return this;
	}

	public Class<? extends ILog> getTableClassByName(String tableName) {
		return this.getChecker().getTableClass(tableName);
	}

	/**
	 * 查询日志条数
	 * 
	 * @param tableName
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public int queryCount(String tableName, QueryConditionBuilder builder) throws Exception {
		Class<? extends ILog> tableClass = getTableClassByName(tableName);
		if (tableClass == null) {
			return 0;
		}
		return queryCount(tableClass, builder);
	}

	/**
	 * 查询日志条数
	 * 
	 * @param tableName
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public int queryCount(Class<? extends ILog> clss, QueryConditionBuilder builder) throws Exception {
		if (isStop) {
			throw new Exception("server is stopped!");
		}
		String buildSelectTableSql = LogDBUtil.buildSelectCountTableSql_MYSQL(builder);
		try (Connection connection = this.dataSourceFactory.getDataSource().getConnection();
				PreparedStatement prepareStatement = connection.prepareStatement(buildSelectTableSql);
				ResultSet executeQuery = prepareStatement.executeQuery();) {
			executeQuery.next();
			int count = executeQuery.getInt(1);
			return count;
		}
	}

	public Collection<String> queryRelativeTables(Class<? extends ILog> clss, long startTime, long endTime)
			throws InstantiationException, IllegalAccessException, SQLException {
		// 获取相关表
		Set<String> relativeTableNames = LogDBUtil.getRelativeTableNames(clss, startTime, endTime);
		// 筛选不存在的表
		Iterator<String> iterator2 = relativeTableNames.iterator();
		try (Connection connection = this.dataSourceFactory.getDataSource().getConnection();) {
			List<String> tableNames = LogDBUtil.getTableNames(connection);
			for (; iterator2.hasNext();) {
				if (!tableNames.contains(iterator2.next())) {
					iterator2.remove();
				}
			}
		}
		return relativeTableNames;
	}

	/**
	 * 查询某段日期的日志
	 * 
	 * @param tableName
	 * @param startTime
	 * @param endTime
	 * @param startIndex
	 * @param size
	 * @param orderParam
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T extends ILog> List<T> query(String tableName, QueryConditionBuilder builder) throws Exception {
		Class<? extends ILog> tableClass = getTableClassByName(tableName);
		if (tableClass == null) {
			return null;
		}
		return (List<T>) query(tableClass, builder);
	}

	/**
	 * 查询某段日期的日志
	 * 
	 * @param clss
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public <T extends ILog> List<T> query(Class<T> clss, QueryConditionBuilder builder) throws Exception {
		if (isStop) {
			throw new Exception("server is stopped!");
		}
		List<T> result = new ArrayList<>();
		String buildSelectTableSql = LogDBUtil.buildSelectTableSql_MYSQL(builder);
		try (Connection connection = this.dataSourceFactory.getDataSource().getConnection();
				PreparedStatement prepareStatement = connection.prepareStatement(buildSelectTableSql);
				ResultSet executeQuery = prepareStatement.executeQuery();) {
			while (executeQuery.next()) {
				T newInstance = clss.newInstance();
				FieldAccess logFields = LogDBUtil.getLogFields(clss);
				for (int index = 0; index < logFields.getFieldCount(); ++index) {
					Field field = logFields.getFields()[index];
					LogColumn annotation = field.getAnnotation(LogColumn.class);
					if (annotation == null) {
						continue;
					}
					String name = logFields.getFieldNames()[index];
					Object object = executeQuery.getObject(name);
					if (object == null) {
						continue;
					}
					logFields.set(newInstance, index, object);
				}
				result.add(newInstance);
			}
		}
		return result;
	}

	/**
	 * 获取当前队列中日志任务的数量
	 * 
	 * @return
	 */
	public long getTaksCount() {
		return logTaskQueue.size();
	}

	/**
	 * 获取日志检查器
	 * 
	 * @return
	 */
	public LogStructChecker getChecker() {
		return checker;
	}

	public LogDBServerConfig getConfig() {
		return config;
	}

	public IDataSourceFactory getDataSourceFactory() {
		return this.dataSourceFactory;
	}

	public long getDoneLogNum() {
		return doneLogNum.longValue();
	}

	public long getLostLogNum() {
		return lostLogNum.longValue();
	}

	public void increaseDoneLogNum() {
		this.doneLogNum.increment();
	}

	public void increaseLostLogNum() {
		this.lostLogNum.increment();
	}

	/**
	 * 日志插入任务
	 * 
	 * @author hank
	 *
	 */
	private class LogInsertTask implements Runnable {
		private ILog alog;

		public LogInsertTask(ILog alog) {
			this.alog = alog;
		}

		public void run() {
			try (Connection con = LogDBServer.this.getDataSourceFactory().getDataSource().getConnection();) {
				long now = System.currentTimeMillis();
				if (this.alog == null) {
					return;
				}
				String buildExistTableSql_MYSQL = LogDBUtil
						.buildExistTableSql_MYSQL(LogDBUtil.getLogTableName(alog, now));
				try (PreparedStatement existStatement = con.prepareStatement(buildExistTableSql_MYSQL);
						ResultSet executeQuery = existStatement.executeQuery();) {
					if (!executeQuery.next()) {
						String buildCreateTableSql = LogDBUtil.buildCreateTableSql_MYSQL(alog,
								LogDBServer.this.getConfig().getDbEngine(), LogDBServer.this.getConfig().getCharset());
						try (PreparedStatement createStatement = con.prepareStatement(buildCreateTableSql);) {
							// 执行创建表
							createStatement.executeUpdate();
						}

					}
				}
				String buildInsertTableSql = LogDBUtil.buildInsertTableSql_MYSQL(alog);
				try (PreparedStatement insertStatement = con.prepareStatement(buildInsertTableSql);) {
					// 执行插入
					if (insertStatement.executeUpdate() > 0) {
						LogDBServer.this.increaseDoneLogNum();
					} else {
						log.error(LogDBUtil.log2JSON(alog));
						LogDBServer.this.increaseLostLogNum();
					}
				}
			} catch (Exception e) {
				log.error(e, e);
				log.error(LogDBUtil.log2JSON(alog));
				LogDBServer.this.increaseLostLogNum();
			}
		}
	}

	@Override
	public void startServer() throws Exception {
		if (this.config == null) {
			throw new NullPointerException("please init server first!");
		}
		if (this.dataSourceFactory == null) {
			throw new NullPointerException("please set a DBConnection!");
		}
		if (!isStop) {
			throw new NullPointerException("server has already start!");
		}
		// 初始化任务线程池
		if (this.config.getCustomInsertThreadPool() == null) {
			this.logTaskQueue = new LinkedBlockingQueue<>();
			this.threadPool = new ThreadPoolExecutor(this.config.getThreadCorePoolSize(),
					this.config.getThreadMaximumPoolSize(), 0, TimeUnit.MILLISECONDS, logTaskQueue,
					new NamedThreadFactory() {

						@Override
						public String getThreadName() {
							return "LogDBServer-Insert-" + threadPool.getPoolSize();
						}
					});
		} else {
			this.logTaskQueue = this.config.getCustomInsertThreadPool().getQueue();
			this.threadPool = this.config.getCustomInsertThreadPool();
		}
		// 检查所有表的变更状况
		if (checker != null) {
			// 添加默认日志
			if (config.getScanPackages() != null && config.getScanPackages().length > 0) {
				for (String packageName : config.getScanPackages()) {
					checker.registTable(packageName);
				}
			}
			// 启动时，执行表格结构检查
			try (Connection connection = this.dataSourceFactory.getDataSource().getConnection();) {
				checker.executeCheck(connection);
			}
		}
		this.isStop = false;
	}

	@Override
	public void stopServer() throws Exception {
		if (isStop) {
			throw new LogDBServerAlreadyStopException();
		}
		this.isStop = true;
		List<Runnable> shutdownNow = threadPool.shutdownNow();
		// 完成剩余的任务
		for (Runnable task : shutdownNow) {
			try {
				task.run();
				log.info("保存文件日志队列第" + shutdownNow.size() + "条完成");
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		shutdownNow.clear();
		log.info("日志系统关闭完成");
	}
}