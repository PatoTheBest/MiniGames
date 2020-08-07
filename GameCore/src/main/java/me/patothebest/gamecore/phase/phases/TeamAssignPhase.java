package me.patothebest.gamecore.phase.phases;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.phase.AbstractPhase;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import org.bukkit.entity.Player;

public class TeamAssignPhase extends AbstractPhase<CorePlugin, AbstractArena> {

    @Inject protected TeamAssignPhase(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void start() {
        arena.getTeamPreferences().forEach((key, value) -> {
            for (Player player : value) {
                key.addPlayer(player);
            }
        });

        // iterate over each player
        arena.getPlayers().forEach(player -> {
            if(arena.getTeam(player) != null) {
                return;
            }

            // get a team for the player and add him to it
            AbstractGameTeam gameTeam = arena.getNewTeamForPlayer(player);
            gameTeam.addPlayer(player);
        });

        // clear the team preferences
        arena.getTeamPreferences().clear();

        arena.nextPhase();
    }

    @Override
    public void stop() {

    }

    @Override
    public ArenaState getArenaState() {
        return ArenaState.STARTING;
    }
}
