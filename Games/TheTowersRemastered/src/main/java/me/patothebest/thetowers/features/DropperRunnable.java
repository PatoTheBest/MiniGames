package me.patothebest.thetowers.features;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.feature.Feature;
import me.patothebest.gamecore.util.WrappedBukkitRunnable;
import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.ItemDropper;

public class DropperRunnable extends WrappedBukkitRunnable implements Feature {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private Arena arena;
    private volatile boolean running = false;

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void run() {
        // tick every item dropper
        arena.getDroppers().values().forEach(ItemDropper::tick);
    }

    private void startTicking() {
        // If is already running...
        if(running) {
            // ...return
            return;
        }

        // run the task
        runTaskTimer(arena.getPlugin(), 0L, 1L);
        running = true;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        // If the task has not been scheduled yet...
        if(!hasBeenScheduled()) {
            // ...return
            return;
        }

        // set running to false
        running = false;

        // cancel the actual task
        super.cancel();
    }

    @Override
    public String toString() {
        return "DropperRunnable{" +
                "arena=" + arena.getName() +
                ", running=" + running +
                '}';
    }

    @Override
    public void initializeFeature() {
        startTicking();
    }

    @Override
    public void stopFeature() {
        cancel();
    }

    @Override
    public boolean hasBeenRegistered() {
        return hasBeenScheduled();
    }

    @Override
    public void setArena(AbstractArena arena) {
        this.arena = (Arena) arena;
    }

    @Override
    public Arena getArena() {
        return arena;
    }
}