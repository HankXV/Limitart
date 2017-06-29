package com.limitart.net.http.constant;

public enum ContentTypes {
	/**
	 * 二进制流
	 */
	application_octet_stream("application/octet-stream"),
	/**
	 * jpg格式
	 */
	image_jpeg("image/jpeg"),
	/**
	 * 普通文本
	 */
	text_plain("text/plain; charset=UTF-8"),
	/**
	 * html
	 */
	text_html("text/html; charset=UTF-8"),
	/**
	 * json
	 */
	application_json("application/json; charset=UTF-8");
	private String value;

	private ContentTypes(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
