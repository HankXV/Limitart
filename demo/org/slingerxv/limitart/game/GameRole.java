package org.slingerxv.limitart.game;

import org.slingerxv.limitart.game.mission.*;

import java.util.*;

/**
 * @author hank
 * @version 2018/4/12 0012 23:02
 */
public class GameRole implements MissionExecutor {
    private Mission dailyMission;

    @Override
    public Collection<Mission> missions() {
        List<Mission> missions = new LinkedList<>();
        if (dailyMission != null) {
            missions.add(dailyMission);
        }
        return missions;
    }

    @Override
    public void addMission(Mission mission) {
        if (mission.type() == GameMissionType.DAILY) {
            this.dailyMission = mission;
        }
    }


    public Mission getDailyMission() {
        return dailyMission;
    }

    public void setDailyMission(Mission dailyMission) {
        this.dailyMission = dailyMission;
    }
}
