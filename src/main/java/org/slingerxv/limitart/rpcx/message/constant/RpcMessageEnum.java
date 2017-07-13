package org.slingerxv.limitart.rpcx.message.constant;

/**
 * RPC相关消息Id
 * 
 * @author hank
 *
 */
public enum RpcMessageEnum {
	/**
	 * 链接验证服务器
	 */
	RpcExecuteClientMessage((short)-101),
	/**
	 * 链接验证客户端
	 */
	RpcResultServerMessage((short)-102),
	/**
	 * 直接拉取RPC服务器服务列表
	 */
	DirectFetchProviderServicesMessage((short)-103),
	/**
	 * RPC服务器服务列表拉取结果
	 */
	DirectFetchProviderServicesResultMessage((short)-104),
	/**
	 * 推送服务到服务中心消息
	 */
	PushServiceToServiceCenterProviderMessage((short)-105),
	/**
	 * 向服务中心订阅服务
	 */
	SubscribeServiceFromServiceCenterConsumerMessage((short)-106),
	/**
	 * 服务中心订阅服务返回
	 */
	SubscribeServiceResultServiceCenterMessage((short)-107),
	/**
	 * 通知断开链接
	 */
	NoticeProviderDisconnectedServiceCenterMessage((short)-108),
	/**
	 * 向服务中心申请调度任务
	 */
	AddScheduleToServiceCenterProviderMessage((short)-109),
	/**
	 * 服务中心任务触发
	 */
	TriggerScheduleServiceCenterToProviderServiceCenterMessage((short)-110),;
	private short messageId;

	RpcMessageEnum(short messageId) {
		this.messageId = messageId;
	}

	public short getValue() {
		return this.messageId;
	}
}
