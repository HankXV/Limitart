package org.slingerxv.limitart.game.statemachine.event.impl;

import org.slingerxv.limitart.game.statemachine.StateMachine;
import org.slingerxv.limitart.game.statemachine.event.IEvent;
import org.slingerxv.limitart.game.statemachine.state.State;

/**
 * 状态完成事件
 * 
 * @author hank
 * @param <T>
 *
 */
public class FinishedEvent<T extends StateMachine> implements IEvent<T> {

	private Integer nextNodeId;
	private long delay;

	public FinishedEvent(Integer nextNodeId, long delay) {
		this.nextNodeId = nextNodeId;
		this.delay = delay;
	}

	public FinishedEvent(Integer nextNodeId) {
		this(nextNodeId, 0);
	}

	public Integer getNextStateId() {
		return this.nextNodeId;
	}

	@Override
	public boolean onCondition(T fsm, State<T> state, long delta) {
		this.delay -= delta;
		return state.finished() && delay <= 0;
	}
}
