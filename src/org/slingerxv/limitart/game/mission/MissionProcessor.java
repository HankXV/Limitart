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

import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务执行器
 *
 * @author hank
 * @version 2018/4/12 0012 22:11
 */
public abstract class MissionProcessor<E extends MissionExecutor> {
    private Map<MissionType, MissionHandler> missions = new HashMap<>();
    private Map<MissionTargetType, MissionTargetHandler> targets = new HashMap<>();

    /**
     * 注册一个任务处理器
     *
     * @param handler
     */
    public void registerMission(MissionHandler handler) {
        Conditions.args(!missions.containsKey(handler.type()));
        missions.put(handler.type(), handler);
    }

    /**
     * 注册一个目标处理器
     *
     * @param target
     */
    public void registerTarget(MissionTargetHandler target) {
        Conditions.args(!targets.containsKey(target.type()));
        targets.put(target.type(), target);
    }

    /**
     * 任务目标处理器
     *
     * @param type
     * @return
     */
    private MissionTargetHandler target(MissionTargetType type) {
        return Conditions.notNull(targets.get(type), "找不到任务目标处理器:%s", type);
    }

    /**
     * 任务处理器
     *
     * @param type
     * @return
     */
    private MissionHandler handler(MissionType type) {
        return Conditions.notNull(missions.get(type), "找不到任务处理器:%s", type);
    }

    /**
     * 发送事件
     *
     * @param executor
     * @param event
     */
    public void postEvent(@NotNull E executor, @NotNull MissionEvent event) {
        //遍历所有任务
        for (Mission mission : executor.missions()) {
            MissionHandler handler = handler(mission.type());
            boolean change = false;
            for (MissionTarget target : mission.getProgresses()) {
                if (event.toWho() == target.type()) {
                    int progress = target(target.type()).computeProgress(target, event);
                    if (progress > 0) {
                        target.updateProgress(progress);
                        change = true;
                    }
                }
            }
            if (change) {
                onProgressUpdate(executor, mission);
                //如果任务完成了，尝试自动接取一次下一个任务
                if (mission.finished()) {
                    if (handler.onFinished(executor, mission)) {
                        newMission(executor, mission.type());
                    }
                }
            }
        }
    }

    /**
     * 检测是否有任务可以接并接取
     *
     * @param executor
     * @param missionType 任务类型
     */
    public void newMission(E executor, MissionType missionType) {
        MissionHandler missionHandler = handler(missionType);
        if (!missionHandler.canReceiveMission(executor)) {
            return;
        }
        List<Integer> list = missionHandler.nextMission(executor);
        if (list != null) {
            for (Integer missionID : list) {
                Mission instance = missionHandler.instance(executor, missionID);
                Conditions.args(!instance.getProgresses().isEmpty(), "任务ID:%s没有目标！", missionID);
                instance.setMissionID(missionID);
                executor.addMission(instance);
                onNewMission(executor, instance);
            }
        }
    }

    /**
     * 当角色任务进度更新时
     *
     * @param executor
     * @param mission
     */
    protected abstract void onProgressUpdate(E executor, Mission mission);

    protected abstract void onNewMission(E executor, Mission mission);
}
