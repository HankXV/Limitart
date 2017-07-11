package com.limitart.net.binary.message.define;

import com.limitart.net.binary.message.MessageFactory;
import com.limitart.net.binary.message.exception.MessageIDDuplicatedException;

public interface IMessagePool {
	void register(MessageFactory facotry) throws MessageIDDuplicatedException;
}
