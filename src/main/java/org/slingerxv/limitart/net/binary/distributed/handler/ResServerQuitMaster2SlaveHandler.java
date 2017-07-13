package org.slingerxv.limitart.net.binary.distributed.handler;

import org.slingerxv.limitart.net.binary.distributed.InnerSlaveServer;
import org.slingerxv.limitart.net.binary.distributed.message.ResServerQuitMaster2SlaveMessage;
import org.slingerxv.limitart.net.binary.handler.IHandler;

public class ResServerQuitMaster2SlaveHandler implements IHandler<ResServerQuitMaster2SlaveMessage> {

	@Override
	public void handle(ResServerQuitMaster2SlaveMessage msg) {
		((InnerSlaveServer) msg.getExtra()).ResServerQuitMaster2Slave(msg);
	}

}
