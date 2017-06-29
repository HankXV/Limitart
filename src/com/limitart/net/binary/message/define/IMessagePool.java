package com.limitart.net.binary.message.define;

import com.limitart.net.binary.message.MessageFactory;

public interface IMessagePool {
	void register(MessageFactory facotry) throws Exception;
}
