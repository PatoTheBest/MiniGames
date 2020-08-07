package me.patothebest.arcade.game.helpers;

import com.google.inject.Inject;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.event.player.GameJoinEvent;
import me.patothebest.gamecore.event.player.PlayerStateChangeEvent;
import me.patothebest.gamecore.phase.phases.TeamAssignPhase;
import org.bukkit.entity.Player;

public class ArcadeTeamAssignPhase extends TeamAssignPhase {

    @Inject private ArcadeTeamAssignPhase(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void start() {
        for (Player player : arena.getPlayers()) {
            ((Arena)arena).getStarCount().addPlayer(player);
            callJoinEvent(player);
        }

        ((Arena)arena).setMaxPointsPerGame(arena.getPlayers().size());
        super.start();
    }

    protected void callJoinEvent(Player player) {
        plugin.getServer().getPluginManager().callEvent(new GameJoinEvent(player, arena));
        plugin.getServer().getPluginManager().callEvent(new PlayerStateChangeEvent(player, arena, PlayerStateChangeEvent.PlayerState.PLAYER));
    }
}
