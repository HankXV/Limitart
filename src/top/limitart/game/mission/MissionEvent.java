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

import top.limitart.event.Event;

/**
 * 任务事件
 *
 * @author hank
 * @version 2018/4/12 0012 22:00
 */
public interface MissionEvent extends Event {
    /**
     * 任务事件触发给哪个目标
     *
     * @return 任务目标类型
     */
    MissionTargetType toWho();
}
