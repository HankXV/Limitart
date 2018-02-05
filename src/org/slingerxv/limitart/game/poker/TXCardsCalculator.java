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
package org.slingerxv.limitart.game.poker;

import org.slingerxv.limitart.util.CollectionUtil;
import org.slingerxv.limitart.util.EnumUtil;
import org.slingerxv.limitart.util.GameMathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 德州扑克牌型判定
 *
 * @author laiyongqiang
 */
public class TXCardsCalculator {
    // 高牌
    private final static long HIGH_CARD = TXCardRank.HIGH_CARD.ordinal() * 0x10000000000L;
    // 一对
    private final static long ONE_PAIR = TXCardRank.ONE_PAIR.ordinal() * 0x10000000000L;
    // 两对
    private final static long TWO_PAIR = TXCardRank.TWO_PAIR.ordinal() * 0x10000000000L;
    // 三条
    private final static long THREE_OF_A_KIND = TXCardRank.THREE_OF_A_KIND.ordinal() * 0x10000000000L;
    // 顺子
    private final static long STRAIGHT = TXCardRank.STRAIGHT.ordinal() * 0x10000000000L;
    // 同花
    private final static long FLUSH = TXCardRank.FLUSH.ordinal() * 0x10000000000L;
    // 葫芦
    private final static long FULL_HOUSE = TXCardRank.FULL_HOUSE.ordinal() * 0x10000000000L;
    // 四条
    private final static long FOUR_OF_A_KIND = TXCardRank.FOUR_OF_A_KIND.ordinal() * 0x10000000000L;
    // 同花顺
    private final static long STRAIGHT_FLUSH = TXCardRank.STRAIGHT_FLUSH.ordinal() * 0x10000000000L;
    // 皇家同花顺
    private final static long ROYAL_FLUSH = TXCardRank.ROYAL_FLUSH.ordinal() * 0x10000000000L;
    // 原始数据
    private byte[] cards;
    private byte[] numbers;
    private byte[] colors;
    // 对子数量
    private byte pairCount = 0;
    // 最大三条点数
    private byte maxthreeOfAKindNumber = 0;
    // 最大四条点数
    private byte maxFourOfAKindNumber = 0;
    // 顺子最大点数
    private byte maxNumberOfStraight = 0;
    // 同花花色
    private byte flush = 0;
    // 牌型
    private long rank;
    // 牌型评估值
    private long evaluator;

    /**
     * 筛选某人最大牌型
     *
     * @param tableCards
     * @param handCards
     * @return
     */
    public static TXCardsCalculator calBestCards(byte[] tableCards, byte[] handCards) {
        List<Byte> sum = new ArrayList<>();
        for (byte handCard : handCards) {
            sum.add(handCard);
        }
        for (byte tableCard : tableCards) {
            sum.add(tableCard);
        }
        // 从所有牌中任意选取五张的集合
        List<List<Byte>> cnm = GameMathUtil.CNM(sum, 5);
        // 所有组合中牌型最大的一种
        long maxValue = 0;
        TXCardsCalculator maxEval = null;
        for (List<Byte> list : cnm) {
            TXCardsCalculator temp = new TXCardsCalculator(CollectionUtil.toByteArray(list));
            if (temp.getValue() > maxValue) {
                maxEval = temp;
                maxValue = temp.getValue();
            }
        }
        return maxEval;
    }

    public TXCardsCalculator(byte[] txCards) {
        if (txCards.length != 2 && txCards.length != 5) {
            throw new IllegalArgumentException("the length must to be two or five");
        }
        numbers = new byte[txCards.length];
        colors = new byte[txCards.length];
        byte[] tempCards = new byte[txCards.length];
        System.arraycopy(txCards, 0, tempCards, 0, txCards.length);
        for (int i = 0; i < tempCards.length; ++i) {
            byte card = tempCards[i];
            numbers[i] = Pokers.getCardNumber(card);
            colors[i] = Pokers.getCardColor(card);
        }
        // 统计
        statistics();
        // 为同花顺时，不去匹配其他牌型
        if (isStraightFlush()) {
            if (maxNumberOfStraight == Pokers.CARD_NUM_ACE) {
                rank = ROYAL_FLUSH;
            }
        } else {
            // 匹配牌型
            compareRank();
        }
        // 将卡牌从大到小排序
        for (int i = 0; i < numbers.length; ++i) {
            for (int j = i + 1; j < numbers.length; ++j) {
                if (numbers[j] > numbers[i]) {
                    byte temp = numbers[i];
                    numbers[i] = numbers[j];
                    numbers[j] = temp;
                    temp = colors[i];
                    colors[i] = colors[j];
                    colors[j] = temp;
                    temp = tempCards[i];
                    tempCards[i] = tempCards[j];
                    tempCards[j] = temp;
                }
            }
        }
        // 如果顺子是A2345，将A放至末位
        if (maxNumberOfStraight == 5) {
            byte[] newCards = new byte[tempCards.length];
            for (int k = 0; k < newCards.length; k++) {
                if (k == newCards.length - 1) {
                    newCards[k] = tempCards[0];
                } else {
                    newCards[k] = tempCards[k + 1];
                }
            }
            cards = newCards;
        } else {
            cards = tempCards;
        }
        // 评估数值
        evaluator = evaluator(rank, cards);
    }

