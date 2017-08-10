/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.util;

import java.util.ArrayList;
import java.util.List;

public final class MathUtil {
	private MathUtil() {
	}

	/**
	 * 从N个元素中选出指定数量的所有组合
	 * 
	 * @param source
	 * @param pickNum
	 * @return
	 */
	public static <E> List<List<E>> CNM(List<E> source, int getNum) {
		List<List<E>> result = new ArrayList<>();
		int pickNum = getNum;
		if (pickNum < 0) {
			pickNum = 1;
		}
		if (pickNum >= source.size()) {
			result.add(source);
		} else {
			// 第几个位置
			CNM0(result, pickNum, new ArrayList<>(), source, pickNum);
		}
		return result;
	}

	private static <E> void CNM0(List<List<E>> result, int originN, List<E> tempResult, List<E> source, int pickNum) {
		if (pickNum == 1) {
			for (E aSource : source) {
				List<E> temp = new ArrayList<>(tempResult);
				temp.add(aSource);
				result.add(temp);
			}
			return;
		}
		for (int j = 0; j < source.size() - (pickNum - 1); j++) {
			List<E> newIa = source.subList(j + 1, source.size());
			if (originN != pickNum) {
				tempResult.add(source.get(j));
				CNM0(result, originN, tempResult, newIa, pickNum - 1);
				for (int a = tempResult.size() - 1; a >= originN - pickNum; --a) {
					tempResult.remove(a);
				}

			} else {
				List<E> ab = new ArrayList<>();
				// 第几个位置确定的数
				ab.add(source.get(j));
				CNM0(result, originN, ab, newIa, pickNum - 1);
			}
		}
	}
}
