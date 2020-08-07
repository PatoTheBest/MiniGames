package me.patothebest.gamecore.placeholder.placeholders.player;

import com.google.inject.Inject;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.StatsManager;
import org.bukkit.entity.Player;

public class StatsPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;
    private final StatsManager statsManager;

    @Inject private StatsPlaceholder(PlayerManager playerManager, StatsManager statsManager) {
        this.playerManager = playerManager;
        this.statsManager = statsManager;
    }

    @Override
    public String getPlaceholderName() {
        return "stats";
    }

    @Override
    public String replace(Player player, String args) {
        if(args == null) {
            return "USAGE eg. {stats:kills)";
        }

        Statistic statistic = statsManager.getStatisticByName(args);

        if(statistic == null) {
            return "Unknown stat " + args;
        }

        IPlayer corePlayer = playerManager.getPlayer(player);

        if (!corePlayer.isAllDataLoaded()) {
            return CoreLang.LOADING.getMessage(player);
        }

        if(corePlayer.getStatistics().get(statistic.getClass()) == null) {
            return "0";
        }

        return corePlayer.getStatistics().get(statistic.getClass()).getAllTime() + "";
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
