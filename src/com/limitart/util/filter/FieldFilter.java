package com.limitart.util.filter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 字段过滤器
 * 
 * @author hank
 *
 */
public abstract class FieldFilter {
	public abstract boolean filter(Field field);

	public static boolean isStatic(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isStatic(modifiers);
	}

	public static boolean isPrivate(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isPrivate(modifiers);
	}

	public static boolean isPublic(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isPublic(modifiers);
	}
}
