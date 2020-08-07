package me.patothebest.arcade.game.helpers;

import com.google.inject.Inject;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.arena.PointList;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class CelebrationFeature extends AbstractRunnableFeature {

    private final CorePlugin plugin;
    private int stage;

    @Inject private CelebrationFeature(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if(stage == 11) {
            cancel();
            return;
        }

        PointList pointList = ((Arena)arena).getStarCount();
        for (int i = 0; i < 3 && i < pointList.size(); i++) {
            Player player = pointList.get(i).getPlayer();
            Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.addEffect(Utils.getRandomEffect());
            firework.setFireworkMeta(fireworkMeta);
        }
        stage++;
    }

    @Override
    public void initializeFeature() {
        stage = 0;
        runTaskTimer(plugin, 0L, 10L);

        for (Player player : arena.getPlayers()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }
}
