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
package top.limitart.game.poker;

import top.limitart.util.RandomUtil;

/**
 * 扑克工具类(扑克号码和颜色都从1开始)
 *
 * @author hank
 */
public final class Pokers {
    private final static byte CARD_COUNT = 52;
    private final static byte CARD_NUM_COUNT = 13;
    private final static byte CARD_SUIT_COUNT = 4;
    private final static byte[] STANDARD_POKER = new byte[CARD_COUNT];
    // 花色
    /**
     * 黑桃
     */
    public final static byte CARD_SUIT_SPADE = 1;
    /**
     * 红心
     */
    public final static byte CARD_SUIT_HEART = 2;
    /**
     * 方块
     */
    public final static byte CARD_SUIT_DIAMOND = 3;
    /**
     * 梅花
     */
    public final static byte CARD_SUIT_CLUB = 4;
    // 编号
    /**
     * J
     */
    public final static byte CARD_NUM_JACK = 11;
    /**
     * Q
     */
    public final static byte CARD_NUM_QUEEN = 12;
    /**
     * K
     */
    public final static byte CARD_NUM_KING = 13;
    /**
     * A
     */
    public final static byte CARD_NUM_ACE = 14;
    /**
     * 鬼
     */
    public final static byte CARD_NUM_JOKER = 15;

    static {
        for (byte i = 0; i < CARD_SUIT_COUNT; ++i) {
            for (byte j = 0; j < CARD_NUM_COUNT; ++j) {
                STANDARD_POKER[CARD_NUM_COUNT * i + j] = createCard((byte) (j + 2), (byte) (i + 1));
            }
        }
    }

    /**
     * 创造一张牌
     *
     * @param number
     * @param color
     * @return
     */
    public static byte createCard(byte number, byte color) {
        return (byte) ((number) | color << 4);
    }

    /**
     * 创造一副新牌(除去大小王)
     *
     * @return
     */
    public static byte[] createPoker() {
        byte[] template = new byte[CARD_COUNT];
        System.arraycopy(STANDARD_POKER, 0, template, 0, STANDARD_POKER.length);
        return template;
    }

    /**
     * 创建一副新牌
     *
     * @return
     */
    public static byte[] createPokerWithJoker() {
        byte[] template = new byte[CARD_COUNT + 2];
        System.arraycopy(STANDARD_POKER, 0, template, 0, STANDARD_POKER.length);
        template[52] = createCard(CARD_NUM_JOKER, CARD_SUIT_SPADE);
        template[53] = createCard(CARD_NUM_JOKER, CARD_SUIT_HEART);
        return template;
    }

    /**
     * 洗牌
     *
     * @param cards
     */
    public static void shuffle(byte[] cards) {
        for (int oldIndex = 0; oldIndex < cards.length; ++oldIndex) {
            int newIndex = RandomUtil.nextInt(0, cards.length - 1);
            if (newIndex == oldIndex) {
                continue;
            }
            byte tempCard = cards[oldIndex];
            cards[oldIndex] = cards[newIndex];
            cards[newIndex] = tempCard;
        }
    }

    /**
     * 获取牌号码
     *
     * @param value
     * @return
     */
    public static byte getCardNumber(byte value) {
        return (byte) (value & 15);
    }

    /**
     * 获取牌花色
     *
     * @param value
     * @return
     */
    public static byte getCardColor(byte value) {
        return (byte) (value >> 4);
    }

    /*
     * 是否是A
     */
    public static boolean isAce(byte card) {
        return getCardNumber(card) == CARD_NUM_ACE;
    }

    /**
     * 是否是鬼
     *
     * @param card
     * @return
     */
    public static boolean isJoker(byte card) {
        return isBigJoker(card) || isSmallJoker(card);
    }

    /**
     * 是否是大鬼
     *
     * @param card
     * @return
     */
    public static boolean isBigJoker(byte card) {
        return (getCardNumber(card) == CARD_NUM_JOKER) && (getCardColor(card) == CARD_SUIT_HEART);
    }

    /**
     * 是否是小鬼
     *
     * @param card
     * @return
     */
    public static boolean isSmallJoker(byte card) {
        return (getCardNumber(card) == CARD_NUM_JOKER) && (getCardColor(card) == CARD_SUIT_SPADE);
    }

    /**
     * 是否是相同花色
     *
     * @param card
     * @param anotherCard
     * @return
     */
    public static boolean isSameSuit(byte card, byte anotherCard) {
        return ((card & (~anotherCard)) >> 4) == 0;
    }

    /**
     * 是否是相同编号
     *
     * @param card
     * @param anotherCard
     * @return
     */
    public static boolean isSameNumber(byte card, byte anotherCard) {
        return ((card & (~anotherCard)) & 15) == 0;
    }

    /**
     * 用long储存一副牌(只支持8张牌)
     *
     * @param cards
     * @return
     */
    public static long cardsToLong(byte[] cards) {

        if (cards.length > Long.BYTES) {
            throw new IllegalArgumentException("length <=" + Long.BYTES);
        }
        long longOfCards = 0;
        for (int i = 0; i < cards.length; i++) {
            longOfCards |= (Byte.toUnsignedLong(cards[i])) << (i << 3);
        }
        return longOfCards;
    }

    /**
     * 将long转化为一副牌(只支持8张牌)
     *
     * @param value
     * @return
     */
    public static byte[] longToCards(long value) {
        int pos = 0;
        byte[] temp = new byte[Long.BYTES];
        while ((temp[pos] = (byte) ((value >> (pos << 3)) & 0XFFL)) != 0) {
            ++pos;
        }
        byte[] result = new byte[pos];
        System.arraycopy(temp, 0, result, 0, result.length);
        return result;
    }

    public static String toString(byte card) {
        byte number = getCardNumber(card);
        byte color = getCardColor(card);
        String suit = null;
        switch (color) {
            case CARD_SUIT_CLUB:
                suit = "梅花";
                break;
            case CARD_SUIT_HEART:
                suit = "红桃";
                break;
            case CARD_SUIT_DIAMOND:
                suit = "方块";
                break;
            case CARD_SUIT_SPADE:
                suit = "黑桃";
                break;
        }
        String num;
        switch (number) {
            case CARD_NUM_JACK:
                num = "J";
                break;
            case CARD_NUM_QUEEN:
                num = "Q";
                break;
            case CARD_NUM_KING:
                num = "K";
                break;
            case CARD_NUM_ACE:
                num = "A";
                break;
            default:
                num = number + "";
                break;
        }
        return num + "[" + suit + "]";
    }
}
