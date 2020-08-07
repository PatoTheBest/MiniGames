package me.patothebest.hungergames.phase.phases;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.phase.phases.EndPhase;
import me.patothebest.hungergames.phase.HungerGamesPhase;
import me.patothebest.hungergames.phase.PhaseType;

public class EndHGPhase extends EndPhase implements HungerGamesPhase<AbstractArena> {

    @Inject private EndHGPhase(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public PhaseType getPhaseType() {
        return PhaseType.END;
    }

    @Override
    public void setTimeTilNextPhase(int phsaeTime) {
    }
}
