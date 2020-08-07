package me.patothebest.gamecore.addon.addons;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.event.player.GameJoinEvent;
import me.patothebest.gamecore.ghost.GhostFactory;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.addon.Addon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamNametags extends Addon {

    private final PlayerManager playerManager;
    private final GhostFactory ghostFactory;
    private String spectatorPrefix;

    @Inject private TeamNametags(PlayerManager playerManager, GhostFactory ghostFactory) {
        this.playerManager = playerManager;
        this.ghostFactory = ghostFactory;
    }

    @Override
    public void configure(ConfigurationSection addonConfigSection) {
        spectatorPrefix = addonConfigSection.getString("spectator-prefix");
        ghostFactory.setTeamName(spectatorPrefix);
        ghostFactory.setTeamPrefix(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(GameJoinEvent event) {
        Scoreboard scoreboard = event.getPlayer().getScoreboard();
        AbstractArena arena = event.getArena();

        if(arena.getTeams().size() <= 1) {
            return;
        }

        for (AbstractGameTeam abstractGameTeam : arena.getTeams().values()) {
            if(scoreboard.getTeam(abstractGameTeam.getName()) != null) {
                scoreboard.getTeam(abstractGameTeam.getName()).unregister();
            }

            Team team = scoreboard.registerNewTeam(abstractGameTeam.getName());
            team.setPrefix(Utils.getColorFromDye(abstractGameTeam.getColor()).toString() + "[" + abstractGameTeam.getName() + "] ");
            abstractGameTeam.getPlayers().forEach(player -> team.addEntry(player.getName()));
        }

        arena.getPlayers().forEach(player -> {
            if (player.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }


            AbstractGameTeam gameTeam = playerManager.getPlayer(event.getPlayer()).getGameTeam();
            if (gameTeam == null) {
                return;
            }

            Team team = player.getScoreboard().getTeam(gameTeam.getName());

            if (team == null) {
                return;
            }

            team.addEntry(event.getPlayer().getName());
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(ArenaLeaveEvent event) {
        AbstractArena arena = event.getArena();

        if(arena.getTeams().size() <= 1) {
            return;
        }

        if (event.getLastGameTeam() == null) {
            return;
        }

        arena.getPlayers().forEach(player -> {
            if (player.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            Team team = player.getScoreboard().getTeam(event.getLastGameTeam().getName());

            if (team == null) {
                return;
            }

            team.removeEntry(event.getPlayer().getName());
        });
    }

    @Override
    public String getConfigPath() {
        return "team-nametags";
    }
}