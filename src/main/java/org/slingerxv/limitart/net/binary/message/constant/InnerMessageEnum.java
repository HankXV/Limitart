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
