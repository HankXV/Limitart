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
package org.slingerxv.limitart.net.http.constant;

/**
 * Http请求错误码
 * 
 * @author hank
 *
 */
public enum RequestErrorCode {
	/**
	 * 消息解析错误
	 */
	ERROR_DECODE_FAIL(1001),
	/**
	 * 空的URL
	 */
	ERROR_URL_EMPTY(1002),
	/**
	 * 禁止上传文件
	 */
	ERROR_FILE_UPLOAD_FORBBIDEN(1003),
	/**
	 * 禁止使用的方法（只允许GET POST）
	 */
	ERROR_METHOD_FORBBIDEN(1004),
	/**
	 * 参数解析错误
	 */
	ERROR_PARAM_PARSE(1005),
	/**
	 * 未注册的URL地址
	 */
	ERROR_URL_FORBBIDEN(1006),
	/**
	 * 消息解析错误
	 */
	ERROR_MESSAGE_PARSE(1007),
	/**
	 * 消息业务处理错误
	 */
	ERROR_MESSAGE_HANDLE(1008),
	/**
	 * 只能上传文件
	 */
	ERROR_FILE_UPLOAD_MUST(1009),
	/**
	 * 服务器逻辑错误
	 */
	ERROR_SERVER_LOGIC_ERROR(1010),
	/**
	 * 服务器方法错误
	 */
	ERROR_METHOD_ERROR(1011),
	/**
	 * POST解析错误
	 */
	ERROR_POST_ERROR(1012),;
	private int value;

	RequestErrorCode(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
