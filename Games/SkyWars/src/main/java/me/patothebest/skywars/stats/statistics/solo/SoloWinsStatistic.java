package me.patothebest.skywars.stats.statistics.solo;

import me.patothebest.gamecore.event.arena.GameEndEvent;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.stats.AbstractStatistic;
import me.patothebest.skywars.arena.Arena;
import me.patothebest.skywars.arena.ArenaType;
import org.bukkit.event.EventHandler;

public class SoloWinsStatistic extends AbstractStatistic {

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        if (((Arena) event.getArena()).getArenaType() != ArenaType.SOLO) {
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
        return "swins";
    }
}
