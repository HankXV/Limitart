package org.slingerxv.limitart.funcs;

public class Tests {
	public static <T> boolean invoke(Test1<T> test, T t) {
		if (test == null) {
			return false;
		}
		return test.test(t);
	}

	public static <T1, T2> boolean invoke(Test2<T1, T2> test, T1 t1, T2 t2) {
		if (test == null) {
			return false;
		}
		return test.test(t1, t2);
	}
}
