package org.slingerxv.limitart.game.mission;

/**
 * @author hank
 * @version 2018/4/12 0012 23:01
 */
public class KillMonsterTarget extends MissionTarget {
    private final int monsterModel;

    public KillMonsterTarget(int goal, int monsterModel) {
        super(goal);
        this.monsterModel = monsterModel;
    }

    public int getMonsterModel() {
        return monsterModel;
    }

    @Override
    public MissionTargetType type() {
        return GameMissionTargetType.KILL_MONSTER;
    }
}
