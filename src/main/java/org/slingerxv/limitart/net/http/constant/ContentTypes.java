/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.net.http.constant;

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

	ContentTypes(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
