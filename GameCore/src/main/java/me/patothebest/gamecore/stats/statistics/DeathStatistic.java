package me.patothebest.gamecore.stats.statistics;

import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.stats.AbstractStatistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class DeathStatistic extends AbstractStatistic {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(CombatDeathEvent event) {
        Player player = event.getPlayer();

        if(!getPlayer(player).isInArena()) {
            return;
        }

        updateStat(player, 1);
        ((CorePlayer)playerManager.getPlayer(player)).addGameDeaths(1);
    }

    @Override
    public String getStatName() {
        return "deaths";
    }
}
