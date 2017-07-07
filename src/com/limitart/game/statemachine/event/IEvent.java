package com.limitart.game.statemachine.event;

import com.limitart.game.statemachine.StateMachine;
import com.limitart.game.statemachine.state.State;

/**
 * 事件
 * 
 * @author hank
 *
 */
public interface IEvent<T extends StateMachine> {
	/**
	 * 事件触发跳转的状态Id
	 * 
	 * @return
	 */
    Integer getNextStateId();

	/**
	 * 是否符合条件跳转
	 * 
	 * @param state
	 * @param param
	 * @return
	 */
    boolean onCondition(State<T> state, T fsm);
}