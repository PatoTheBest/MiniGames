package me.patothebest.gamecore.feature;

import me.patothebest.gamecore.arena.AbstractArena;
import org.bukkit.event.Listener;

public abstract class AbstractFeature implements Listener, Feature {

    protected AbstractArena arena;
    private boolean hasBeenRegistered;

    @Override
    public void setArena(AbstractArena arena) {
        this.arena = arena;
    }

    @Override
    public void initializeFeature() {
        this.hasBeenRegistered = true;
    }

    @Override
    public void stopFeature() {
        this.hasBeenRegistered = false;
    }

    @Override
    public boolean hasBeenRegistered() {
        return hasBeenRegistered;
    }

    @Override
    public AbstractArena getArena() {
        return arena;
    }
}
