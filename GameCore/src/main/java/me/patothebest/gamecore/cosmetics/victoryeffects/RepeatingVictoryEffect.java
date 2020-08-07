package me.patothebest.gamecore.cosmetics.victoryeffects;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.WrappedBukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class RepeatingVictoryEffect implements IVictoryEffect {

    @Inject private CorePlugin plugin;

    @Override
    public final void display(IPlayer player) {
        AtomicInteger times = new AtomicInteger(0);
        new WrappedBukkitRunnable() {
            @Override
            public void run() {
                if (!player.getPlayer().isOnline()) {
                    cancel();
                    return;
                }

                if (!player.isInArena()) {
                    cancel();
                    return;
                }

                if (times.incrementAndGet() > (20.0 / getPeriod()) * 15.0) {
                    cancel();
                    return;
                }

                displayEffect(player);
            }
        }.runTaskTimer(plugin, 0L, getPeriod());
    }

    public abstract void displayEffect(IPlayer player);

    public abstract long getPeriod();
}
