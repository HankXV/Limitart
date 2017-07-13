package org.slingerxv.limitart.net.binary.listener;

public interface SendMessageListener {
	void onComplete(boolean isSuccess, Throwable cause, Channel channel);
}
