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
package org.slingerxv.limitart.game.mission;

import java.util.List;

/**
 * 任务处理器
 *
 * @author hank
 * @version 2018/4/12 0012 22:15
 */
public abstract class MissionHandler<E extends MissionExecutor, M extends Mission> {
    /**
     * 构建一个实体
     *
     * @param executor
     * @return
     */
    public abstract M instance(E executor, int missionID);

    /**
     * 能否接取下一个任务
     *
     * @param executor
     * @return
     */
    public abstract boolean canReceiveMission(E executor);

    /**
     * 寻找下一个任务
     *
     * @param executor
     * @return 可以接取的任务ID列表
     */
    public abstract List<Integer> nextMission(E executor);

    /**
     * 任务类型
     *
     * @return
     */
    public abstract MissionType type();

    /**
     * 当任务完成时候
     *
     * @param executor
     * @param mission
     * @return 是否自动接取下个任务
     */
    public abstract boolean onFinished(E executor, Mission mission);
}
