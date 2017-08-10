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
package org.slingerxv.limitart.util;

import java.lang.reflect.Field;

public class ObjectUtil {

	public static void copyBean(Object oldOne, Object newOne) throws IllegalArgumentException, IllegalAccessException {
		Field[] declaredFields = oldOne.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			field.setAccessible(true);
			field.set(newOne, field.get(oldOne));
		}
	}

	public static int bool2Int(boolean value) {
		return value ? 1 : 0;
	}

}
