package me.patothebest.hungergames.stats.statistics.team;

import me.patothebest.gamecore.event.arena.GameEndEvent;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.stats.AbstractStatistic;
import me.patothebest.hungergames.arena.Arena;
import me.patothebest.hungergames.arena.ArenaType;
import org.bukkit.event.EventHandler;

public class TeamWinsStatistic extends AbstractStatistic {

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        if (((Arena) event.getArena()).getArenaType() != ArenaType.TEAM) {
            return;
        }

        event.getWinners().forEach(winner -> {
            IPlayer player = playerManager.getPlayer(winner);
            if (player == null) {
                return;
            }

            updateStat(player, 1);
        });
    }

    @Override
    public String getStatName() {
        return "twins";
    }
}
