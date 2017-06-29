package com.limitart.game.bag.demo;

import com.limitart.game.item.AbstractItem;

public class ItemDemo extends AbstractItem {

	@Override
	public int getMaxStackNumber() {
		return 99;
	}

	@Override
	public boolean isSameType(AbstractItem another) {
		return true;
	}

	@Override
	public AbstractItem copy() {
		return new ItemDemo();
	}

	@Override
	public int compareTo(AbstractItem o) {
		return 0;
	}

}
