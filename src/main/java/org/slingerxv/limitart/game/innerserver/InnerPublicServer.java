package org.slingerxv.limitart.game.innerserver;

import org.slingerxv.limitart.net.binary.distributed.InnerMasterServer;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;

/**
 * 公共服务器
 * 
 * @author hank
 *
 */
public abstract class InnerPublicServer extends InnerMasterServer {

	public InnerPublicServer(int masterPort, MessageFactory facotry) throws MessageIDDuplicatedException {
		super("Public", masterPort, facotry);
	}
}
