package org.slingerxv.limitart.net.binary.handler;

import org.slingerxv.limitart.net.binary.message.Message;

/**
 * 消息业务逻辑
 * 
 * @author Hank
 *
 */
public interface IHandler<T extends Message> {

	void handle(T msg);

}
