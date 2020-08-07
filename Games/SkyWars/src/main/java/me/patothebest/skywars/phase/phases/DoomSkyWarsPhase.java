package me.patothebest.skywars.phase.phases;

import com.google.inject.Inject;
import me.patothebest.gamecore.feature.features.dragons.DragonFeature;
import me.patothebest.gamecore.feature.features.other.LimitedTimePhaseFeature;
import me.patothebest.skywars.phase.AbstractSkyWarsPhase;
import me.patothebest.skywars.SkyWars;
import me.patothebest.skywars.phase.PhaseType;

public class DoomSkyWarsPhase extends AbstractSkyWarsPhase {

    private int phaseTime;

    @Inject private DoomSkyWarsPhase(SkyWars plugin) {
        super(plugin);
    }

    @Override
    public void configure() {
        setPreviousPhaseFeatures(true);
        registerFeature(DragonFeature.class);
    }

    @Override
    public void start() {
        ((LimitedTimePhaseFeature)getFeatures().get(LimitedTimePhaseFeature.class)).setTimeUntilNextStage(phaseTime);
        super.start();
    }

    @Override
    public PhaseType getPhaseType() {
        return PhaseType.DOOM;
    }

}