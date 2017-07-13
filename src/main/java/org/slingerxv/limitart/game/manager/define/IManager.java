package org.slingerxv.limitart.game.manager.define;

public interface IManager {

	/**
	 * 启动时调用
	 */
    void init() throws Exception;

	/**
	 * 关闭时调用（释放资源等）
	 */
    void deInit();
}
