package me.patothebest.skywars.phase.phases;

import com.google.inject.Inject;
import me.patothebest.gamecore.feature.features.chests.refill.ChestRefillFeature;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.phase.phases.CagePhase;
import me.patothebest.gamecore.util.Sounds;
import me.patothebest.skywars.phase.AbstractSkyWarsPhase;
import me.patothebest.skywars.SkyWars;
import me.patothebest.skywars.phase.PhaseType;

public class RefillSkyWarsPhase extends AbstractSkyWarsPhase {

    @Inject private RefillSkyWarsPhase(SkyWars plugin) {
        super(plugin);
    }

    @Override
    public void configure() {
        setPreviousPhaseFeatures(true);
    }

    @Override
    public void start() {
        arena.getFeature(ChestRefillFeature.class).refill();
        if (!(arena.getPreviousPhase(this) instanceof CagePhase)) {
            arena.playSound(Sounds.BLOCK_CHEST_OPEN);
            arena.sendTitleToArena((player, titleBuilder) ->
                    titleBuilder.withFadeInTime(1)
                    .withStayTime(2)
                    .withFadeOutTime(1)
                    .withTitle(CoreLang.CHESTS_REFILLED.getMessage(player)));
        }
        super.start();
    }

    @Override
    public PhaseType getPhaseType() {
        return PhaseType.REFILL;
    }
}