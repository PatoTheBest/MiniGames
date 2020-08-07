package me.patothebest.gamecore.util;

public final class RunningRunnable extends WrappedBukkitRunnable {

    private final Runnable runnable;

    public RunningRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }
}
