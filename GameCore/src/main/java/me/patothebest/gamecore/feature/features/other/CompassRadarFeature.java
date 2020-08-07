package me.patothebest.gamecore.feature.features.other;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.actionbar.ActionBar;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import org.bukkit.entity.Player;

public class CompassRadarFeature extends AbstractRunnableFeature {

    private final CorePlugin plugin;

    @Inject private CompassRadarFeature(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : arena.getPlayers()) {
            if(player.getInventory().contains(Material.COMPASS.parseMaterial())) {
                Player closest = null;
                AbstractGameTeam gameTeam = arena.getTeam(player);
                double closestDistance = 0;

                // only track players
                for (Player other : arena.getPlayers()) {
                    if(other == player) {
                        continue;
                    }

                    // don't track teammates
                    if(arena.getTeams().size() > 1) {
                        if(arena.getTeam(other) == gameTeam) {
                            continue;
                        }
                    }

                    double otherDistance = player.getLocation().distance(other.getLocation());

                    if(closest == null || otherDistance < closestDistance) {
                        closest = other;
                        closestDistance = otherDistance;
                    }
                }

                if(closest != null) {
                    player.setCompassTarget(closest.getLocation());
                    if (player.getItemInHand().getType() == Material.COMPASS.parseMaterial()) {
                        ActionBar.sendActionBar(player, CoreLang.COMPASS_TRACKER.replace(player, closest.getName(), Utils.roundToTwoDecimals(closestDistance)));
                    }
                }
            }
        }
    }

    @Override
    public void initializeFeature() {
        runTaskTimer(plugin, 0L, 10L);
    }
}
