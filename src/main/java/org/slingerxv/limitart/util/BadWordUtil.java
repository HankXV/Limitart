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
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 敏感词工具
 * 
 * @author hank
 *
 */
public class BadWordUtil {
	private static Logger log = LoggerFactory.getLogger(BadWordUtil.class);
	private static Node rootNode = new Node('R');

	public synchronized static void init(List<String> words) {
		rootNode = new Node('R');
		log.info("start load sensative words...");
		for (String str : words) {
			char[] chars = str.toCharArray();
			if (chars.length > 0)
				insertNode(rootNode, chars, 0);
		}
		log.info("load sensative words：" + words.size());
	}

	/**
	 * 是否有敏感词
	 * 
	 * @param content
	 * @return
	 */
	public static boolean hasBadWords(String content) {
		char[] chars = content.toCharArray();
		Node node = rootNode;
		StringBuilder buffer = new StringBuilder();
		List<String> word = new ArrayList<String>();
		int a = 0;
		while (a < chars.length) {
			node = findNode(node, chars[a]);
			if (node == null) {
				node = rootNode;
				a = a - word.size();
				buffer.append(chars[a]);
				word.clear();
			} else if (node.flag == 1) {
				node = null;
				return true;
			} else {
				word.add(String.valueOf(chars[a]));
			}
			a++;
		}
		return false;
	}

	/**
	 * 过滤敏感词
	 * 
	 * @param content
	 * @return
	 */
	public static String filter(String content, String replace) {
		char[] chars = content.toCharArray();
		Node node = rootNode;
		StringBuffer buffer = new StringBuffer();
		List<String> badList = new ArrayList<String>();
		int a = 0;
		while (a < chars.length) {
			node = findNode(node, chars[a]);
			if (node == null) {
				node = rootNode;
				a = a - badList.size();
				if (badList.size() > 0) {
					badList.clear();
				}
				buffer.append(chars[a]);
			} else if (node.flag == 1) {
				badList.add(String.valueOf(chars[a]));
				for (int i = 0; i < badList.size(); i++) {
					buffer.append(replace);
				}
				node = rootNode;
				badList.clear();
			} else {
				badList.add(String.valueOf(chars[a]));
				if (a == chars.length - 1) {
					for (int i = 0; i < badList.size(); i++) {
						buffer.append(badList.get(i));
					}
				}
			}
			a++;
		}
		return buffer.toString();
	}

	/**
	 * 将单词插入根节点
	 * 
	 * @param node
	 * @param cs
	 * @param index
	 */
	private static void insertNode(Node node, char[] cs, int index) {
		int start = index;
		Node n = findNode(node, cs[start]);
		if (n == null) {
			n = new Node(cs[start]);
			node.nodes.put(String.valueOf(cs[start]), n);
		}

		if (index == (cs.length - 1))
			n.flag = 1;

		start++;
		if (start < cs.length)
			insertNode(n, cs, start);
	}

	private static Node findNode(Node node, char c) {
		return node.nodes.get(String.valueOf(c));
	}

	private static class Node {
		public int flag;
		public HashMap<String, Node> nodes = new HashMap<String, Node>();

		public Node(int flag) {
			this.flag = flag;
		}
	}
}
