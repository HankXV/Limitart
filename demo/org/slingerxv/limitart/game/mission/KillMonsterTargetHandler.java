package org.slingerxv.limitart.game.mission;

/**
 * @author hank
 * @version 2018/4/13 0013 20:25
 */
public class KillMonsterTargetHandler extends MissionTargetHandler<KillMonsterTarget, KillMonsterEvent> {
    @Override
    public int computeProgress(KillMonsterTarget target, KillMonsterEvent event) {
        //要杀死对应的怪物才生效
        if (target.getMonsterModel() != event.getMonsterModel()) {
            return 0;
        }
        return 1;
    }

    @Override
    public MissionTargetType type() {
        return GameMissionTargetType.KILL_MONSTER;
    }
}
