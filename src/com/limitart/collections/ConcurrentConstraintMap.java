package com.limitart.collections;

import java.util.concurrent.ConcurrentHashMap;

/*
 * 并发String约束性Map
 */
public class ConcurrentConstraintMap<K> extends ConstraintMap<K> {
	public ConcurrentConstraintMap() {
		super(new ConcurrentHashMap<K, Object>());
	}
}
