package com.limitart.game.statemachine.event.impl;

import com.limitart.game.statemachine.StateMachine;
import com.limitart.game.statemachine.event.IEvent;
import com.limitart.game.statemachine.state.State;

/**
 * 状态完成事件
 * 
 * @author hank
 *
 */
@SuppressWarnings("rawtypes")
public class FinishedEvent implements IEvent {

	private Integer nextNodeId;

	public FinishedEvent(Integer nextNodeId) {
		this.nextNodeId = nextNodeId;
	}

	public Integer getNextStateId() {
		return this.nextNodeId;
	}

	@Override
	public boolean onCondition(State state, StateMachine fsm) {
		return state.finished();
	}
}
