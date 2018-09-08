package top.limitart.game.mission;

/**
 * 日常任务
 *
 * @author hank
 * @version 2018/4/12 0012 23:03
 */
public class DailyMission extends Mission {
    /**
     * 每天能接取的最大日常任务数量
     */
    public static final int MAX_DAILY = 10;

    @Override
    public MissionType type() {
        return GameMissionType.DAILY;
    }
}
