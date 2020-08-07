package me.patothebest.skywars.stats.statistics.solo;

import me.patothebest.gamecore.event.arena.GameEndEvent;
import me.patothebest.gamecore.event.player.PlayerLooseEvent;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.stats.AbstractStatistic;
import me.patothebest.skywars.arena.Arena;
import me.patothebest.skywars.arena.ArenaType;
import org.bukkit.event.EventHandler;

public class SoloLosesStatistic extends AbstractStatistic {

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        if(((Arena)event.getArena()).getArenaType() != ArenaType.SOLO) {
            return;
        }

        event.getLosers().forEach(loser -> {
            IPlayer player = playerManager.getPlayer(loser);
            if(player == null) {
                return;
            }

            updateStat(player, 1);
        });
    }

    @EventHandler
    public void onLoose(PlayerLooseEvent event){
        if(((Arena)event.getArena()).getArenaType() != ArenaType.SOLO) {
            return;
        }

        updateStat(event.getPlayer(), 1);
    }


    @Override
    public String getStatName() {
        return "sloses";
    }
}
