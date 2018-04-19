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

import org.slingerxv.limitart.base.NotNull;

import java.util.Collection;

/**
 * 任务执行者
 *
 * @author hank
 * @version 2018/4/12 0012 22:11
 */
public interface MissionExecutor {
    /**
     * 获取当前执行者的所有任务
     *
     * @return 执行者所有任务集合
     */
    @NotNull
    Collection<Mission> missions();

    /**
     * 存储任务
     *
     * @param mission 任务实体
     */
    void addMission(Mission mission);
}
