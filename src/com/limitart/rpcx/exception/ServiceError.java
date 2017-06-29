package com.limitart.rpcx.exception;

/**
 * RPC服务服务器返回错误类型
 * 
 * @author Hank
 *
 */
public class ServiceError {
	/**
	 * 成功
	 */
	public static int SUCCESS = 0;
	/**
	 * 没有模块
	 */
	public static int SERVER_HAS_NO_MODULE = 1;
	/**
	 * 没有方法
	 */
	public static int SERVER_HAS_NO_METHOD = 2;
}
