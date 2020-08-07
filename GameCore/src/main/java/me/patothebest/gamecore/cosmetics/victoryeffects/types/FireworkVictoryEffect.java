package me.patothebest.gamecore.cosmetics.victoryeffects.types;

import me.patothebest.gamecore.cosmetics.victoryeffects.RepeatingVictoryEffect;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkVictoryEffect extends RepeatingVictoryEffect {

    @Override
    public void displayEffect(IPlayer player) {
        Firework firework = (Firework) player.getPlayer().getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(Utils.getRandomEffect());
        firework.setFireworkMeta(fireworkMeta);
    }

    @Override
    public long getPeriod() {
        return 20;
    }
}
