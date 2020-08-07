package me.patothebest.gamecore.ghost;

import com.google.inject.Singleton;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModuleName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
@ModuleName("Ghost Manager")
public class GhostFactory implements ActivableModule {

    /**
     * Team of ghosts and people who can see ghosts.
     */
    private static final String GHOST_TEAM_NAME = "Ghosts";

    // No players in the ghost factory
    private static final Player[] EMPTY_PLAYERS = new Player[0];
    private final Map<Player, Team> ghostTeams = new HashMap<>();

    // Players that are actually ghosts
    private final Set<Player> ghosts = new HashSet<>();

    // Team nametags addon
    private boolean teamPrefix;
    private String teamName;

    @Override
    public void onDisable() {
        clearMembers();
    }

    /**
     * Remove all existing player members and ghosts.
     */
    public void clearMembers() {
        for (Player player : getGhosts()) {
            removePlayer(player);
        }
    }

    /**
     * Add the given player to this ghost manager. This ensures that it can see ghosts, and later become one.
     *
     * @param player - the player to add to the ghost manager.
     */
    public void addPlayer(Player player) {
        if(!ghosts.contains(player)) {
            Team team = createTeam(player);
            team.addEntry(player.getName());

            for (Player ghost : ghosts) {
                team.addEntry(ghost.getName());
                ghostTeams.get(ghost).addEntry(player.getName());
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
            ghosts.add(player);
        }
    }

    /**
     * Determine if the given player is tracked by this ghost manager and is a ghost.
     *
     * @param player - the player to test.
     *
     * @return TRUE if it is, FALSE otherwise.
     */
    public boolean isGhost(Player player) {
        return player != null && hasPlayer(player);
    }

    /**
     * Determine if the current player is tracked by this ghost manager, or is a ghost.
     *
     * @param player - the player to check.
     *
     * @return TRUE if it is, FALSE otherwise.
     */
    public boolean hasPlayer(Player player) {
        return ghosts.contains(player);
    }

    /**
     * Set whether or not a given player is a ghost.
     *
     * @param player  - the player to set as a ghost.
     * @param isGhost - TRUE to make the given player into a ghost, FALSE otherwise.
     */
    public void setGhost(Player player, boolean isGhost) {
        if (isGhost) {
            addPlayer(player);
        } else {
            removePlayer(player);
        }
    }

    /**
     * Remove the given player from the manager, turning it back into the living and making it unable to see ghosts.
     *
     * @param player - the player to remove from the ghost manager.
     */
    public void removePlayer(Player player) {
        if(ghosts.contains(player)) {
            Team team = ghostTeams.get(player);
            ghosts.remove(player);
            ghostTeams.remove(player);

            for (Player ghost : ghosts) {
                ghostTeams.get(ghost).removeEntry(player.getName());
            }

            team.unregister();
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    /**
     * Retrieve every ghost and every player that can see ghosts.
     *
     * @return Every ghost or every observer.
     */
    public Player[] getGhosts() {
        return toArray(ghosts);
    }

    private Player[] toArray(Set<Player> players) {
        if (players != null) {
            return players.toArray(new Player[players.size()]);
        } else {
            return EMPTY_PLAYERS;
        }
    }

    private Team createTeam(Player player) {
        Scoreboard board = player.getScoreboard();
        Team ghostTeam = board.getTeam(GHOST_TEAM_NAME);

        // Create a new ghost team if needed
        if (ghostTeam == null) {
            ghostTeam = board.registerNewTeam(GHOST_TEAM_NAME);
            if(teamPrefix) {
                ghostTeam.setPrefix(ChatColor.GRAY + "[" + teamName + "] ");
            }
        }

        // Thanks to Rprrr for noticing a bug here
        ghostTeam.setCanSeeFriendlyInvisibles(true);
        ghostTeams.put(player, ghostTeam);
        return ghostTeam;
    }

    public void setTeamPrefix(boolean teamPrefix) {
        this.teamPrefix = teamPrefix;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}