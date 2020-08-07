package me.patothebest.arcade.placeholder.placeholders.player.points;

import com.google.inject.Inject;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.game.Game;
import me.patothebest.arcade.game.goal.PointGoal;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;

public class GamePointsPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject private GamePointsPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "game_points";
    }

    @Override
    public final String replace(Player player, String args) {
        IPlayer arcadePlayer = playerManager.getPlayer(player);
        Arena arena = (Arena) arcadePlayer.getCurrentArena();
        if (arena == null) {
            return "NOT IN ARENA";
        }

        Game game = arena.getCurrentGame();
        if (game == null) {
            return "NOT IN GAME";
        }

        if (!(game.getGoal() instanceof PointGoal)) {
            return "GAME IS NOT POINT GAME";
        }

        PointGoal pointGoal = (PointGoal) game.getGoal();

        return pointGoal.getPointList().getPoints(player) + "";
    }

    @Override
    public final String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
