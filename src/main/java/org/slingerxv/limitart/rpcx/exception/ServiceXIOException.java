package org.slingerxv.limitart.rpcx.exception;

import java.io.IOException;

/**
 * 服务IO错误
 * 
 * @author Hank
 *
 */
public class ServiceXIOException extends IOException {
	private static final long serialVersionUID = 1L;

	public ServiceXIOException(String info) {
		super(info);
	}

	public ServiceXIOException(Exception e) {
		super(e);
	}
}
