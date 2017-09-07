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
import java.lang.reflect.Modifier;

/**
 * 字段过滤器
 * 
 * @author hank
 *
 */
public interface FieldFilter {
	boolean filter(Field field);

	public static boolean isStatic(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}

	public static boolean isPrivate(Field field) {
		return Modifier.isPrivate(field.getModifiers());
	}

	public static boolean isPublic(Field field) {
		return Modifier.isPublic(field.getModifiers());
	}

	public static boolean isTransient(Field field) {
		return Modifier.isTransient(field.getModifiers());
	}

	public static boolean isProtected(Field field) {
		return Modifier.isProtected(field.getModifiers());
	}

	public static boolean isFinal(Field field) {
		return Modifier.isFinal(field.getModifiers());
	}

	public static boolean isVolatile(Field field) {
		return Modifier.isVolatile(field.getModifiers());
	}
}
