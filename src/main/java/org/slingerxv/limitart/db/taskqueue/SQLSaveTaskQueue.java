package org.slingerxv.limitart.db.taskqueue;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.db.define.ISQLBean;
import org.slingerxv.limitart.db.define.ISQLSaveDao;
import org.slingerxv.limitart.taskqueue.DisruptorTaskQueue;
import org.slingerxv.limitart.taskqueue.define.ITaskQueue;
import org.slingerxv.limitart.taskqueue.define.ITaskQueueHandler;
import org.slingerxv.limitart.taskqueue.exception.TaskQueueException;
import org.slingerxv.limitart.util.StringUtil;

/**
 * 数据库更新队列
 * 
 * @author Hank
 *
 */
public class SQLSaveTaskQueue implements ITaskQueue<ISQLBean> {
	public static final int INSERT = 0;
	public static final int UPDATE = 1;
	public static final int MIXED = 2;
	private static Logger log = LogManager.getLogger();
	private HashMap<Class<? extends ISQLBean>, ISQLSaveDao> daos = new HashMap<>();
	private ITaskQueue<SQLBeanWrap> taskQueue;

	public SQLSaveTaskQueue(String threadName) {
		this.taskQueue = new DisruptorTaskQueue<>(threadName, new ITaskQueueHandler<SQLBeanWrap>() {

			@Override
			public boolean intercept(SQLBeanWrap t) {
				return false;
			}

			@Override
			public void handle(SQLBeanWrap t) {
				ISQLSaveDao baseDao = daos.get(t.bean.getClass());
				if (baseDao == null) {
					log.error("dao not exist:" + t.getClass().getName());
					return;
				}
				if (t.dealType == INSERT) {
					if (baseDao.insert(t.bean) == 0) {
						log.error(getClass().getSimpleName() + " insert bean error,bean:"
								+ StringUtil.toJSONWithClassInfo(t));
					}
				} else if (t.dealType == UPDATE) {
					baseDao.update(t.bean);
				} else if (t.dealType == MIXED) {
					if (baseDao.update(t.bean) == 0) {
						if (baseDao.insert(t.bean) == 0) {
							log.error(getClass().getSimpleName() + " update bean error,bean:"
									+ StringUtil.toJSONWithClassInfo(t));
						}
					}
				}
			}
		});
	}

	public synchronized void register(Class<? extends ISQLBean> beanClass, Class<? extends ISQLSaveDao> daoClass) {
		try {
			daos.put(beanClass, daoClass.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e, e);
		}
	}

	@Override
	public void startServer() throws Exception {
		taskQueue.startServer();
	}

	@Override
	public void stopServer() throws Exception {
		taskQueue.stopServer();
	}

	@Override
	public String getThreadName() {
		return taskQueue.getThreadName();
	}

	@Override
	public void addCommand(ISQLBean bean) throws TaskQueueException {
		addCommand(MIXED, bean);
	}

	public void addCommand(int dealType, ISQLBean bean) throws TaskQueueException {
		if (dealType != INSERT && dealType != UPDATE && dealType != MIXED) {
			throw new TaskQueueException("deal type error!");
		}
		SQLBeanWrap wrap = new SQLBeanWrap();
		wrap.bean = bean;
		wrap.dealType = dealType;
		taskQueue.addCommand(wrap);
	}

	private class SQLBeanWrap {
		private int dealType;
		private ISQLBean bean;
	}
}
