package org.slingerxv.limitart.util.filter;

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
