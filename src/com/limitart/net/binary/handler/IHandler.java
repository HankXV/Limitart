package com.limitart.net.binary.handler;

import com.limitart.net.binary.message.Message;

/**
 * 消息业务逻辑
 * 
 * @author Hank
 *
 */
public interface IHandler {

	void handle(Message message);

}
