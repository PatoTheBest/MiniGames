package me.patothebest.thetowers.arena;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.arena.ArenaGroup;
import me.patothebest.gamecore.event.arena.GameEndEvent;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.phase.phases.EndPhase;
import me.patothebest.gamecore.phase.phases.LobbyPhase;
import me.patothebest.gamecore.phase.phases.TeamAssignPhase;
import me.patothebest.gamecore.util.PlayerList;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.Cuboid;
import me.patothebest.thetowers.TheTowersRemastered;
import me.patothebest.thetowers.player.TheTowersPlayer;
import net.megaplanet.thetowers.api.IArena;
import net.megaplanet.thetowers.api.IGameTeam;
import net.megaplanet.thetowers.api.events.ArenaEndEvent;
import net.megaplanet.thetowers.api.events.PlayerJoinLobbyEvent;
import net.megaplanet.thetowers.api.events.PlayerLeaveArenaEvent;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class Arena extends AbstractArena implements IArena {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final Map<String, ItemDropper> droppers;
    private final List<Cuboid> protectedAreas;
    private final PointList gameTeamScoredPoints = new PointList();
    private final PointList scoredPoints = new PointList(); // for the teams scoreboard
    private final PointList kills = new PointList();
    private final TheTowersRemastered plugin;
    private GameTeam winner;
    private final PlayerList winners = new PlayerList();
    private final PlayerList losers = new PlayerList();
    private long elapsedTime = 0;


    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private Arena(TheTowersRemastered plugin, Provider<Injector> injector, @Assisted("name") String name, @Assisted("worldName") String worldName) {
        super(name, worldName, injector.get());
        this.plugin = plugin;

        // initialize default objects
        droppers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        protectedAreas = new ArrayList<>();

        // scoreboard stuff
        Scoreboard bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective dummy = bukkitScoreboard.registerNewObjective("team-prefix", "dummy");
        dummy.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        arenaGroup = ArenaGroup.DEFAULT_GROUP;
    }

    @Override
    protected void parseData(Map<String, Object> map) {
        super.parseData(map);
        arenaGroup = ArenaGroup.DEFAULT_GROUP;
        // get the item spawners list
        List<Map<String, Object>> dropperList = (List<Map<String, Object>>) map.get("droppers");
        dropperList.forEach(dropper -> {
            ItemDropper itemDropper = new ItemDropper(this, dropper);
            droppers.put(itemDropper.getName(), itemDropper);
        });

        // load protected areas
        if (map.get("protected-areas") != null) {
            List<Map<String, Object>> protectedAreaMap = (List<Map<String, Object>>) map.get("protected-areas");
            protectedAreaMap.forEach(protectedArea -> {
                Cuboid cuboid = new Cuboid(protectedArea, this);
                protectedAreas.add(cuboid);
            });
        }
    }

    @Override
    public void initializePhase() {
        addPhase(LobbyPhase.class);
        addPhase(TeamAssignPhase.class);
        addPhase(TheTowersGamePhase.class);
        addPhase(CelebrationPhase.class);
        addPhase(EndPhase.class);

        for (AbstractGameTeam value : teams.values()) {
            gameTeamScoredPoints.addPlayer((GameTeam) value, value.getName());
        }
    }

    // -------------------------------------------- //
    // CORE METHODS
    // -------------------------------------------- //

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);

        if (currentPhase instanceof LobbyPhase) {
            // call the PlayerJoinLobbyEvent event
            plugin.getServer().getPluginManager().callEvent(new PlayerJoinLobbyEvent(player, this));
        }
    }

    @Override
    protected void callLeaveEvent(Player player, AbstractGameTeam team) {
        plugin.getServer().getPluginManager().callEvent(new PlayerLeaveArenaEvent(player, this, (IGameTeam) team));
    }

    @Override
    public void checkWin() {
        if(getPlayers().size() == 0) {
            setPhase(EndPhase.class);
            return;
        }

        // iterate over each team
        teams.values().forEach(gameTeam -> {
            // check if the team has reached the score limit
            if (((GameTeam)gameTeam).getPoints() != 10) {
                return;
            }

            for (Player player : getPlayers()) {
                TheTowersPlayer ttPlayer = (TheTowersPlayer) getPlayerManager().getPlayer(player);

                // If the player is on the winning team...
                if (ttPlayer.getGameTeam().equals(gameTeam)) {
                    // ...add him to the winners list
                    winners.add(player);
                } else {
                    // ..else add him to the losers list
                    losers.add(player);
                }
            }
            winner = (GameTeam) gameTeam;

            // call the ArenaEndEvent with the winners and losers
            plugin.callEvent(new GameEndEvent(this, winners, losers));
            plugin.callEvent(new ArenaEndEvent(winners.toJavaList(), losers.toJavaList(), false));

            elapsedTime = getTimePhaseHasBeenRunning();

            // finally end the arena
            nextPhase();
        });
    }

    @Override
    public void endArena() {
        endArena(true);
    }

    @Override
    public IGameTeam getGameTeamPreference(Player player) {
        return (IGameTeam) getTeamPreference(player);
    }

    @Override
    public IGameTeam getGameTeam(Player player) {
        return (IGameTeam) getTeam(player);
    }

    @Override
    public IGameTeam getGameTeam(DyeColor color) {
        return (IGameTeam) getTeam(color);
    }

    @Override
    public IGameTeam getGameTeam(String name) {
        return (IGameTeam) getTeam(name);
    }

    @Override
    public List<Player> getArenaPlayers() {
        return players.toJavaList();
    }

    @Override
    public void endArena(boolean regen) {
        super.endArena(regen);

        if(!regen) {
            return;
        }

        // reset stuff
        teams.values().forEach(gameTeam -> ((GameTeam)gameTeam).setPoints(0));
        scoredPoints.clear();
        kills.clear();
        winners.clear();
        losers.clear();

        gameTeamScoredPoints.clear();
        for (AbstractGameTeam value : teams.values()) {
            gameTeamScoredPoints.addPlayer((GameTeam) value, value.getName());
        }
    }

    // -------------------------------------------- //
    // CHECKING IF ARENA MEETS REQUIREMENTS
    // -------------------------------------------- //

    @Override
    public boolean canArenaBeEnabled(CommandSender sender) {
        // final effective field
        final boolean[] canBeEnabled = {super.canArenaBeEnabled(sender)};

        // check if there is at least two teams
        if (teams.size() < 2) {
            canBeEnabled[0] = false;
            sender.sendMessage(CoreLang.SETUP_ERROR_TEAM_MIN.getMessage(sender));
        }

        // validate each team
        teams.values().forEach(team -> {
            // check if the team has a spawn
            if (team.getSpawn() == null) {
                canBeEnabled[0] = false;
                sender.sendMessage(CoreLang.SETUP_ERROR_TEAM_SPAWN_NULL.getMessage(sender).replace("%teamcolor%", Utils.getColorFromDye(team.getColor()).toString()).replace("%teamname%", team.getName()));
            }

            // check if the team has at least one point area
            if (((GameTeam)team).getPointAreas().size() == 0) {
                canBeEnabled[0] = false;
                sender.sendMessage(CoreLang.SETUP_ERROR_TEAM_POINT_AREA_NULL.getMessage(sender).replace("%teamcolor%", Utils.getColorFromDye(team.getColor()).toString()).replace("%teamname%", team.getName()));
            }
        });

        // returns true if all the requirements have been met
        return canBeEnabled[0];
    }

    // -------------------------------------------- //
    // SAVING AND DELETING
    // -------------------------------------------- //

    @Override
    public Map<String, Object> serialize() {
        // map to store data
        Map<String, Object> objectMap = super.serialize();

        // store data
        objectMap.put("droppers", serializeDroppers());
        objectMap.put("protected-areas", serializeProtectedAreas());

        // return the map
        return objectMap;
    }

    private List<Map<String, Object>> serializeDroppers() {
        List<Map<String, Object>> dropperList = new ArrayList<>();
        droppers.forEach((key, value) -> dropperList.add(value.serialize()));
        return dropperList;
    }

    private List<Map<String, Object>> serializeProtectedAreas() {
        List<Map<String, Object>> serializedProtectedAreas = new ArrayList<>();
        protectedAreas.forEach(stringCuboidEntry -> serializedProtectedAreas.add(stringCuboidEntry.serialize()));
        return serializedProtectedAreas;
    }

    // -------------------------------------------- //
    // IMPLEMENTATIONS
    // -------------------------------------------- //

    @Override
    public GameTeam createTeam(String name, DyeColor color) {
        GameTeam gameTeam = new GameTeam(this, name, color);
        addTeam(gameTeam);
        return gameTeam;
    }

    @Override
    public GameTeam createTeam(Map<String, Object> data) {
        return new GameTeam(this, data);
    }

    @Override
    public int getMinimumRequiredPlayers() {
        return 1;
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    private List<AbstractGameTeam> getTeamSortedByPoints() {
        return teams.values().stream().sorted((o1, o2) -> Integer.compare(((GameTeam)o2).getPoints(), ((GameTeam)o1).getPoints())).collect(Collectors.toList());
    }

    public Map<String, ItemDropper> getDroppers() {
        return droppers;
    }

    public void addDropper(ItemDropper dropper) {
        droppers.put(dropper.getName(), dropper);
    }

    public TheTowersRemastered getPlugin() {
        return plugin;
    }

    public List<Cuboid> getProtectedAreas() {
        return protectedAreas;
    }

    @Override
    public String toString() {
        return "Arena{" +
                "enabled=" + enabled +
                ", minPlayers=" + minPlayers +
                ", maxPlayers=" + maxPlayers +
                ", area=" + getArea() +
                ", lobbyLocation=" + lobbyLocation +
                ", name='" + getName() + '\'' +
                ", arenaFile=" + arenaFile +
                ", teams=" + teams +
                ", droppers=" + droppers +
                ", plugin=" + plugin +
                ", world=" + world +
                ", players=" + players +
                ", teamPreferences=" + teamPreferences +
                '}';
    }

    public PointList getScoredPoints() {
        return scoredPoints;
    }

    public PointList getGameTeamScoredPoints() {
        return gameTeamScoredPoints;
    }

    public PointList getKills() {
        return kills;
    }

    public PlayerList getWinners() {
        return winners;
    }

    public PlayerList getLosers() {
        return losers;
    }

    public GameTeam getWinner() {
        return winner;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
}