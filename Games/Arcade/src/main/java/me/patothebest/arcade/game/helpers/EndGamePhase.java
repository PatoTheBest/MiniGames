package me.patothebest.arcade.game.helpers;

import com.google.inject.Inject;
import me.patothebest.arcade.game.Game;
import me.patothebest.arcade.Arcade;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.arena.PointList;
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

public class EndGamePhase extends AbstractPhase<Arcade, Arena> implements GamePhase {

    private Game game;

    @Inject private EndGamePhase(Arcade plugin) {
        super(plugin);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void configure() {
        registerFeature(SpectatorProtectionFeature.class);
        registerFeature(SpectatorFeature.class);
        registerFeature(PlayerProtectionFeature.class);
        registerFeature(IntroduceGameFeature.class);
        LimitedTimePhaseFeature limitedTimePhaseFeature = registerFeature(LimitedTimePhaseFeature.class);
        limitedTimePhaseFeature.setTimeUntilNextStage(5);
    }

    @Override
    public void start() {
        super.start();
        for (Player player : arena.getPlayers()) {
            sendEndGame(player);
        }
        for (Player player : arena.getSpectators()) {
            sendEndGame(player);
        }
    }

    private void sendEndGame(Player player) {
        Utils.sendCenteredMessage(player, CoreLang.LINE_SEPARATOR.getMessage(player));
        player.sendMessage("");
        Utils.sendCenteredMessage(player, ChatColor.WHITE.toString() + ChatColor.BOLD + game.getName());
        player.sendMessage("");
        for (int i = 0; i < arena.getGameStarCount().size(); i++) {
            PointList.Entry entry = arena.getGameStarCount().get(i);
            player.sendMessage(" " + ChatColor.WHITE + (i + 1) + ". " + ChatColor.GREEN + (entry.getPlayer() == player ? ChatColor.BOLD : "") + entry.getPlayer().getName() + " " + entry.getPoints() + "⭑");
        }
        player.sendMessage("");
        Utils.sendCenteredMessage(player, CoreLang.LINE_SEPARATOR.getMessage(player));
    }

    @Override
    public ArenaState getArenaState() {
        return ArenaState.IN_GAME;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
