package org.slingerxv.limitart.collections.define;

import java.util.Comparator;

import org.slingerxv.limitart.funcs.Func;

public class DefaultRankObj implements Func<Long> {
	public static final DefaultRankComparator COMPARATOR = new DefaultRankComparator();
	private long uniqueId;
	private long param1;
	private long param2;
	private long param3;

	public DefaultRankObj(long uniqueId, long param1, long param2, long param3) {
		this.uniqueId = uniqueId;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
	}

	@Override
	public Long run() {
		return uniqueId;
	}

	@Override
	public String toString() {
		return "DefaultRankObj [uniqueId=" + uniqueId + ", param1=" + param1 + ", param2=" + param2 + ", param3="
				+ param3 + "]";
	}

	private static class DefaultRankComparator implements Comparator<DefaultRankObj> {
		@Override
		public int compare(DefaultRankObj o1, DefaultRankObj o2) {
			if (o1.hashCode() == o2.hashCode()) {
				return 0;
			} else {
				if (o1.param1 > o2.param1) {
					return -1;
				} else if (o1.param1 < o2.param1) {
					return 1;
				} else {
					if (o1.param2 > o2.param2) {
						return -1;
					} else if (o1.param2 < o2.param2) {
						return 1;
					} else {
						if (o1.param3 > o2.param3) {
							return -1;
						} else if (o1.param3 < o2.param3) {
							return 1;
						} else {
							return 0;
						}
					}
				}
			}
		}
	}
}
