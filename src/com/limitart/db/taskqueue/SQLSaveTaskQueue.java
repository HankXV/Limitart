package com.limitart.db.taskqueue;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.db.define.ISQLBean;
import com.limitart.db.define.ISQLSaveDao;
import com.limitart.taskqueue.DisruptorTaskQueue;
import com.limitart.taskqueue.define.ITaskQueue;
import com.limitart.taskqueue.define.ITaskQueueHandler;
import com.limitart.taskqueue.exception.TaskQueueException;
import com.limitart.util.StringUtil;

/**
 * 数据库更新队列
 * 
 * @author Hank
 *
 */
public class SQLSaveTaskQueue implements ITaskQueue<ISQLBean> {
	private static Logger log = LogManager.getLogger();
	private HashMap<Class<? extends ISQLBean>, ISQLSaveDao> daos = new HashMap<>();
	private ITaskQueue<ISQLBean> taskQueue;

	public SQLSaveTaskQueue(String threadName) {
		this.taskQueue = new DisruptorTaskQueue<>(threadName, new ITaskQueueHandler<ISQLBean>() {

			@Override
			public boolean intercept(ISQLBean t) {
				return false;
			}

			@Override
			public void handle(ISQLBean t) {
				ISQLSaveDao baseDao = daos.get(t.getClass());
				if (baseDao == null) {
					log.error("dao not exist:" + t.getClass().getName());
					return;
				}
				if (baseDao.update(t) == 0) {
					if (baseDao.insert(t) == 0) {
						log.error(getClass().getSimpleName() + " update bean error,bean:"
								+ StringUtil.toJSONWithClassInfo(t));
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
	public void startServer() {
		taskQueue.startServer();
	}

	@Override
	public void stopServer() {
		taskQueue.stopServer();
	}

	@Override
	public String getThreadName() {
		return taskQueue.getThreadName();
	}

	@Override
	public void addCommand(ISQLBean bean) throws TaskQueueException {
		taskQueue.addCommand(bean);
	}
}
