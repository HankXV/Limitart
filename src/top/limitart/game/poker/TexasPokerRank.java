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

/*
 * 德州牌型
 */
public enum TexasPokerRank {
    /**
     * 0 高牌
     */
    HIGH_CARD,
    /**
     * 1 一对
     */
    ONE_PAIR,
    /**
     * 2 两对
     */
    TWO_PAIR,
    /**
     * 3 三条
     */
    THREE_OF_A_KIND,
    /**
     * 4 顺子
     */
    STRAIGHT,
    /**
     * 5 同花
     */
    FLUSH,
    /**
     * 6 葫芦
     */
    FULL_HOUSE,
    /**
     * 7 四条
     */
    FOUR_OF_A_KIND,
    /**
     * 8 同花顺
     */
    STRAIGHT_FLUSH,
    /**
     * 9 皇家同花顺
     */
    ROYAL_FLUSH,;
}
