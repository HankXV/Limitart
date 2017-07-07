package com.limitart.game.bag.filter;

import com.limitart.game.item.AbstractItem;

/**
 * 物品过滤器
 * 
 * @author hank
 *
 */
public interface IItemFilter {
	boolean filter(AbstractItem item);
}
