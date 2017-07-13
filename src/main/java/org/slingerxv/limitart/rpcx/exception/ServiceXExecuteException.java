package org.slingerxv.limitart.rpcx.exception;

/**
 * 服务执行错误
 * 
 * @author Hank
 *
 */
public class ServiceXExecuteException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ServiceXExecuteException(String info) {
		super(info);
	}
}
