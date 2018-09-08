package top.limitart.game.mission;


/**
 * @author hank
 * @version 2018/4/12 0012 23:00
 */
public class CollectEvent implements MissionEvent {
    private final int gathereeModel;

    public CollectEvent(int gathereeModel) {
        this.gathereeModel = gathereeModel;
    }

    public int getGathereeModel() {
        return gathereeModel;
    }

    @Override
    public MissionTargetType toWho() {
        return GameMissionTargetType.COLLECT;
    }
}
