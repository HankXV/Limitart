package org.slingerxv.limitart.game.table;

import io.netty.channel.Channel;

public class TableRole {
	private long uniqueId;
	private Channel channel;
	private boolean isGiveUpHandle;

	public long getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(long uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public boolean isGiveUpHandle() {
		return isGiveUpHandle;
	}

	public void setGiveUpHandle(boolean isGiveUpHandle) {
		this.isGiveUpHandle = isGiveUpHandle;
	}
}
