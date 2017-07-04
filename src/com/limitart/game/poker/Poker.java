package com.limitart.game.poker;

import com.limitart.util.RandomUtil;

/**
 * 扑克工具类(扑克号码和颜色都从1开始)
 * 
 * @author hank
 *
 */
public class Poker {
	private final static byte CARD_COUNT = 52;
	private final static byte CARD_NUM_COUNT = 13;
	private final static byte CARD_SUIT_COUNT = 4;
	private final static byte[] STANDARD_POKER = new byte[CARD_COUNT];
	// 花色
	public final static byte CARD_SUIT_HEART = 1;
	public final static byte CARD_SUIT_CLUB = 2;
	public final static byte CARD_SUIT_DIAMOND = 3;
	public final static byte CARD_SUIT_SPADE = 4;
	public final static byte CARD_SUIT_JOKER = 5;
	// 编号
	public final static byte CARD_NUM_JACK = 11;
	public final static byte CARD_NUM_QUEEN = 12;
	public final static byte CARD_NUM_KING = 13;
	public final static byte CARD_NUM_ACE = 14;
	public final static byte CARD_NUM_JOKER_SMALL = 1;
	public final static byte CARD_NUM_JOKER_BIG = 2;

	static {
		for (byte i = 0; i < CARD_SUIT_COUNT; ++i) {
			for (byte j = 0; j < CARD_NUM_COUNT; ++j) {
				STANDARD_POKER[CARD_NUM_COUNT * i + j] = createCard((byte) (j + 2), (byte) (i + 1));
			}
		}
	}

	public static byte createCard(byte number, byte color) {
		return (byte) ((number << 3) | (color));
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
		template[52] = createCard(CARD_NUM_JOKER_SMALL, CARD_SUIT_JOKER);
		template[53] = createCard(CARD_NUM_JOKER_BIG, CARD_SUIT_JOKER);
		return template;
	}

	/**
	 * 洗牌
	 * 
	 * @param cards
	 */
	public static void shuffle(byte[] cards) {
		for (int oldIndex = 0; oldIndex < cards.length; ++oldIndex) {
			int newIndex = RandomUtil.randomInt(0, cards.length - 1);
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
		return (byte) (value >> 3);
	}

	/**
	 * 获取牌花色
	 * 
	 * @see PokerColor
	 * @param value
	 * @return
	 */
	public static byte getCardColor(byte value) {
		return (byte) (value & 7);
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
		return getCardColor(card) == CARD_SUIT_JOKER;
	}

	/**
	 * 是否是大鬼
	 * 
	 * @param card
	 * @return
	 */
	public static boolean isBigJoker(byte card) {
		return isJoker(card) && getCardNumber(card) == CARD_NUM_JOKER_BIG;
	}

	/**
	 * 是否是小鬼
	 * 
	 * @param card
	 * @return
	 */
	public static boolean isSmallJoker(byte card) {
		return isJoker(card) && getCardNumber(card) == CARD_NUM_JOKER_SMALL;
	}

	/**
	 * 是否是相同花色
	 * 
	 * @param card
	 * @param anotherCard
	 * @return
	 */
	public static boolean isSameSuit(byte card, byte anotherCard) {
		return (card & anotherCard & 7) != 0;
	}

	/**
	 * 是否是相同编号
	 * 
	 * @param card
	 * @param anotherCard
	 * @return
	 */
	public static boolean isSameNumber(byte card, byte anotherCard) {
		return (card & anotherCard) >> 3 != 0;
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
			longOfCards |= ((long) cards[i]) << i * Byte.SIZE;
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
		while ((temp[pos] = (byte) ((value >> (pos * Byte.SIZE)) & 0XFFL)) != 0) {
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
		if (color == CARD_SUIT_CLUB) {
			suit = "梅花";
		} else if (color == CARD_SUIT_HEART) {
			suit = "红桃";
		} else if (color == CARD_SUIT_DIAMOND) {
			suit = "方块";
		} else if (color == CARD_SUIT_SPADE) {
			suit = "黑桃";
		}
		String num = null;
		if (number == CARD_NUM_JACK) {
			num = "J";
		} else if (number == CARD_NUM_QUEEN) {
			num = "Q";
		} else if (number == CARD_NUM_KING) {
			num = "K";
		} else if (number == CARD_NUM_ACE) {
			num = "A";
		} else {
			num = number + "";
		}
		return num + "[" + suit + "]";
	}
}
