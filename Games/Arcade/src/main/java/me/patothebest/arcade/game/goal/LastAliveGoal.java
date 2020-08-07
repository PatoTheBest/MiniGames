package me.patothebest.arcade.game.goal;

import me.patothebest.gamecore.event.player.PlayerStateChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.LinkedList;

public class LastAliveGoal extends AbstractGoal {

    private final LinkedList<Player> places = new LinkedList<>();

    @EventHandler
    public void onPlayerOut(PlayerStateChangeEvent event) {
        if (event.getPlayerState() != PlayerStateChangeEvent.PlayerState.SPECTATOR) {
            return;
        }

        if (event.getArena() != arena) {
            return;
        }

        places.add(0, event.getPlayer());

        if (arena.getPlayers().size() > 1) {
            return;
        }

        places.add(0, arena.getPlayers().iterator().next());

        arena.getGameStarCount().clear();
        for (int i = 0; i < places.size(); i++) {
            Player player = places.get(i);
            arena.getStarCount().addPoints(player, (arena.getMaxPointsPerGame() - i));
            arena.getGameStarCount().addPlayer(player);
            arena.getGameStarCount().addPoints(player, (arena.getMaxPointsPerGame() - i));
        }

        arena.nextPhase();
    }

    @Override
    public LinkedList<Player> getPlacement() {
        return places;
    }

    @Override
    public void reset() {
        places.clear();
    }
}
