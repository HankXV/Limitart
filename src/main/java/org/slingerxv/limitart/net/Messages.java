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

public final class Messages {

	private Messages() {
	}

	public static String ID2String(short ID) {
		return String.format("0X%04x", ID).toUpperCase();
	}

	/**
	 * 构造消息ID
	 * 
	 * @param modID
	 * @param mID
	 * @return
	 * @throws MessageIDException
	 */
	public static short createID(int modID, int mID) throws MessageIDException {
		if (modID > Short.MAX_VALUE || modID < Short.MIN_VALUE) {
			throw new MessageIDException(modID);
		}
		if (mID > Short.MAX_VALUE || mID < Short.MIN_VALUE) {
			throw new MessageIDException(mID);
		}
		return (short) (modID << 8 | mID);
	}
}
