/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
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
