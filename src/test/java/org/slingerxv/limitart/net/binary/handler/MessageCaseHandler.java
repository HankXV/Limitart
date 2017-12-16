package org.slingerxv.limitart.net.binary.handler;

public class MessageCaseHandler implements IHandler<MessageCase> {

	@Override
	public void handle(MessageCase msg) {
		System.out.println(msg.info);
	}

}
