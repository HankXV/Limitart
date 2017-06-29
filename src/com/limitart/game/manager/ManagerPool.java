package com.limitart.game.manager;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.game.manager.define.IManager;

public class ManagerPool {
	private static Logger log = LogManager.getLogger();
	private LinkedHashMap<Class<? extends IManager>, IManager> managers = new LinkedHashMap<>();

	public synchronized void register(Class<? extends IManager> clazz) throws Exception {
		if (managers.containsKey(clazz)) {
			throw new Exception("manager type duplicated!");
		}
		managers.put(clazz, clazz.newInstance());
	}

	public synchronized void init() throws Exception {
		for (IManager manager : managers.values()) {
			manager.init();
			log.info(manager.getClass().getSimpleName() + " init done");
		}
	}

	public synchronized void deinit() {
		for (IManager manager : managers.values()) {
			manager.deInit();
			log.info(manager.getClass().getSimpleName() + " deinit done");
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends IManager> T get(Class<T> clazz) {
		return (T) this.managers.get(clazz);
	}

	public static ManagerPool self() {
		return InstanceHolder.INSTANCE.value;
	}

	private ManagerPool() {
	}

	private enum InstanceHolder {
		INSTANCE;
		private ManagerPool value;

		private InstanceHolder() {
			value = new ManagerPool();
		}
	}
}
