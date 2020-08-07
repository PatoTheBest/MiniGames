package me.patothebest.hungergames.stats.statistics.team;

import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.stats.AbstractStatistic;
import me.patothebest.hungergames.arena.Arena;
import me.patothebest.hungergames.arena.ArenaType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class TeamDeathStatistic extends AbstractStatistic {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(CombatDeathEvent event) {
        Player player = event.getPlayer();

        if(!getPlayer(player).isInArena()) {
            return;
        }

        if(((Arena)getPlayer(player).getCurrentArena()).getArenaType() != ArenaType.TEAM) {
            return;
        }

        updateStat(player, 1);
    }

    @Override
    public String getStatName() {
        return "tdeaths";
    }
}
