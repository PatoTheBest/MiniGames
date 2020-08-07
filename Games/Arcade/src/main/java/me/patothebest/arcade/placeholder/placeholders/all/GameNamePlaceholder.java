package me.patothebest.arcade.placeholder.placeholders.all;

import com.google.inject.Inject;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.game.Game;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;

public class GameNamePlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject private GameNamePlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "game_name";
    }

    @Override
    public final String replace(Player player, String args) {
        IPlayer arcadePlayer = playerManager.getPlayer(player);
        Arena arena = (Arena) arcadePlayer.getCurrentArena();
        if (arena == null) {
            return "NOT IN ARENA";
        }

        return replace(arena);
    }

    @Override
    public final String replace(AbstractArena arena) {
        Game currentGame = ((Arena) arena).getCurrentGame();
        return currentGame == null ? "Not in a game" : currentGame.getName();
    }
}
