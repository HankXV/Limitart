/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.fsm;

import java.util.LinkedList;
import java.util.List;

/**
 * 状态
 *
 * @author hank
 */
public abstract class State<T extends FSM> {
    private List<Event<T>> conditions = new LinkedList<>();
    private boolean finished = false;
    private long executedTime;

    public abstract Integer getStateId();

    /**
     * 状态进入
     *
     * @param preState
     * @param fsm
     */
    public abstract void onEnter(State<T> preState, T fsm);

    /**
     * 状态退出
     *
     * @param nextState
     * @param fsm
     */
    public abstract void onExit(State<T> nextState, T fsm);

    /**
     * 状态执行
     *
     * @param deltaTimeInMills
     * @param fsm
     */
    protected abstract void onExecute(long deltaTimeInMills, T fsm);

    public void execute(long deltaTimeInMills, T fsm) {
        executedTime += deltaTimeInMills;
        onExecute(deltaTimeInMills, fsm);
    }

    /**
     * 重置状态
     *
     * @return
     */
    public State<T> reset() {
        executedTime = 0;
        finished = false;
        return this;
    }

    /**
     * 增加一个条件
     *
     * @param condition
     * @return
     */
    public State<T> addEvent(Event<T> condition) {
        conditions.add(condition);
        return this;
    }

    /**
     * 添加完成事件
     *
     * @param condition
     * @return
     */
    public State<T> addFinishEvent(FinishedEvent<T> condition) {
        return addEvent(condition);
    }

    public Event<T> eventTrigger(T fsm, long delta) {
        for (Event<T> con : conditions) {
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

    /**
     * 获取当前状态执行的时长
     *
     * @return
     */
    public long getExecutedTime() {
        return executedTime;
    }
}