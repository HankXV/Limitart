package top.limitart.game.mission;


/**
 * @author hank
 * @version 2018/4/12 0012 23:00
 */
public class KillMonsterEvent implements MissionEvent {
    private final int monsterModel;

    public KillMonsterEvent(int monsterModel) {
        this.monsterModel = monsterModel;
    }

    public int getMonsterModel() {
        return monsterModel;
    }

    @Override
    public MissionTargetType toWho() {
        return GameMissionTargetType.KILL_MONSTER;
    }
}
