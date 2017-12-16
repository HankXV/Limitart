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
package org.slingerxv.limitart.net.binary.message.constant;

/**
 * 内部消息保留Id
 * 
 * @author hank
 *
 */
public enum InnerMessageEnum {
	ZERO((short)0),
	/**
	 * 链接验证服务器
	 */
	ConnectionValidateServerMessage((short)1),
	/**
	 * 链接验证客户端
	 */
	ConnectionValidateClientMessage((short)2),
	/**
	 * 验证链接成功服务器
	 */
	ConnectionValidateSuccessServerMessage((short)3),
	/**
	 * 客户端发送心跳
	 */
	HeartClientMessage((short)4),
	/**
	 * 服务器回复心跳
	 */
	HeartServerMessage((short)5),;
	private short messageId;

	InnerMessageEnum(short messageId) {
		this.messageId = messageId;
	}

	public short getValue() {
		return this.messageId;
	}

	public static InnerMessageEnum getTypeByValue(short value) {
		for (InnerMessageEnum type : InnerMessageEnum.values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		return null;
	}
}
