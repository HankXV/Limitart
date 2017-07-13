package org.slingerxv.limitart.game.statemachine.state;

import java.util.LinkedList;
import java.util.List;

import org.slingerxv.limitart.game.statemachine.StateMachine;
import org.slingerxv.limitart.game.statemachine.event.IEvent;

/**
 * 状态
 * 
 * @author hank
 *
 */
public abstract class State<T extends StateMachine> {
	private List<IEvent<T>> conditions = new LinkedList<IEvent<T>>();

	private boolean finished = false;

	public abstract Integer getStateId();

	/**
	 * 状态进入
	 * 
	 * @param preState
	 * @param param
	 */
	public abstract void onEnter(State<T> preState, T fsm);

	/**
	 * 状态退出
	 * 
	 * @param nextState
	 * @param param
	 */
	public abstract void onExit(State<T> nextState, T fsm);

	/**
	 * 状态执行
	 * 
	 * @param deltaTimeInMills
	 * @param param
	 */
	public abstract void execute(long deltaTimeInMills, T fsm);

	public State<T> reset() {
		finished = false;
		return this;
	}

	/**
	 * 增加一个条件
	 * 
	 * @param condition
	 * @return
	 */
	public State<T> addEvent(IEvent<T> condition) {
		conditions.add(condition);
		return this;
	}

	public IEvent<T> EventTrigger(T fsm) {
		for (IEvent<T> con : conditions) {
			if (con.onCondition(this, fsm)) {
				return con;
			}
		}
		return null;
	}

	/**
	 * 此状态是否完毕
	 * 
	 * @return
	 */
	public boolean finished() {
		return this.finished;
	}

	/**
	 * 设置此状态完成
	 */
	public void finish() {
		this.finished = true;
	}
}