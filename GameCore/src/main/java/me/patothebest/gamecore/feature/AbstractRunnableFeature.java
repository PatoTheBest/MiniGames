package me.patothebest.gamecore.feature;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.util.WrappedBukkitRunnable;
import org.bukkit.event.Listener;

public abstract class AbstractRunnableFeature extends WrappedBukkitRunnable implements Listener, Feature {

    protected AbstractArena arena;

    @Override
    public final AbstractArena getArena() {
        return arena;
    }

    @Override
    public final void setArena(AbstractArena arena) {
        this.arena = arena;
    }

    @Override
    public void stopFeature() {
        cancel();
    }

    @Override
    public final boolean hasBeenRegistered() {
        return hasBeenScheduled();
    }
}
