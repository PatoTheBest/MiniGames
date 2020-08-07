package me.patothebest.gamecore.feature.features.celebration;

import com.google.inject.Inject;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.player.PlayerManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CelebrationMessagesFeature extends AbstractFeature {

    private final PlayerManager playerManager;

    @Inject private CelebrationMessagesFeature(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void initializeFeature() {
        super.initializeFeature();

        String winners = StringUtils.join(arena.getPlayers().stream().map(Player::getName).collect(Collectors.toList()), ", ");

        for (Player player : arena.getPlayers()) {
            arena.getTopKillers().put(player.getName(), playerManager.getPlayer(player).getGameKills());
        }

        List<Map.Entry<String, Integer>> entries = Utils.sortMap(arena.getTopKillers());
        String[] killer1 = entries.size() > 0 ? new String[] {entries.get(0).getKey(), entries.get(0).getValue().toString()} : null;
        String[] killer2 = entries.size() > 1 ? new String[] {entries.get(1).getKey(), entries.get(1).getValue().toString()} : null;
        String[] killer3 = entries.size() > 2 ? new String[] {entries.get(2).getKey(), entries.get(2).getValue().toString()} : null;

        for (Player player : arena.getPlayers()) {
            sendMessage(player, killer1, killer2, killer3, winners);
        }
        for (Player player : arena.getSpectators()) {
            sendMessage(player, killer1, killer2, killer3, winners);
        }
    }

    private void sendMessage(Player player, String[] killer1, String[] killer2, String[] killer3, String winners) {
        Utils.sendCenteredMessage(player, CoreLang.LINE_SEPARATOR.getMessage(player));
        player.sendMessage("");
        Utils.sendCenteredMessage(player, ChatColor.WHITE.toString() + ChatColor.BOLD + PluginConfig.GAME_TITLE);
        player.sendMessage("");
        Utils.sendCenteredMessage(player, CoreLang.WINNERS.replace(player, winners));
        player.sendMessage("");

        if(killer1 != null) {
            Utils.sendCenteredMessage(player, CoreLang.FIRST_KILLER.replace(player, killer1[0], killer1[1]));
        }

        if(killer2 != null) {
            Utils.sendCenteredMessage(player, CoreLang.SECOND_KILLER.replace(player, killer2[0], killer2[1]));
        }

        if(killer3 != null) {
            Utils.sendCenteredMessage(player, CoreLang.THIRD_KILLER.replace(player, killer3[0], killer3[1]));
        }

        player.sendMessage("");
        Utils.sendCenteredMessage(player, CoreLang.LINE_SEPARATOR.getMessage(player));
    }
}