    /**
     * 解析评估数值获得牌型
     *
     * @param evaluator
     * @param cardCount
     * @return
     */
    public static TXCardRank getCardRank(long evaluator, int cardCount) {
        return EnumUtil.byOrdinal(TXCardRank.class, (int) (evaluator >> (cardCount << 3)));
    }

    // 评估数值
    public static long evaluator(long rank, byte[] txCards) {
        long evaluatorNum = rank;
        for (int i = 0; i < txCards.length; ++i) {
            evaluatorNum |= ((long) Pokers.getCardNumber(txCards[i])) << ((txCards.length - i - 1) << 3);
        }
        return evaluatorNum;
    }

    /**
     * 获取牌型
     *
     * @return
     */
    public TXCardRank getRank() {
        return EnumUtil.byOrdinal(TXCardRank.class, (int) (this.rank / 0x10000000000L));
    }

    /**
     * 获取牌价值
     *
     * @return
     */
    public long getValue() {
        return this.evaluator;
    }

    public byte[] getCards() {
        return this.cards;
    }

    /**
     * 统计卡牌的点数、花色、对子、三条、四条的数量及顺子
     */
    private void statistics() {
        // 点数容器
        byte[] cardNumbers = new byte[13];
        // 花色容器
        byte[] cardColors = new byte[4];
        for (int i = 0; i < numbers.length; ++i) {
            // 统计点数
            ++cardNumbers[numbers[i] - 2];
            // 统计花色
            ++cardColors[colors[i] - 1];
            if (cardColors[colors[i] - 1] >= 5) {
                flush = colors[i];
            }
        }
        for (int i = cardNumbers.length - 1; i >= 0; --i) {
            // 给所有对子的点数增加一个区间，用于最后对比牌内大小
            if (cardNumbers[i] > 1) {
                for (int j = 0; j < numbers.length; ++j) {
                    if (numbers[j] == i + 2) {
                        numbers[j] += 13;
                    }
                }
            }
            // 统计对子
            if (cardNumbers[i] == 2) {
                ++pairCount;
            }
            // 统计三条
            else if (cardNumbers[i] == 3 && cardNumbers[i] > maxthreeOfAKindNumber) {
                maxthreeOfAKindNumber = (byte) (i + 2);
            }
            // 统计四条
            else if (cardNumbers[i] == 4 && cardNumbers[i] > maxFourOfAKindNumber) {
                maxFourOfAKindNumber = (byte) (i + 2);
            }
            // 顺子
            for (int j = i; ; --j) {
                if (i < 3) {
                    break;
                }
                // 满足A2345特殊情况
                if (j == -1 && cardNumbers[cardNumbers.length - 1] > 0) {
                    maxNumberOfStraight = (byte) (i + 2);
                    break;
                } else if (j == -1 && cardNumbers[cardNumbers.length - 1] <= 0) {
                    break;
                }
                // 不连续
                if (cardNumbers[j] <= 0) {
                    break;
                }
                // 普通顺子
                if (j == i - 4) {
                    maxNumberOfStraight = (byte) (i + 2);
                    break;
                }
            }
        }
    }

    /**
     * 匹配牌型
     */
    private void compareRank() {
        boolean hasRank = (isFourOfAKind() || isFullHouse() || isFlush() || isStraight() || isThreeOfAKind()
                || isTwoPair() || isOnePair());
        if (!hasRank) {
            rank = HIGH_CARD;
        }
    }

    /**
     * 一对
     *
     * @return
     */
    private boolean isOnePair() {
        if (pairCount > 0) {
            rank = ONE_PAIR;
            return true;
        }
        return false;
    }

    /**
     * 两对
     *
     * @return
     */
    private boolean isTwoPair() {
        if (pairCount > 1) {
            rank = TWO_PAIR;
            return true;
        }
        return false;
    }

    /**
     * 三条
     *
     * @return
     */
    private boolean isThreeOfAKind() {
        if (maxthreeOfAKindNumber > 0) {
            rank = THREE_OF_A_KIND;
            return true;
        }
        return false;
    }

    /**
     * 顺子
     *
     * @return
     */
    private boolean isStraight() {
        if (maxNumberOfStraight > 0) {
            rank = STRAIGHT;
            return true;
        }
        return false;
    }

    /**
     * 同花
     *
     * @return
     */
    private boolean isFlush() {
        if (flush > 0) {
            rank = FLUSH;
            return true;
        }
        return false;
    }

    /**
     * 满堂彩(俘虏、葫芦)
     *
     * @return
     */
    private boolean isFullHouse() {
        if (maxthreeOfAKindNumber > 0 && pairCount > 0) {
            rank = FULL_HOUSE;
            return true;
        }
        return false;
    }

    /**
     * 四条
     *
     * @return
     */
    private boolean isFourOfAKind() {
        if (maxFourOfAKindNumber > 0) {
            rank = FOUR_OF_A_KIND;
            return true;
        }
        return false;
    }

    /**
     * 同花顺
     *
     * @return
     */
    private boolean isStraightFlush() {
        if (isStraight() && isFlush()) {
            rank = STRAIGHT_FLUSH;
            return true;
        }
        return false;
    }

}
