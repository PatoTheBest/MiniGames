package me.patothebest.arcade.arena;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.arcade.game.Game;
import me.patothebest.arcade.game.GameType;
import me.patothebest.arcade.game.helpers.ArcadeTeamAssignPhase;
import me.patothebest.arcade.game.helpers.EndArenaPhase;
import me.patothebest.arcade.game.helpers.EndGamePhase;
import me.patothebest.arcade.game.helpers.GamePhase;
import me.patothebest.arcade.game.helpers.IntroduceGamePhase;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaGroup;
import me.patothebest.gamecore.event.player.PlayerStateChangeEvent;
import me.patothebest.gamecore.event.player.SpectateEvent;
import me.patothebest.gamecore.phase.Phase;
import me.patothebest.gamecore.phase.phases.EndPhase;
import me.patothebest.gamecore.phase.phases.LobbyPhase;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.ArenaLocation;
import me.patothebest.gamecore.vector.Cuboid;
import org.apache.commons.lang.Validate;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Arena extends AbstractArena {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private ArenaType arenaType = ArenaType.SOLO;
    private final List<Player> inGameSpectators = new CopyOnWriteArrayList<>();
    private final Map<GameType, Game> games = new HashMap<>();
    private final LinkedList<Game> playingGames = new LinkedList<>();
    private final PointList starCount = new PointList();
    private final PointList gameStarCount = new PointList();
    private int maxPointsPerGame = 0;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private Arena(@Assisted("name") String name, @Assisted("worldName") String worldName, Provider<Injector> injector) {
        super(name, worldName, injector.get());
        needsSpectatorLocation = false;
        needsArenaArea = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void parseData(Map<String, Object> map) {
        super.parseData(map);
        arenaGroup = ArenaGroup.DEFAULT_GROUP;

        this.arenaType = Utils.getEnumValueFromString(ArenaType.class, (String) map.get("arenatype"));

        if(map.containsKey("games")) {
            List<Map<String, Object>> games = (List<Map<String, Object>>) map.get("games");
            games.forEach(gameMap -> {
                GameType gameType = GameType.getGameType((String) gameMap.get("type"));

                if(gameType == null) {
                    Utils.printError("Unknown game type " + gameMap.get("type"));
                    return;
                }

                Game game = createGame(gameType);
                game.parse(gameMap);
            });
        }
    }

    @Override
    public void initializePhase() {
        if(arenaType == null) {
            return;
        }

        currentPhase = firstPhase = null;
        LobbyPhase lobbyPhase = addPhase(LobbyPhase.class);
        addPhase(ArcadeTeamAssignPhase.class);
        playingGames.clear();

        for (Game game : games.values()) {
            game.setNextPhase(null);
            if (!game.isEnabled()) {
                continue;
            }

            IntroduceGamePhase phase = createPhase(IntroduceGamePhase.class);
            phase.setGame(game);
            addPhase(phase);
            game.configure();
            addPhase(game);

            EndGamePhase endGame = createPhase(EndGamePhase.class);
            endGame.setGame(game);
            addPhase(endGame);

            playingGames.add(game);
        }

        Phase previousPhase = firstPhase;

        while (previousPhase.getNextPhase().getNextPhase() != null) {
            previousPhase = previousPhase.getNextPhase();
        }
        previousPhase.setNextPhase(null);
        EndArenaPhase endPhase = createPhase(EndArenaPhase.class);
        previousPhase.setNextPhase(endPhase);
        addPhase(EndPhase.class);
        teams.clear();
        teamPreferences.clear();
        createTeam("Players", DyeColor.YELLOW).setTeamChatPrefix(false);
    }

    public void revivePlayers() {
        for (Player player : inGameSpectators) {
            IPlayer corePlayer = getPlayerManager().getPlayer(player);

            // clear inventory
            corePlayer.getPlayerInventory().clearPlayer();

            players.add(player);
            spectators.remove(player);
            inGameSpectators.remove(player);
            plugin.getServer().getPluginManager().callEvent(new PlayerStateChangeEvent(player, this, PlayerStateChangeEvent.PlayerState.PLAYER));
            ghostFactory.removePlayer(player);
        }
    }

    @Override
    public void checkWin() {

    }

    @Override
    public void endArena(boolean regen) {
        super.endArena(regen);
        starCount.clear();
        gameStarCount.clear();
        for (Game playingGame : playingGames) {
            playingGame.getGoal().reset();
        }
    }

    // -------------------------------------------- //
    // SAVING
    // -------------------------------------------- //

    @Override
    public boolean canArenaBeEnabled(CommandSender commandSender) {
        boolean canBeEnabled = super.canArenaBeEnabled(commandSender);
        boolean atLeastOneGame = false;

        for (Game game : games.values()) {
            if (game.isEnabled()) {
                atLeastOneGame = true;
                canBeEnabled &= game.canArenaBeEnabled(commandSender);
            }
        }

        return canBeEnabled & atLeastOneGame;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> dataMap = super.serialize();

        dataMap.put("arenatype", arenaType.name());
        dataMap.put("games", serializeGames());

        return dataMap;
    }

    @Override
    protected List<Map<String, Object>> serializeTeams() {
        return arenaType == ArenaType.SOLO ? null : super.serializeTeams();
    }

    private List<Map<String, Object>> serializeGames() {
        List<Map<String, Object>> serializedGames = new ArrayList<>();
        games.values().forEach(game -> serializedGames.add(game.serialize()));
        return serializedGames;
    }

    // -------------------------------------------- //
    // TEAMS
    // -------------------------------------------- //

    @Override
    public GameTeam createTeam(String name, DyeColor color) {
        Validate.isTrue(!teams.containsKey(name), "Team already exists!");
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
        return 2;
    }

    @Override
    public void enableArena() {
        super.enableArena();
        if (currentPhase != null) {
            currentPhase.stop();
        }
        initializePhase();
        currentPhase = firstPhase;
        currentPhase.start();
    }

    @Override
    public void removePlayer(Player player, boolean offline) {
        inGameSpectators.remove(player);
        super.removePlayer(player, offline);
    }

    @Override
    public void changeToSpectator(Player player, boolean shouldTeleport) {
        // NoBorderTrespassingFeature calls this method again, so we check if it isn't called twice
        if(spectators.contains(player)) {
            if(shouldTeleport) {
                // teleport the player and give items
                player.teleport(getSpectatorLocation());
            }

            return;
        }

        IPlayer corePlayer = getPlayerManager().getPlayer(player);

        // clear inventory
        corePlayer.getPlayerInventory().clearPlayer();

        // set fly mode
        player.setAllowFlight(true);
        player.setFlying(true);

        // remove the player from the arena
        players.remove(player);
        spectators.add(player);
        inGameSpectators.add(player);

        if(shouldTeleport) {
            // teleport the player and give items
            player.teleport(getSpectatorLocation());
        }

        ghostFactory.addPlayer(player);
        plugin.getServer().getPluginManager().callEvent(new SpectateEvent(player, this));
        plugin.getServer().getPluginManager().callEvent(new PlayerStateChangeEvent(player, this, PlayerStateChangeEvent.PlayerState.SPECTATOR));
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //


    @Override
    public Cuboid getArea() {
        if (currentPhase instanceof GamePhase) {
            return ((GamePhase) currentPhase).getGame().getArea();
        }
        return super.getArea();
    }

    @Override
    public ArenaLocation getSpectatorLocation() {
        if (currentPhase instanceof GamePhase) {
            return ((GamePhase) currentPhase).getGame().getSpectatorLocation();
        }
        return getLobbyLocation();
    }

    public int getGameIndex() {
        if (currentPhase instanceof GamePhase) {
            Game currentGame = ((GamePhase) currentPhase).getGame();
            for (int i = 0; i < playingGames.size(); i++) {
                if (playingGames.get(i) == currentGame) {
                    return i + 1;
                }
            }

            return -1;
        }

        switch (getArenaState()) {
            case WAITING:
            case STARTING:
                return 1;
            case ENDING:
                return playingGames.size();
            case RESTARTING:
            case IN_GAME:
            case OTHER:
            case ERROR:
                return -1;
        }

        return -1;
    }

    public int getTotalGames() {
        return playingGames.size();
    }

    public Game getCurrentGame() {
        if (currentPhase instanceof GamePhase) {
            return ((GamePhase) currentPhase).getGame();
        }
        return null;
    }

    public ArenaType getArenaType() {
        return arenaType;
    }

    public void setArenaType(ArenaType arenaType) {
        this.arenaType = arenaType;
    }

    @Override
    public int getMaxPlayers() {
        return super.getMaxPlayers();
    }

    public Map<GameType, Game> getGames() {
        return games;
    }

    public Game createGame(GameType gameType) {
        Game game = injector.getInstance(gameType.getGameClass());
        game.setArena(this);
        games.put(gameType, game);
        return game;
    }

    public PointList getStarCount() {
        return starCount;
    }

    public PointList getGameStarCount() {
        return gameStarCount;
    }

    public int getTotalPlayers() {
        return players.size() + inGameSpectators.size();
    }

    public int getMaxPointsPerGame() {
        return maxPointsPerGame;
    }

    public void setMaxPointsPerGame(int maxPointsPerGame) {
        this.maxPointsPerGame = maxPointsPerGame;
    }
}
