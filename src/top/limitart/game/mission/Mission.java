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


import java.util.LinkedList;
import java.util.List;

/**
 * 任务实体
 *
 * @author hank
 * @version 2018/3/30 0030 13:23
 */
public abstract class Mission {
    //任务ID
    private int missionID;
    //任务进度
    private final List<MissionTarget> progresses = new LinkedList<>();


    /**
     * 直接完成这个任务
     */
    public void finish() {
        progresses.forEach(t -> t.updateProgress(Integer.MAX_VALUE));
    }

    /**
     * 该任务是否完成
     *
     * @return
     */
    public boolean finished() {
        for (MissionTarget target : progresses) {
            if (!target.finished()) {
                return false;
            }
        }
        return true;
    }

    public int getMissionID() {
        return missionID;
    }

    public void setMissionID(int missionID) {
        this.missionID = missionID;
    }

    public List<MissionTarget> getProgresses() {
        return progresses;
    }

    public abstract MissionType type();
}
