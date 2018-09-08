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


/**
 * 任务目标
 *
 * @author hank
 * @version 2018/4/12 0012 22:13
 */
public abstract class MissionTargetHandler<T extends MissionTarget, E extends MissionEvent> {


    /**
     * 当事件到来计算该事件增加的进度值
     *
     * @param event
     * @return
     */
    public abstract int computeProgress(T target, E event);

    /**
     * 任务目标类型
     *
     * @return
     */
    public abstract MissionTargetType type();
}
