package org.slingerxv.limitart.game.item;

/**
 * 物品
 * 
 * @author hank
 *
 */
public abstract class AbstractItem implements Comparable<AbstractItem> {
	// 堆叠数量
	private int num;

	/**
	 * 堆叠数量
	 * 
	 * @return
	 */
	public int getNum() {
		return num;
	}

	/**
	 * 堆叠数量
	 * 
	 * @param num
	 */
	public void setNum(int num) {
		this.num = num;
	}

	/**
	 * 最大堆叠数量
	 * 
	 * @return
	 */
	public abstract int getMaxStackNumber();

	/**
	 * 是否为同种物品
	 * 
	 * @param another
	 * @return
	 */
	public abstract boolean isSameType(AbstractItem another);

	/**
	 * 拷贝物品
	 * 
	 * @return
	 */
	public abstract AbstractItem copy();
}
