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
package org.slingerxv.limitart.game.statemachine.state;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slingerxv.limitart.funcs.Proc;
import org.slingerxv.limitart.game.statemachine.StateMachine;
import org.slingerxv.limitart.game.statemachine.event.IEvent;
import org.slingerxv.limitart.game.statemachine.event.impl.FinishedEvent;

/**
 * 状态
 * 
 * @author hank
 *
 */
public abstract class State<T extends StateMachine> {
	private List<IEvent<T>> conditions = new LinkedList<>();
	private List<Ticker> tickers = new LinkedList<>();
	private boolean finished = false;

	public abstract Integer getStateId();

	protected void tick(long delay, int times, Proc listener) {
		tickers.add(new Ticker(delay, times, listener));
	}

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

	public void execute0(long deltaTimeInMills, T fsm) {
		Iterator<Ticker> iterator = tickers.iterator();
		for (; iterator.hasNext();) {
			Ticker ticker = iterator.next();
			ticker.delayCounter += deltaTimeInMills;
			if (ticker.delayCounter >= ticker.delay) {
				ticker.delayCounter = 0;
				ticker.times -= 1;
				ticker.listener.run();
				if (ticker.times <= 0) {
					iterator.remove();
				}
			}
		}
		execute(deltaTimeInMills, fsm);
	}

	/**
	 * 状态执行
	 * 
	 * @param deltaTimeInMills
	 * @param param
	 */
	protected abstract void execute(long deltaTimeInMills, T fsm);

	public State<T> reset() {
		tickers.clear();
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

	public State<T> addFinishEvent(FinishedEvent<T> condition) {
		return addEvent(condition);
	}

	public IEvent<T> EventTrigger(T fsm, long delta) {
		for (IEvent<T> con : conditions) {
			if (con.onCondition(fsm, this, delta)) {
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

	private static class Ticker {
		private long delay;
		private int times;
		private long delayCounter;
		private Proc listener;

		public Ticker(long delay, int times, Proc listener) {
			this.delay = delay;
			this.times = times;
			this.listener = listener;
		}
	}
}