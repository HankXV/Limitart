package top.limitart.game.mission;

import top.limitart.game.GameRole;

import java.util.Collections;
import java.util.List;

/**
 * @author hank
 * @version 2018/4/13 0013 20:28
 */
public class DailyMissionHandler extends MissionHandler<GameRole, DailyMission> {
    @Override
    public DailyMission instance(GameRole executor, int missionID) {
        DailyMission mission = new DailyMission();
        mission.getProgresses().add(new KillMonsterTarget(10, 1));
        mission.getProgresses().add(new CollectTarget(4, 1));
        return mission;
    }

    @Override
    public boolean canReceiveMission(GameRole executor) {
        return executor.getDailyMission() == null;
    }

    @Override
    public List<Integer> nextMission(GameRole executor) {
        return Collections.singletonList(1);
    }

    @Override
    public MissionType type() {
        return GameMissionType.DAILY;
    }

    @Override
    public boolean onFinished(GameRole executor, Mission mission) {
        //完成领奖
        System.out.println("玩家:" + executor + "领取了日常任务奖励");
        //清空任务
        executor.setDailyMission(null);
        //自动接取下一个任务
        return true;
    }
}
