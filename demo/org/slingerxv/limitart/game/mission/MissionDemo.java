package org.slingerxv.limitart.game.mission;

import org.slingerxv.limitart.concurrent.TaskQueue;
import org.slingerxv.limitart.game.GameRole;

import java.util.concurrent.TimeUnit;

/**
 * @author hank
 * @version 2018/4/13 0013 20:24
 */
public class MissionDemo {
    public static void main(String[] args) {
        TaskQueue queue = TaskQueue.create("map");
        GameRole role = new GameRole();
        NormalMissionProcessor processor = new NormalMissionProcessor();
        //注册各种处理器
        processor.registerMission(new DailyMissionHandler());
        processor.registerTarget(new KillMonsterTargetHandler());
        processor.registerTarget(new CollectTargetHandler());
        //接取第一个日常任务
        processor.newMission(role, GameMissionType.DAILY);
        //假设玩家每秒杀一只怪(1或者2的怪)
        queue.scheduleAtFixedRate(() -> processor.postEvent(role, new KillMonsterEvent(1)), 0, 1, TimeUnit.SECONDS);
        queue.scheduleAtFixedRate(() -> processor.postEvent(role, new CollectEvent(1)), 0, 5, TimeUnit.SECONDS);
    }
}
