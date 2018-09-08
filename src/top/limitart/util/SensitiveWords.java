package top.limitart.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 敏感字处理
 *
 * @author hank
 * @version 2018/9/4 0004 17:23
 */
public class SensitiveWords {
    private static Logger LOGGER = LoggerFactory.getLogger(SensitiveWords.class);
    private Node rootNode = new Node('R');

    public void init(List<String> words) {
        rootNode = new Node('R');
        LOGGER.info("start load sensative words...");
        for (String str : words) {
            char[] chars = str.toCharArray();
            if (chars.length > 0)
                insertNode(rootNode, chars, 0);
        }
        LOGGER.info("load sensative words：" + words.size());
    }

    /**
     * 是否有敏感词
     *
     * @param content
     * @return
     */
    public boolean hasBadWords(String content) {
        char[] chars = content.toCharArray();
        Node node = rootNode;
        StringBuilder buffer = new StringBuilder();
        List<String> word = new ArrayList<>();
        int a = 0;
        while (a < chars.length) {
            node = findNode(node, chars[a]);
            if (node == null) {
                node = rootNode;
                a = a - word.size();
                buffer.append(chars[a]);
                word.clear();
            } else if (node.flag == 1) {
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
    public String filter(String content, String replace) {
        char[] chars = content.toCharArray();
        Node node = rootNode;
        StringBuffer buffer = new StringBuffer();
        List<String> badList = new ArrayList<>();
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
    private void insertNode(Node node, char[] cs, int index) {
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

    private Node findNode(Node node, char c) {
        return node.nodes.get(String.valueOf(c));
    }

    private static class Node {
        public int flag;
        public Map<String, Node> nodes = new HashMap<>();

        public Node(int flag) {
            this.flag = flag;
        }
    }
}
