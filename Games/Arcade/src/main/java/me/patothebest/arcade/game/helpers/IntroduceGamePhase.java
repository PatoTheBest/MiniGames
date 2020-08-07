package me.patothebest.arcade.game.helpers;

import com.google.inject.Inject;
import me.patothebest.arcade.game.Game;
import me.patothebest.arcade.Arcade;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.feature.features.protection.PlayerProtectionFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorProtectionFeature;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.phase.AbstractPhase;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class IntroduceGamePhase extends AbstractPhase<Arcade, Arena> implements GamePhase {

    private Game game;

    @Inject private IntroduceGamePhase(Arcade plugin) {
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
    }

    @Override
    public void start() {
        super.start();
        arena.revivePlayers();

        int i = 0;
        int n = game.getSpawns().size();
        for (Player player : arena.getPlayers()) {
            introduceGame(player);
            player.getInventory().clear();
            player.teleport(game.getSpawns().get(i++ % n));
        }

        for (Player player : arena.getSpectators()) {
            introduceGame(player);
            player.getInventory().clear();
            player.teleport(game.getSpectatorLocation());
        }
    }

    private void introduceGame(Player player) {
        Utils.sendCenteredMessage(player, CoreLang.LINE_SEPARATOR.getMessage(player));
        player.sendMessage("");
        Utils.sendCenteredMessage(player, ChatColor.WHITE.toString() + ChatColor.BOLD + game.getName());
//            player.sendMessage("");
//            player.sendMessage("");
//            player.sendMessage("");
//            player.sendMessage("");
//            player.sendMessage("");
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
