package org.slingerxv.limitart.rpcx.providerx.listener;

import org.slingerxv.limitart.rpcx.providerx.ProviderX;

public interface IProviderListener {
	void onServiceCenterConnected(ProviderX provider);

	void onProviderBind(ProviderX provider);
}
