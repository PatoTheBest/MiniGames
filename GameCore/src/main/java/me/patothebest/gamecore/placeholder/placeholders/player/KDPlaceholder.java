package me.patothebest.gamecore.placeholder.placeholders.player;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.stats.statistics.DeathStatistic;
import me.patothebest.gamecore.stats.statistics.KillStatistic;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

public class KDPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject private KDPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "kd";
    }

    @Override
    public String replace(Player player, String args) {
        IPlayer corePlayer = playerManager.getPlayer(player);

        if(corePlayer.getStatistics().get(KillStatistic.class) == null ||
                corePlayer.getStatistics().get(DeathStatistic.class) == null) {
            return "0";
        }

        double kills = corePlayer.getStatistics().get(KillStatistic.class).getAllTime();
        double deaths = corePlayer.getStatistics().get(DeathStatistic.class).getAllTime();
        return Utils.roundToTwoDecimals(kills / deaths);
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
