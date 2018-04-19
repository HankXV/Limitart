package org.slingerxv.limitart.game.mission;


/**
 * @author hank
 * @version 2018/4/13 0013 21:52
 */
public class CollectTargetHandler extends MissionTargetHandler<CollectTarget, CollectEvent> {
    @Override
    public int computeProgress(CollectTarget target, CollectEvent event) {
        if (target.getMonsterModel() != event.getGathereeModel()) {
            return 0;
        }
        return 1;
    }

    @Override
    public MissionTargetType type() {
        return GameMissionTargetType.COLLECT;
    }
}
