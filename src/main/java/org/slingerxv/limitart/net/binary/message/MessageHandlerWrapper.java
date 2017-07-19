package org.slingerxv.limitart.net.binary.message;

import org.slingerxv.limitart.net.binary.handler.IHandler;

public class MessageHandlerWrapper {
	private Message message;
	private IHandler<Message> handler;

	public MessageHandlerWrapper(Message message, IHandler<Message> handler) {
		this.message = message;
		this.handler = handler;
	}

	public void handle() throws Exception {
		handler.handle(message);
	}

	public Message getMessage() {
		return message;
	}

	public IHandler<Message> getHandler() {
		return handler;
	}

}
