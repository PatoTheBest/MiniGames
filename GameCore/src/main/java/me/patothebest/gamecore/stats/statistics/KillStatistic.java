package me.patothebest.gamecore.stats.statistics;

import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.stats.AbstractStatistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class KillStatistic extends AbstractStatistic {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(CombatDeathEvent event) {
        if (event.getKillerPlayer() != null && getPlayer(event.getKillerPlayer()) != null) {
            updateStat(event.getKillerPlayer(), 1);
            ((CorePlayer)playerManager.getPlayer(event.getKillerPlayer())).addGameKills(1);
        }
    }

    @Override
    public String getStatName() {
        return "kills";
    }
}
