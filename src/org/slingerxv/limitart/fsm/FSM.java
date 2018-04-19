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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.NotNull;
import org.slingerxv.limitart.base.Proc;
import org.slingerxv.limitart.base.ThreadSafe;
import org.slingerxv.limitart.collections.ConstraintConcurrentMap;
import org.slingerxv.limitart.collections.ConstraintMap;
import org.slingerxv.limitart.concurrent.AutoGrowthTaskQueueGroup;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 状态机代理
 *
 * @author hank
 */
@SuppressWarnings("rawtypes")
@ThreadSafe
public class FSM {
    private static final Logger LOGGER = LoggerFactory.getLogger(FSM.class);
    private final Map<Integer, State> stateMap = new HashMap<>();
    private final Queue<Integer> stateQueue = new LinkedList<>();
    private State preState;
    private State curState;
    private final ConstraintMap<Object> params = new ConstraintConcurrentMap<>();
    private long lastLoopTime = 0;
    private int firstStateId;
    private Thread lastThread;
    private final List<Ticker> tickers = new ArrayList<>();
    private final ReentrantLock tickerLock = new ReentrantLock();

    /**
     * 开启
     *
     * @throws StateException
     */
    public void start() throws StateException {
        if (curState != null) {
            throw new StateException("called once");
        }
        this.stateQueue.clear();
        this.params.clear();
        this.lastLoopTime = 0;
        changeState(this.firstStateId);
    }

    /**
     * 设置起始状态
     *
     * @param stateId
     */
    public void firstState(int stateId) {
        this.firstStateId = stateId;
    }

    /**
     * 添加一系列状态
     *
     * @param states
     * @return
     * @throws StateException
     */
    public FSM addState(State... states) throws StateException {
        for (State temp : Conditions.notNull(states, "states")) {
            addState(temp);
        }
        return this;
    }

    /**
     * 添加一个状态
     *
     * @param state
     * @throws Exception
     */
    public FSM addState(@NotNull State state) throws StateException {
        if (curState != null) {
            throw new StateException("already start");
        }
        Conditions.notNull(state, "state");
        Conditions.notNull(state.getStateId(), "stateId");
        if (stateMap.containsKey(state.getStateId())) {
            throw new StateException("stateId:" + state.getStateId() + " duplicated in this FSM !");
        }
        if (stateMap.isEmpty()) {
            firstState(state.getStateId());
        }
        this.stateMap.put(state.getStateId(), state);
        LOGGER.info("ADD:{}", state.getStateId());
        return this;
    }

    /**
     * 改变状态
     *
     * @param stateId
     * @return
     * @throws StateException
     */
    private FSM changeState(Integer stateId) throws StateException {
        Conditions.notNull(stateId, "stateId");
        if (this.curState != null && stateId.intValue() == this.curState.getStateId().intValue()) {
            return this;
        }
        if (!stateMap.containsKey(stateId)) {
            throw new StateException(MessageFormat.format("stateId:{0} does not exist !", stateId));
        }
        stateQueue.offer(stateId);
        LOGGER.debug("CHANGE:{}", stateId);
        return this;
    }

    /**
     * 添加计时行为
     *
     * @param delay
     * @param times
     * @param listener
     */
    public void tick(long delay, int times, @NotNull Proc listener) {
        try {
            tickerLock.lock();
            tickers.add(new Ticker(delay, times, listener));
        } finally {
            tickerLock.unlock();
        }
    }

    /**
     * 状态机循环
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void loop() throws StateException {
        if (lastThread != null) {
            Conditions.sameThread(lastThread);
        }
        lastThread = Thread.currentThread();
        long now = System.currentTimeMillis();
        long deltaTimeInMills = this.lastLoopTime == 0 ? 0 : now - this.lastLoopTime;
        lastLoopTime = now;
        try {
            tickerLock.lock();
            for (int i = tickers.size() - 1; i >= 0; --i) {
                Ticker ticker = tickers.get(i);
                ticker.delayCounter += deltaTimeInMills;
                if (ticker.delayCounter >= ticker.delay) {
                    ticker.delayCounter = 0;
                    ticker.times -= 1;
                    ticker.listener.run();
                    if (ticker.times <= 0) {
                        tickers.remove(i);
                    }
                }
            }
        } finally {
            tickerLock.unlock();
        }
        Integer nextNode = getNextNode();
        State next = null;
        if (nextNode != null) {
            next = this.stateMap.get(nextNode);
        }
        if (next != null) {
            if (this.curState != null) {
                this.curState.onExit(next, this);
                LOGGER.debug("EXIST:{}", this.curState.getStateId());
            }
            next.reset();
            LOGGER.debug("RESET:{}", next.getStateId());
            next.onEnter(this.curState, this);
            LOGGER.debug("ENTER:{}", next.getStateId());
            this.preState = this.curState;
            curState = next;
        }
        if (this.curState != null) {
            if (!curState.finished()) {
                this.curState.execute(deltaTimeInMills, this);
            }
            // log.debug("EXE:{}", this.curState.getStateId());
            Event con = this.curState.eventTrigger(this, deltaTimeInMills);
            if (con != null) {
                int nextNodeId = con.getNextStateId();
                if (!this.stateMap.containsKey(nextNodeId)) {
                    throw new StateException(MessageFormat.format(
                            "condition:{0} in state:{1}, it's next stateId:{2} does't exist in this FSM !",
                            con.getClass().getSimpleName(), curState.getClass().getSimpleName(), con.getNextStateId()));
                }
                changeState(nextNodeId);
            }
        }
    }

    /**
     * 获取当前状态
     *
     * @return
     */
    public State getCurrentState() {
        return this.curState;
    }

    /**
     * 获取上一个状态
     *
     * @return
     */
    public State getPreState() {
        return this.preState;
    }

    /**
     * 获取参数列表
     *
     * @return
     */
    public ConstraintMap<Object> getParams() {
        return params;
    }

    private Integer getNextNode() {
        if (stateQueue.size() < 1) {
            return null;
        }
        return stateQueue.poll();
    }

    private static class Ticker {
        private final long delay;
        private int times;
        private long delayCounter;
        private final Proc listener;

        public Ticker(long delay, int times, Proc listener) {
            this.delay = delay;
            this.times = times;
            this.listener = listener;
        }
    }
}
