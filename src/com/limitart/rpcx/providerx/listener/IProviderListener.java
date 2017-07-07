package com.limitart.rpcx.providerx.listener;

import com.limitart.rpcx.providerx.ProviderX;

public interface IProviderListener {
	void onServiceCenterConnected(ProviderX provider);

	void onProviderBind(ProviderX provider);
}
