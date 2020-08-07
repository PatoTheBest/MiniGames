package me.patothebest.hungergames.stats.statistics.team;

import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.stats.AbstractStatistic;
import me.patothebest.hungergames.arena.Arena;
import me.patothebest.hungergames.arena.ArenaType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class TeamKillStatistic extends AbstractStatistic {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(CombatDeathEvent event) {
        if (event.getKillerPlayer() != null && getPlayer(event.getKillerPlayer()) != null) {
            if (!getPlayer(event.getKillerPlayer()).isInArena()) {
                return;
            }

            if(((Arena)getPlayer(event.getKillerPlayer()).getCurrentArena()).getArenaType() != ArenaType.TEAM) {
                return;
            }

            updateStat(event.getKillerPlayer(), 1);
        }
    }

    @Override
    public String getStatName() {
        return "tkills";
    }
}
