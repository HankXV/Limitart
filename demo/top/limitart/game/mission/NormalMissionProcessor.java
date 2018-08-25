package top.limitart.game.mission;

import top.limitart.game.GameRole;

/**
 * @author hank
 * @version 2018/4/12 0012 23:01
 */
public class NormalMissionProcessor extends MissionProcessor<GameRole> {
    @Override
    protected void onProgressUpdate(GameRole executor, Mission mission) {
        System.out.println("任务:" + mission + "更新进度，是否完成:" + mission.finished());
    }

    @Override
    protected void onNewMission(GameRole executor, Mission mission) {
        System.out.println("role:" + executor + "接取新任务:" + mission);
    }
}
