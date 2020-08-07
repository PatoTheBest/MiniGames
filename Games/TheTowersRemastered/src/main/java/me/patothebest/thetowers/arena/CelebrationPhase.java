package me.patothebest.thetowers.arena;

import com.google.inject.Inject;
import me.patothebest.thetowers.features.celebration.CelebrationFeature;
import me.patothebest.thetowers.features.celebration.CelebrationMessagesFeature;
import me.patothebest.thetowers.features.celebration.CelebrationTitleFeature;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.feature.features.other.LimitedTimePhaseFeature;
import me.patothebest.gamecore.feature.features.protection.PlayerProtectionFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorProtectionFeature;
import me.patothebest.gamecore.phase.AbstractPhase;

public class CelebrationPhase extends AbstractPhase<CorePlugin, AbstractArena> {

    @Inject private CelebrationPhase(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void configure() {
        registerFeature(CelebrationFeature.class);
        registerFeature(CelebrationTitleFeature.class);
        registerFeature(CelebrationMessagesFeature.class);
        registerFeature(SpectatorProtectionFeature.class);
        registerFeature(LimitedTimePhaseFeature.class);
        registerFeature(PlayerProtectionFeature.class);
    }

    @Override
    public void start() {
        ((LimitedTimePhaseFeature)getFeatures().get(LimitedTimePhaseFeature.class)).setTimeUntilNextStage(20);

        super.start();
    }

    @Override
    public ArenaState getArenaState() {
        return ArenaState.ENDING;
    }
}
