package top.limitart.game.mission;

/**
 * @author hank
 * @version 2018/4/12 0012 23:01
 */
public class CollectTarget extends MissionTarget {
    private final int gatheree;

    public CollectTarget(int goal, int gatheree) {
        super(goal);
        this.gatheree = gatheree;
    }

    public int getMonsterModel() {
        return gatheree;
    }

    @Override
    public MissionTargetType type() {
        return GameMissionTargetType.COLLECT;
    }
}
