package org.slingerxv.limitart.db.log.config;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import org.slingerxv.limitart.util.StringUtil;


/**
 * 日志服务器配置
 * 
 * @author hank
 *
 */
public final class LogDBServerConfig {
	// 扫描项目包名(日志结构检查)
	private String[] scanPackages;
	// 任务线程池基本线程数
	private int threadCorePoolSize;
	// 任务线程池最大线程数
	private int threadMaximumPoolSize;
	// 任务上限数量
	private int taskMaxSize;
	// 数据库引擎
	private String dbEngine;
	// 编码
	private String charset;
	// 自定义线程池
	private ThreadPoolExecutor customInsertThreadPool;

	private LogDBServerConfig(LogDBServerConfigBuilder builder) {
		this.scanPackages = builder.scanPackages.toArray(new String[0]);
		this.taskMaxSize = builder.taskMaxSize;
		this.threadCorePoolSize = builder.threadCorePoolSize;
		this.threadMaximumPoolSize = builder.threadMaximumPoolSize;
		this.dbEngine = builder.dbEngine;
		this.charset = builder.charset;
		this.customInsertThreadPool = builder.customInsertThreadPool;
	}

	public int getThreadCorePoolSize() {
		return threadCorePoolSize;
	}

	public int getThreadMaximumPoolSize() {
		return threadMaximumPoolSize;
	}

	public String[] getScanPackages() {
		return scanPackages;
	}

	public String getDbEngine() {
		return dbEngine;
	}

	public String getCharset() {
		return charset;
	}

	public ThreadPoolExecutor getCustomInsertThreadPool() {
		return customInsertThreadPool;
	}

	public int getTaskMaxSize() {
		return taskMaxSize;
	}

	public static class LogDBServerConfigBuilder {
		// 扫描项目包名(日志结构检查)
		private Set<String> scanPackages = new HashSet<>();
		// 任务上限数量
		private int taskMaxSize;
		// 任务线程池基本线程数
		private int threadCorePoolSize;
		// 任务线程池最大线程数
		private int threadMaximumPoolSize;
		// 数据库引擎
		private String dbEngine;
		// 编码
		private String charset;
		// 自定义线程池
		private ThreadPoolExecutor customInsertThreadPool;

		public LogDBServerConfigBuilder() {
			this.taskMaxSize = 8000;
			// 任务线程池基本线程数
			this.threadCorePoolSize = 3;
			// 任务线程池最大线程数
			this.threadMaximumPoolSize = 5;
			// 数据库引擎
			this.dbEngine = "myisam";
			// 编码
			this.charset = "utf8";
		}

		/**
		 * 构建配置
		 * 
		 * @return
		 */
		public LogDBServerConfig build() {
			return new LogDBServerConfig(this);
		}

		/**
		 * 添加一个需要扫描的包
		 * 
		 * @param packageName
		 * @return
		 */
		public LogDBServerConfigBuilder addScanPackage(String packageName) {
			if (StringUtil.isEmptyOrNull(packageName)) {
				throw new NullPointerException("packageName");
			}
			this.scanPackages.add(packageName);
			return this;
		}

		/**
		 * 插入任务数量上限
		 * 
		 * @param size
		 * @return
		 */
		public LogDBServerConfigBuilder taskMaxSize(int size) {
			if (size > 0) {
				this.taskMaxSize = size;
			}
			return this;
		}

		/**
		 * 初始线程数大小
		 * 
		 * @param size
		 * @return
		 */
		public LogDBServerConfigBuilder threadCorePoolSize(int size) {
			if (size > 0) {
				this.threadCorePoolSize = size;
			}
			return this;
		}

		/**
		 * 最大线程数大小
		 * 
		 * @param size
		 * @return
		 */
		public LogDBServerConfigBuilder threadMaximumPoolSize(int size) {
			if (size > 0) {
				this.threadMaximumPoolSize = size;
			}
			return this;
		}

		/**
		 * 数据库引擎
		 * 
		 * @param dbEngine
		 * @return
		 */
		public LogDBServerConfigBuilder dbEngine(String dbEngine) {
			if (StringUtil.isEmptyOrNull(dbEngine)) {
				throw new NullPointerException("dbEngine");
			}
			this.dbEngine = dbEngine;
			return this;
		}

		/**
		 * 编码
		 * 
		 * @param charset
		 * @return
		 */
		public LogDBServerConfigBuilder charset(String charset) {
			if (StringUtil.isEmptyOrNull(charset)) {
				throw new NullPointerException("charset");
			}
			this.charset = charset;
			return this;
		}

		/**
		 * 自定义一个线程池来处理(threadCorePoolSize和threadMaximumPoolSize将无效)
		 * 
		 * @param threadPool
		 * @return
		 */
		public LogDBServerConfigBuilder customInsertThreadPool(ThreadPoolExecutor threadPool) {
			if (threadPool == null) {
				throw new NullPointerException("customInsertThreadPool");
			}
			this.customInsertThreadPool = threadPool;
			return this;
		}
	}
}
