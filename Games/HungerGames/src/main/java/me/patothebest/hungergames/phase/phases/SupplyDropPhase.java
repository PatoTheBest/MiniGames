package me.patothebest.hungergames.phase.phases;

import com.google.inject.Inject;
import me.patothebest.hungergames.HungerGames;
import me.patothebest.hungergames.feature.SupplyDropFeature;
import me.patothebest.hungergames.phase.AbstractHungerGamesPhase;
import me.patothebest.hungergames.phase.PhaseType;

public class SupplyDropPhase extends AbstractHungerGamesPhase {

    @Inject private SupplyDropPhase(HungerGames plugin) {
        super(plugin);
    }

    @Override
    public void configure() {
        setPreviousPhaseFeatures(true);
        registerFeature(SupplyDropFeature.class);
    }

    @Override
    public PhaseType getPhaseType() {
        return PhaseType.SUPPLY_DROP;
    }
}