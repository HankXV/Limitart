package org.slingerxv.limitart.game.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slingerxv.limitart.funcs.Test1;
import org.slingerxv.limitart.game.org.exception.AlreadyJoinException;
import org.slingerxv.limitart.game.statemachine.StateMachine;
import org.slingerxv.limitart.game.statemachine.exception.StateException;
import org.slingerxv.limitart.game.table.exception.GameAlreadyEndException;
import org.slingerxv.limitart.game.table.exception.GameAlreadyStartException;
import org.slingerxv.limitart.game.table.exception.NotJoinTableException;
import org.slingerxv.limitart.game.table.exception.SeatIndexIllegalException;
import org.slingerxv.limitart.game.table.exception.SeatOccupiedException;
import org.slingerxv.limitart.game.table.exception.TableRoleFullException;
import org.slingerxv.limitart.taskqueuegroup.struct.AutoGrowthEntity;

/**
 * 游戏桌
 * 
 * @author hank
 *
 */
public abstract class Table extends StateMachine {
	private int roleCapacity;
	private int seatCapacity;
	private AutoGrowthEntity autoGrothEntity = new AutoGrowthEntity();
	// 此桌子的所有角色
	private ConcurrentHashMap<Long, TableRole> roles = new ConcurrentHashMap<>();
	// 游戏中玩家坐下
	private long[] seats;
	// 座位
	private long[] gameSeats;
	// 是否已经开始游戏
	private boolean isStart = false;
	// 桌子Id
	private long tableId;
	// 桌子创建时间
	private long createTime = System.currentTimeMillis();
	// 上次加入人的时间
	private long lastJoinTime = 0;
	private boolean isReseted = false;

	/**
	 * 座位数量
	 * 
	 * @param seatCapacity
	 */
	public Table(long tableId, int seatCapacity) {
		this.tableId = tableId;
		seats = new long[seatCapacity];
		gameSeats = new long[seatCapacity];
		this.seatCapacity = seatCapacity;
		this.roleCapacity = Integer.MAX_VALUE;
	}

	/**
	 * 桌子类型
	 * 
	 * @return
	 */
	public abstract int tableType();

	/**
	 * 当加入桌子时
	 * 
	 * @param role
	 */
	protected abstract void onJoinTable(TableRole role);

	/**
	 * 当退出桌子时
	 * 
	 * @param role
	 * @param isRealRemove
	 *            是否从内存中移除了
	 * @param disconnected
	 *            是否断开链接了
	 */
	protected abstract void onQuitTable(TableRole role, boolean isRealRemove, boolean disconnected);

	/**
	 * 当坐上座位时
	 * 
	 * @param role
	 */
	protected abstract void onSit(TableRole role, int index);

	/**
	 * 当站起来时
	 */
	protected abstract void onStandUp(TableRole role, int index);

	/**
	 * 该玩家是否满足开始游戏的条件
	 * 
	 * @param role
	 * @return
	 */
	protected abstract boolean isValidatedToStart(TableRole role);

	/**
	 * 重置游戏数据
	 */
	public void resetGame() {
		if (!isReseted) {
			resetGame0();
			isReseted = true;
		}
	}

	/**
	 * 重置游戏数据
	 */
	protected abstract void resetGame0();

	/**
	 * 当游戏开始时
	 */
	protected abstract void onStartGame();

	/**
	 * 当游戏结时
	 */
	protected abstract void onEndGame();

	/**
	 * 座位占用数量
	 * 
	 * @return
	 */
	public int seatRoleSize() {
		int size = 0;
		for (long temp : seats) {
			if (temp != 0) {
				++size;
			}
		}
		return size;
	}

	/**
	 * 游戏中的座位占用数量
	 * 
	 * @return
	 */
	public int gameSeatRoleSize() {
		int size = 0;
		for (long temp : gameSeats) {
			if (temp != 0) {
				++size;
			}
		}
		return size;
	}

	/**
	 * 剩余座位数量
	 * 
	 * @return
	 */
	public int remainSeatSize() {
		int size = 0;
		for (long temp : seats) {
			if (temp == 0) {
				++size;
			}
		}
		return size;
	}

	/**
	 * 游戏是否已经开始
	 * 
	 * @return
	 */
	public boolean isStart() {
		return this.isStart;
	}

	/**
	 * 获取座子编号
	 * 
	 * @return
	 */
	public long getTableId() {
		return tableId;
	}

	/**
	 * 加入桌子
	 * 
	 * @param player
	 * @throws AlreadyJoinException
	 * @throws TableRoleFullException
	 */
	public void joinTable(TableRole role) throws AlreadyJoinException, TableRoleFullException {
		if (roles.containsKey(role.getUniqueId())) {
			throw new AlreadyJoinException();
		}
		if (roleSize() >= getRoleCapacity()) {
			throw new TableRoleFullException();
		}
		roles.put(role.getUniqueId(), role);
		lastJoinTime = System.currentTimeMillis();
		onJoinTable(role);
	}

	/**
	 * 退出桌子
	 * 
	 * @param player
	 * @throws NotJoinTableException
	 * @throws GameAlreadyStartException
	 */
	public void quitTable(TableRole role, boolean isGiveUpHandle, boolean isDisconnected) {
		if (!roles.containsKey(role.getUniqueId())) {
			return;
		}
		boolean isRealRemove = false;
		boolean disconnected = false;
		// 在 游戏桌子上并且游戏正在开始中
		if (!isStart()) {
			if (isOnSeats(role) != -1) {
				standUp(role);
			}
			if (roles.remove(role.getUniqueId()) != null) {
				isRealRemove = true;
			}
		} else {
			if (isOnGameSeats(role) != -1) {
				if (isGiveUpHandle) {
					role.setGiveUpHandle(true);
				}
			}
		}
		// 断开链接
		if (role.getChannel() != null) {
			if (isDisconnected) {
				role.getChannel().close();
				isDisconnected = true;
			}
			role.setChannel(null);
		}
		onQuitTable(role, isRealRemove, disconnected);
	}

