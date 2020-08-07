package me.patothebest.gamecore.phase.phases;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.phase.AbstractPhase;
import me.patothebest.gamecore.phase.Phase;

public class NullPhase extends AbstractPhase<CorePlugin, AbstractArena> {

    @Inject private NullPhase(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public Phase getNextPhase() {
        return this;
    }

    @Override
    public ArenaState getArenaState() {
        return ArenaState.OTHER;
    }
}
