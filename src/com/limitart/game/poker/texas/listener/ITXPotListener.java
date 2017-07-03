package com.limitart.game.poker.texas.listener;

public interface ITXPotListener {
	public void onAward(int index, long chips);

	public boolean canGetAward(int index);
}
