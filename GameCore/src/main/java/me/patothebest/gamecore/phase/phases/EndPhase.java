package me.patothebest.gamecore.phase.phases;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.phase.AbstractPhase;

import javax.inject.Inject;

public class EndPhase extends AbstractPhase<CorePlugin, AbstractArena> {

    @Inject public EndPhase(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void start() {
        arena.endArena(true);
    }

    @Override
    public ArenaState getArenaState() {
        return ArenaState.RESTARTING;
    }
}
