package org.slingerxv.limitart.game.innerserver;

import org.slingerxv.limitart.game.innerserver.config.InnerPublicServerConfig;
import org.slingerxv.limitart.net.binary.distributed.InnerMasterServer;
import org.slingerxv.limitart.net.binary.distributed.config.InnerMasterServerConfig.InnerMasterServerConfigBuilder;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;

/**
 * 公共服务器
 * 
 * @author hank
 *
 */
public abstract class InnerPublicServer extends InnerMasterServer {

	public InnerPublicServer(InnerPublicServerConfig config) throws MessageIDDuplicatedException {

		super(new InnerMasterServerConfigBuilder().masterPort(config.getMasterPort()).factory(config.getFactory())
				.serverName("Public").build());
	}
}
