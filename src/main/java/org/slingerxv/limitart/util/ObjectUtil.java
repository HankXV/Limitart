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

}
