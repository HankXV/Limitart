package org.slingerxv.limitart.net.http.util;

public class HttpResult {
	private byte[] result;
	private int status;

	public byte[] getResult() {
		return result;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
