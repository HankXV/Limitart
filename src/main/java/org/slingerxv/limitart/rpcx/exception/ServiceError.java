/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.rpcx.exception;

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
