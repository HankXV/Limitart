package org.slingerxv.limitart.game.statemachine;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.collections.ConstraintMap;
import org.slingerxv.limitart.game.statemachine.event.IEvent;
import org.slingerxv.limitart.game.statemachine.exception.StateException;
import org.slingerxv.limitart.game.statemachine.state.State;
import org.slingerxv.limitart.util.Beta;

/**
 * 状态机代理
 * 
 * @author hank
 *
 */
@Beta
@SuppressWarnings("rawtypes")
public class StateMachine {
	private static Logger log = LogManager.getLogger();
	private ConcurrentHashMap<Integer, State> stateMap = new ConcurrentHashMap<>();
	private Queue<Integer> stateQueue = new LinkedList<>();
	private State preState;
	private State curState;
	private ConstraintMap<Object> params = new ConstraintMap<Object>();
	private long lastLoopTime = 0;
	private int firstStateId;

	/**
	 * 重置状态机
	 * 
	 * @throws StateException
	 */
	public void revert() throws StateException {
		this.stateQueue.clear();
		this.params.clear();
		this.lastLoopTime = 0;
		changeState(this.firstStateId);
	}

	public void firstState(int stateId) {
		this.firstStateId = stateId;
	}

	public StateMachine addState(State... states) throws StateException {
		for (State temp : Objects.requireNonNull(states, "states")) {
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
	public StateMachine addState(State state) throws StateException {
		Objects.requireNonNull(state, "state");
		Objects.requireNonNull(state.getStateId(), "stateId");
		if (this.stateMap.containsKey(state.getStateId())) {
			throw new StateException("NodeId:" + state.getStateId() + " duplicated in this FSM !");
		}
		this.stateMap.put(state.getStateId(), state);
		log.info("ADD:{}", state.getStateId());
		return this;
	}

	/**
	 * 改变状态
	 * 
	 * @param stateId
	 * @return
	 * @throws StateException
	 */
	private StateMachine changeState(Integer stateId) throws StateException {
		Objects.requireNonNull(stateId, "stateId");
		if (this.curState != null && stateId.intValue() == this.curState.getStateId().intValue()) {
			return this;
		}
		if (!stateMap.containsKey(stateId)) {
			throw new StateException(MessageFormat.format("NodeId:{0} does not exist !", stateId));
		}
		stateQueue.offer(stateId);
		log.debug("CHANGE:{}", stateId);
		return this;
	}

	/**
	 * 跳转回上一个状态
	 * 
	 * @throws StateException
	 */
	public StateMachine Reverse2PreState() throws StateException {
		if (preState != null) {
			changeState(preState.getStateId());
		}
		return this;
	}

	/**
	 * 状态机循环
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void loop() throws StateException {
		long now = System.currentTimeMillis();
		long deltaTimeInMills = this.lastLoopTime == 0 ? 0 : now - this.lastLoopTime;
		lastLoopTime = System.currentTimeMillis();
		Integer nextNode = getNextNode();
		State next = null;
		if (nextNode != null) {
			next = this.stateMap.get(nextNode);
		}
		if (next != null) {
			if (this.curState != null) {
				this.curState.onExit(next, this);
				log.debug("EXIST:{}", this.curState.getStateId());
			}
			next.reset();
			log.debug("RESET:{}", next.getStateId());
			next.onEnter(this.curState, this);
			log.debug("ENTER:{}", next.getStateId());
			this.preState = this.curState;
			curState = next;
		}
		if (this.curState != null) {
			if (!curState.finished()) {
				this.curState.execute0(deltaTimeInMills, this);
			}
			log.debug("EXE:{}", this.curState.getStateId());
			IEvent con = this.curState.EventTrigger(this, deltaTimeInMills);
			if (con != null) {
				int nextNodeId = con.getNextStateId();
				if (!this.stateMap.containsKey(nextNodeId)) {
					throw new StateException(MessageFormat.format(
							"condition:{0} in node:{1}, it's next nodeId:{2} does't exist in this FSM !",
							con.getClass().getSimpleName(), curState.getClass().getSimpleName(), con.getNextStateId()));
				}
				changeState(nextNodeId);
			}
		}
	}

	public State getCurrentState() {
		return this.curState;
	}

	public State getPreState() {
		return this.preState;
	}

	public ConstraintMap<Object> getParams() {
		return params;
	}

	private Integer getNextNode() {
		if (stateQueue.size() < 1) {
			return null;
		}
		return stateQueue.poll();
	}

}