	/**
	 * 选个合适的位置坐下
	 * 
	 * @param roleId
	 * @return
	 * @throws SeatIndexIllegalException
	 * @throws SeatOccupiedException
	 * @throws NotJoinTableException
	 * @throws GameAlreadyStartException
	 */
	public boolean sit(TableRole role)
			throws SeatIndexIllegalException, SeatOccupiedException, NotJoinTableException {
		// 选择空位置
		for (int index = 0; index < seats.length; ++index) {
			if (seats[index] == 0) {
				return sit(role, index);
			}
		}
		return false;
	}

	/**
	 * 指定位置坐下
	 * 
	 * @param role
	 * @param index
	 * @throws SeatIndexIllegalException
	 * @throws SeatOccupiedException
	 * @throws NotJoinTableException
	 * @throws GameAlreadyStartException
	 */
	public boolean sit(TableRole role, int index)
			throws SeatIndexIllegalException, SeatOccupiedException, NotJoinTableException {
		if (seats.length - 1 < index || index < 0) {
			throw new SeatIndexIllegalException();
		}
		if (!roles.containsKey(role.getUniqueId())) {
			throw new NotJoinTableException();
		}
		// 先从当前座位起来
		int oldIndex = isOnSeats(role);
		if (oldIndex == index) {
			return false;
		} else if (oldIndex >= 0) {
			standUp(role);
		}
		if (seats[index] != 0) {
			throw new SeatOccupiedException();
		}
		seats[index] = role.getUniqueId();
		onSit(role, index);
		return true;
	}

	/**
	 * 站起
	 * 
	 * @param role
	 * @throws NotJoinTableException
	 * @throws GameAlreadyStartException
	 */
	public void standUp(TableRole role) {
		for (int index = 0; index < seats.length; ++index) {
			if (seats[index] == role.getUniqueId()) {
				seats[index] = 0;
				onStandUp(role, index);
				break;
			}
		}
	}

	/**
	 * 开始游戏
	 * 
	 * @throws GameAlreadyStartException
	 * 
	 * @throws GameAlreadyEndException
	 * @throws StateException
	 */
	public synchronized boolean startGame() throws StateException {

		if (isStart()) {
			return false;
		}
		if (seatRoleSize() < startGameRoleNum()) {
			return false;
		}
		int size = 0;
		for (long temp : seats) {
			if (temp != 0) {
				TableRole role = getRole(temp);
				if (isValidatedToStart(role)) {
					++size;
				}
			}
		}
		if (size < startGameRoleNum()) {
			return false;
		}
		HashSet<Integer> gameSeatsTemp = new HashSet<>();
		for (int i = 0; i < seats.length; ++i) {
			long temp = seats[i];
			if (temp == 0) {
				continue;
			}
			TableRole role = getRole(temp);
			if (role == null) {
				continue;
			}
			if (canRoleStartGame(role)) {
				gameSeatsTemp.add(i);
			}
		}
		if (gameSeatsTemp.size() >= startGameRoleNum()) {
			isStart = true;
			for (int i = 0; i < gameSeats.length; ++i) {
				if (gameSeatsTemp.contains(i)) {
					gameSeats[i] = seats[i];
				} else {
					gameSeats[i] = 0;
				}
			}
			// 重置游戏数据
			resetGame0();
			onStartGame();
			isReseted = false;
		}
		return true;
	}

	protected abstract boolean canRoleStartGame(TableRole role);

	public abstract int startGameRoleNum();

	/**
	 * 结束游戏
	 * 
	 * @throws GameAlreadyEndException
	 * @throws NotJoinTableException
	 * @throws GameAlreadyStartException
	 */
	public synchronized void endGame()
			throws GameAlreadyEndException, NotJoinTableException, GameAlreadyStartException {
		if (!isStart()) {
			throw new GameAlreadyEndException();
		}
		isStart = false;
		onEndGame();
	}

	/**
	 * 是否在座位上
	 * 
	 * @param role
	 */
	public int isOnSeats(TableRole role) {
		for (int index = 0; index < seats.length; ++index) {
			if (seats[index] == role.getUniqueId()) {
				return index;
			}
		}
		return -1;
	}

	public int isOnGameSeats(TableRole role) {
		for (int index = 0; index < gameSeats.length; ++index) {
			if (gameSeats[index] == role.getUniqueId()) {
				return index;
			}
		}
		return -1;
	}

	public void clearRoles() throws NotJoinTableException, GameAlreadyStartException {
		foreach((role) -> {
			quitTable(role, false, true);
			return true;
		});
	}

	public void foreach(Test1<TableRole> proc) {
		List<TableRole> copy = new ArrayList<>(roles.values());
		for (TableRole role : copy) {
			if (!proc.test(role)) {
				break;
			}
		}
	}

	public void clearGameSeats() {
		gameSeats = new long[seats.length];
	}

	public AutoGrowthEntity getAutoGrothEntity() {
		return autoGrothEntity;
	}

	public long getLastJoinTime() {
		return lastJoinTime;
	}

	public long[] getGameSeats() {
		return gameSeats;
	}

	public long[] getSeats() {
		return seats;
	}

	public TableRole getRole(long id) {
		return roles.get(id);
	}

	public int getRoleCapacity() {
		return roleCapacity;
	}

	public int getSeatCapacity() {
		return seatCapacity;
	}

	public int roleSize() {
		return roles.size();
	}

	public long getCreateTime() {
		return createTime;
	}
}
