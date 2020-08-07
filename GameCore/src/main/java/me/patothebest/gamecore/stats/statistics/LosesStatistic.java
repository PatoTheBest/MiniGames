package me.patothebest.gamecore.stats.statistics;

import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.event.arena.GameEndEvent;
import me.patothebest.gamecore.event.player.PlayerLooseEvent;
import me.patothebest.gamecore.stats.AbstractStatistic;
import org.bukkit.event.EventHandler;

public class LosesStatistic extends AbstractStatistic {

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        event.getLosers().forEach(loser -> {
            IPlayer player = getPlayer(loser);
            if(player == null) {
                return;
            }

            updateStat(player, 1);
        });
    }

    @EventHandler
    public void onLoose(PlayerLooseEvent event){
        updateStat(event.getPlayer(), 1);
    }

    @Override
    public String getStatName() {
        return "loses";
    }
}
