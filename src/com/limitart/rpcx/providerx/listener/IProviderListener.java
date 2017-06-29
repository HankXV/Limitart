package com.limitart.rpcx.providerx.listener;

import com.limitart.rpcx.providerx.ProviderX;

public interface IProviderListener {
	public void onServiceCenterConnected(ProviderX provider);

	public void onProviderBind(ProviderX provider);
}
