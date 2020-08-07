package me.patothebest.gamecore.stats.statistics;

import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.event.arena.GameEndEvent;
import me.patothebest.gamecore.stats.AbstractStatistic;
import org.bukkit.event.EventHandler;

public class WinsStatistic extends AbstractStatistic {

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        event.getWinners().forEach(winner -> {
            IPlayer player = playerManager.getPlayer(winner);
            if(player == null) {
                return;
            }

            updateStat(player, 1);
        });
    }

    @Override
    public String getStatName() {
        return "wins";
    }
}
