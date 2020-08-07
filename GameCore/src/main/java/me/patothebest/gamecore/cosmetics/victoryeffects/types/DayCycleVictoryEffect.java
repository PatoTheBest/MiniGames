package me.patothebest.gamecore.cosmetics.victoryeffects.types;

import me.patothebest.gamecore.cosmetics.victoryeffects.RepeatingVictoryEffect;
import me.patothebest.gamecore.player.IPlayer;
import org.bukkit.World;

public class DayCycleVictoryEffect extends RepeatingVictoryEffect {

    @Override
    public void displayEffect(IPlayer player) {
        World world = player.getCurrentArena().getWorld();
        world.setTime(world.getTime() + 100L);
    }

    @Override
    public long getPeriod() {
        return 1;
    }
}
