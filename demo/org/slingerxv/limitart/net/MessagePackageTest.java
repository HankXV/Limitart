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
package org.slingerxv.limitart.net;

/**
 * 消息生成测试
 * 
 * @author limitart
 *
 */
public class MessagePackageTest {
	private static byte PACKAGE_ID = 1;

	/**
	 * 消息Bean测试
	 * 
	 * @author limitart
	 *
	 */
	public static class MetaInfo extends org.slingerxv.limitart.net.binary.BinaryMeta {
		/**
		 * 测试ID
		 */
		public int ID;
		/**
		 * 测试名字
		 * 
		 */
		public String str;
		/**
		 * 测试列表
		 */
		public java.util.List<Integer> list = new java.util.ArrayList<>();
	}

	/**
	 * 消息测试
	 * 
	 * @author limitart
	 *
	 */
	public static class MessageTest extends org.slingerxv.limitart.net.binary.BinaryMessage {
		/**
		 * ID
		 */
		public int ID;
		/**
		 * 名称
		 */
		public String name;
		/**
		 * bean列表
		 */
		public java.util.List<MetaInfo> metas = new java.util.ArrayList<>();

		@Override
		public short messageID() {
			return org.slingerxv.limitart.net.binary.BinaryMessages.createID(PACKAGE_ID, 1);
		}

	}

	/**
	 * 消息测试1
	 * 
	 * @author limitart
	 *
	 */
	public static class MessageTest1 extends org.slingerxv.limitart.net.binary.BinaryMessage {
		/**
		 * ID
		 */
		public int ID;
		/**
		 * 名称
		 */
		public String name;
		/**
		 * bean列表
		 */
		public java.util.List<MetaInfo> metas = new java.util.ArrayList<>();

		@Override
		public short messageID() {
			return org.slingerxv.limitart.net.binary.BinaryMessages.createID(PACKAGE_ID, 2);
		}

	}
}
