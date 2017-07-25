package org.slingerxv.limitart.game.innerserver;

import org.slingerxv.limitart.net.binary.distributed.InnerMasterServer;
import org.slingerxv.limitart.util.Beta;

/**
 * 公共服务器
 * 
 * @author hank
 *
 */
@Beta
public class InnerPublicServer extends InnerMasterServer {

	public InnerPublicServer(InnerMasterServerBuilder builder) throws Exception {
		super(builder);
	}

}
