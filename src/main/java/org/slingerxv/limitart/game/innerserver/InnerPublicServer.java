package org.slingerxv.limitart.game.innerserver;

import org.slingerxv.limitart.net.binary.distributed.InnerMasterServer;

/**
 * 公共服务器
 * 
 * @author hank
 *
 */
public class InnerPublicServer extends InnerMasterServer {

	public InnerPublicServer(InnerMasterServerBuilder builder) throws Exception {
		super(builder);
	}

}
