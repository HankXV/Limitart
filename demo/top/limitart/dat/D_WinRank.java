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

package top.limitart.dat;


/**
 * 胜利场数排行奖励
 *
 * @author limitart
 */
public class D_WinRank extends DataMeta {
    /**
     * 起始排名
     */
    private int startRank;
    /**
     * 结束排名
     */
    private int endRank;
    /**
     * 奖励数量
     */
    private long count;

    /**
     * 起始排名
     */
    public int getStartRank() {
        return this.startRank;
    }

    /**
     * 结束排名
     */
    public int getEndRank() {
        return this.endRank;
    }

    /**
     * 奖励数量
     */
    public long getCount() {
        return this.count;
    }
}