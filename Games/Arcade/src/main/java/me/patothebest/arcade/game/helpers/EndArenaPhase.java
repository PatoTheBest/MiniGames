package me.patothebest.arcade.game.helpers;

import com.google.inject.Inject;
import me.patothebest.arcade.Arcade;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.arena.PointList;
import me.patothebest.arcade.lang.Lang;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.feature.features.other.LimitedTimePhaseFeature;
import me.patothebest.gamecore.feature.features.protection.PlayerProtectionFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorProtectionFeature;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.phase.AbstractPhase;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EndArenaPhase extends AbstractPhase<Arcade, Arena> {

    private final static Lang[] PLACE_MESSAGES = {Lang.FIRST_PLACE, Lang.SECOND_PLACE, Lang.THIRD_PLACE};

    @Inject private EndArenaPhase(Arcade plugin) {
        super(plugin);
    }

    @Override
    public void configure() {
        registerFeature(SpectatorProtectionFeature.class);
        registerFeature(SpectatorFeature.class);
        registerFeature(PlayerProtectionFeature.class);
        registerFeature(CelebrationFeature.class);
        LimitedTimePhaseFeature limitedTimePhaseFeature = registerFeature(LimitedTimePhaseFeature.class);
        limitedTimePhaseFeature.setTimeUntilNextStage(5);
    }

    @Override
    public void start() {
        super.start();
        arena.revivePlayers();
        for (Player player : arena.getPlayers()) {
            sendEndGame(player);
        }
        for (Player player : arena.getSpectators()) {
            sendEndGame(player);
        }
    }

    private void sendEndGame(Player player) {
        player.teleport(arena.getLobbyLocation());
        Utils.sendCenteredMessage(player, CoreLang.LINE_SEPARATOR.getMessage(player));
        Utils.sendCenteredMessage(player, ChatColor.WHITE.toString() + ChatColor.BOLD + PluginConfig.GAME_TITLE);
        player.sendMessage("");

        for (int i = 0; i < 3 && i < arena.getStarCount().size(); i++) {
            PointList.Entry entry = arena.getStarCount().get(i);
            PLACE_MESSAGES[i].replaceAndSend(player, entry.getPlayer().getName(), entry.getPoints());
        }
        player.sendMessage("");
        Utils.sendCenteredMessage(player, CoreLang.LINE_SEPARATOR.getMessage(player));
    }

    @Override
    public ArenaState getArenaState() {
        return ArenaState.IN_GAME;
    }
}
