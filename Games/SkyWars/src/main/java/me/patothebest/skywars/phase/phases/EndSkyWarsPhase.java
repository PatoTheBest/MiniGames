package me.patothebest.skywars.phase.phases;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.phase.phases.EndPhase;
import me.patothebest.skywars.phase.PhaseType;
import me.patothebest.skywars.phase.SkyWarsPhase;

public class EndSkyWarsPhase extends EndPhase implements SkyWarsPhase<AbstractArena> {

    @Inject private EndSkyWarsPhase(CorePlugin plugin) {
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
