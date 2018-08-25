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
package top.limitart.game.mission;

import top.limitart.base.IntCounter;

/**
 * 任务目标
 *
 * @author hank
 * @version 2018/4/13 0013 19:53
 */
public abstract class MissionTarget {
    private final IntCounter progress = new IntCounter() {
        @Override
        public int high() {
            return goal;
        }
    };
    private final int goal;

    public MissionTarget(int goal) {
        this.goal = goal;
    }

    /**
     * 目标值
     *
     * @return
     */
    public int getGoal() {
        return progress.high();
    }

    /**
     * 更新该目标进度条
     *
     * @param count
     * @return
     */
    public int updateProgress(int count) {
        if (count <= 0) {
            return progress.getCount();
        }
        return progress.addAndGet(count);
    }

    /**
     * 获取当前进度值
     *
     * @return
     */
    public int getProgress() {
        return progress.getCount();
    }

    /**
     * 当前目标是否已经完结
     *
     * @return
     */
    public boolean finished() {
        return progress.reachHigh();
    }

    /**
     * 任务目标类型
     *
     * @return
     */
    public abstract MissionTargetType type();
}
