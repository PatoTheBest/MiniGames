package me.patothebest.gamecore.arena;

import com.google.inject.Injector;
import com.google.inject.Provider;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.actionbar.ActionBar;
import me.patothebest.gamecore.arena.modes.bungee.AdvancedBungeeMode;
import me.patothebest.gamecore.arena.option.ArenaOption;
import me.patothebest.gamecore.arena.option.options.EnvironmentOption;
import me.patothebest.gamecore.arena.option.options.TNTExplosionOption;
import me.patothebest.gamecore.arena.option.options.TimeOfDayOption;
import me.patothebest.gamecore.combat.CombatManager;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.event.arena.ArenaDisableEvent;
import me.patothebest.gamecore.event.arena.ArenaPhaseChangeEvent;
import me.patothebest.gamecore.event.arena.ArenaPrePhaseChangeEvent;
import me.patothebest.gamecore.event.arena.ArenaPreRegenEvent;
import me.patothebest.gamecore.event.arena.ArenaUnLoadEvent;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.event.player.ArenaLeaveMidGameEvent;
import me.patothebest.gamecore.event.player.ArenaPreJoinEvent;
import me.patothebest.gamecore.event.player.ArenaPreLeaveEvent;
import me.patothebest.gamecore.event.player.GameJoinEvent;
import me.patothebest.gamecore.event.player.PlayerLooseEvent;
import me.patothebest.gamecore.event.player.PlayerStateChangeEvent;
import me.patothebest.gamecore.event.player.SpectateEvent;
import me.patothebest.gamecore.feature.Feature;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.ghost.GhostFactory;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.GroupPermissible;
import me.patothebest.gamecore.permission.PermissionGroup;
import me.patothebest.gamecore.permission.PermissionGroupManager;
import me.patothebest.gamecore.phase.Phase;
import me.patothebest.gamecore.phase.phases.EndPhase;
import me.patothebest.gamecore.phase.phases.GamePhase;
import me.patothebest.gamecore.phase.phases.LobbyPhase;
import me.patothebest.gamecore.phase.phases.NullPhase;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.sign.SignManager;
import me.patothebest.gamecore.stats.StatsManager;
import me.patothebest.gamecore.title.TitleBuilder;
import me.patothebest.gamecore.util.DoubleCallback;
import me.patothebest.gamecore.util.MessageCallback;
import me.patothebest.gamecore.util.NameableObject;
import me.patothebest.gamecore.util.PlayerList;
import me.patothebest.gamecore.util.ServerVersion;
import me.patothebest.gamecore.util.Sounds;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.ArenaLocation;
import me.patothebest.gamecore.vector.Cuboid;
import me.patothebest.gamecore.world.ArenaWorld;
import me.patothebest.gamecore.world.WorldHandler;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

public abstract class AbstractArena implements GroupPermissible, ConfigurationSerializable, NameableObject {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    protected final Map<AbstractGameTeam, List<Player>> teamPreferences;
    // players will rejoin the team they were in if they left the arena mid-game
    protected final Map<AbstractGameTeam, List<String>> lastTeams = new HashMap<>();
    protected final List<ArenaOption> arenaOptions = new ArrayList<>();
    protected final ArenaManager arenaManager;
    protected final PlayerList players;
    protected final PlayerList spectators;
    protected final Map<String, Integer> topKillers;
    protected final ArenaFile arenaFile;
    protected final Injector injector;
    protected final Provider<WorldHandler> worldHandler;
    protected final CoreConfig config;
    protected final CorePlugin plugin;
    protected final PluginScheduler pluginScheduler;
    protected final GhostFactory ghostFactory;
    protected final EventRegistry eventRegistry;
    protected final StatsManager statsManager;
    protected final CombatManager combatManager;
    private final SignManager signManager;
    private final PlayerManager playerManager;
    private final KitManager kitManager;
    private final PermissionGroupManager permissionGroupManager;
    private final String name;

    protected ArenaWorld world;
    protected ArenaGroup arenaGroup;
    protected int minPlayers;
    protected int maxPlayers;
    protected boolean needsLobbyLocation = true;
    protected boolean needsSpectatorLocation = true;
    protected boolean needsArenaArea = true;
    protected boolean publicJoinable = true;
    protected boolean publicSpectable = true;
    protected boolean privateArena = false;
    protected boolean whitelist = false;
    protected boolean teamSelector = true;
    protected boolean disableSaving = false;
    protected boolean disableStats = false;
    protected final List<String> whitelistedPlayers = new ArrayList<>();
    protected final String worldName;
    protected String displayName;
    protected Vector offset = new Vector(0, 0, 0);
    protected ArenaLocation lobbyLocation;
    protected Cuboid lobbyArea;
    protected ArenaLocation spectatorLocation;
    protected PermissionGroup permissionGroup;
    protected final Map<String, AbstractGameTeam> teams;

    protected Phase firstPhase;
    protected Phase currentPhase;
    // things that are going to be saved
    protected volatile boolean enabled;
    protected Cuboid area;

    private long phaseTime;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public AbstractArena(String name, String worldName, Injector injector) {
        this.name = name;
        this.worldName = worldName;
        this.arenaManager = injector.getInstance(ArenaManager.class);
        this.injector = injector;
        this.plugin = injector.getInstance(CorePlugin.class);
        this.pluginScheduler = injector.getInstance(PluginScheduler.class);
        this.playerManager = injector.getInstance(PlayerManager.class);
        this.signManager = injector.getInstance(SignManager.class);
        this.config = injector.getInstance(CoreConfig.class);
        this.permissionGroupManager = injector.getInstance(PermissionGroupManager.class);
        this.kitManager = injector.getInstance(KitManager.class);
        this.ghostFactory = injector.getInstance(GhostFactory.class);
        this.eventRegistry = injector.getInstance(EventRegistry.class);
        this.statsManager = injector.getInstance(StatsManager.class);
        this.worldHandler = injector.getProvider(WorldHandler.class);
        this.combatManager = injector.getInstance(CombatManager.class);

        world = new ArenaWorld(this);
        players = new PlayerList();
        spectators = new PlayerList();
        arenaFile = new ArenaFile(this);
        teams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        teamPreferences = new HashMap<>();
        topKillers = new HashMap<>();

        minPlayers = 2;
        maxPlayers = 24;

        arenaOptions.add(new TimeOfDayOption());
        arenaOptions.add(new TNTExplosionOption());
        arenaOptions.add(new EnvironmentOption());

        for (ArenaOption arenaOption : arenaOptions) {
            arenaOption.setArena(this);
        }
    }

    public void initializeData() {
        // checks whether or not the arena has data saved on the file
        if (arenaFile.get("data") == null) {
            // if not, create the world
            world.loadWorld(true);
            permissionGroup = permissionGroupManager.getDefaultPermissionGroup();
        } else {
            // gets the configuration section as a map
            Map<String, Object> map = arenaFile.getConfigurationSection("data").getValues(true);
            parseData(map);

            // check if the arena is valid
            if (enabled && !canArenaBeEnabled(Bukkit.getConsoleSender())) {
                // if is invalid and the arena is enabled, disable the arena
                disableArena();
            }
        }

        initializePhase();

        if(enabled) {
            currentPhase.start();
        } else {
            currentPhase = createPhase(NullPhase.class);
        }
    }

    @SuppressWarnings("unchecked")
    protected void parseData(Map<String, Object> map) {
        enabled = (boolean) map.get("enabled");
        minPlayers = (int) map.get("minplayers");
        maxPlayers = (int) map.get("maxplayers");
        if (map.containsKey("arenagroup")) {
            arenaGroup = arenaManager.getGroup((String) map.get("arenagroup"));
        }

        for (ArenaOption arenaOption : arenaOptions) {
            arenaOption.parse(map);
        }

        if (enabled) {
            // unzip, load, and prepare the world
            if(!world.decompressWorld()) {
                arenaManager.getArenas().remove(name);
                throw new RuntimeException("Could not unzip world for arena " + name + "!");
            }

            if (!map.containsKey("server-version") || !map.get("server-version").equals(ServerVersion.getVersion())) {
                arenaManager.getLogger().log(Level.INFO, "Detected arena was saved with an older server version.");
                arenaManager.getLogger().log(Level.INFO, "Starting arena upgrade...");
                ArenaWorld conversion = new ArenaWorld(this, worldName + "-temp");
                conversion.loadWorld(true);
                conversion.unloadWorld(true);
                conversion.getWorldZipFile().delete();
                Utils.zipIt(conversion.getTempWorld(), conversion.getWorldZipFile());
                arenaManager.getLogger().log(Level.INFO, "Done upgrading");
            }

            world.loadWorld(false);
        } else {
            // load the world
            world.loadWorld(true);
        }

        // get the lobby location if present (legacy)
        if (map.get("lobbylocation") != null) {
            lobbyLocation = ArenaLocation.deserialize(((MemorySection) map.get("lobbylocation")).getValues(true), this);
            spectatorLocation = ArenaLocation.deserialize(((MemorySection) map.get("lobbylocation")).getValues(true), this);
        }

        // get the lobby location if present
        if (map.get("lobby-location") != null) {
            lobbyLocation = ArenaLocation.deserialize(((MemorySection) map.get("lobby-location")).getValues(true), this);
        }

        // get the spectator location if present
        if (map.get("spectator-location") != null) {
            spectatorLocation = ArenaLocation.deserialize(((MemorySection) map.get("spectator-location")).getValues(true), this);
        }

        // get the arena area if present
        if (map.get("area") != null) {
            area = new Cuboid(((MemorySection) map.get("area")).getValues(true), this);
        }

        // get the arena area if present
        if (map.get("lobby-area") != null) {
            lobbyArea = new Cuboid(((MemorySection) map.get("lobby-area")).getValues(true), this);
        }

        // get the arena's permission group
        if (map.get("permission-group") != null) {
            permissionGroup = permissionGroupManager.getOrCreatePermissionGroup((String) map.get("permission-group"));
        } else {
            permissionGroup = permissionGroupManager.getDefaultPermissionGroup();
        }

        // get the team list
        if(map.get("teams") != null) {
            List<Map<String, Object>> teamList = (List<Map<String, Object>>) map.get("teams");
            teamList.forEach(team -> {
                AbstractGameTeam gameTeam = createTeam(team);
                teams.put(gameTeam.getName(), gameTeam);
            });
        }
    }

    public abstract void initializePhase();

    // -------------------------------------------- //
    // PHASE
    // -------------------------------------------- //

    public void startGame() {
        nextPhase();
    }

    public void nextPhase() {
        ArenaPrePhaseChangeEvent arenaPrePhaseChange = eventRegistry.callEvent(new ArenaPrePhaseChangeEvent(this, currentPhase, currentPhase.getNextPhase()));

        if(arenaPrePhaseChange.isCancelled()) {
            return;
        }

        if(!currentPhase.getNextPhase().isPreviousPhaseFeatures()) {
            currentPhase.stop();
        }

        Phase oldPhase = currentPhase;

        currentPhase = currentPhase.getNextPhase();
        eventRegistry.callEvent(new ArenaPhaseChangeEvent(this, oldPhase, currentPhase));
        currentPhase.start();
        phaseTime = System.currentTimeMillis();
    }

    public void setPhase(Class<? extends Phase> phaseClass) {
        Phase phase1 = firstPhase;

        while (phase1.getClass() != phaseClass) {
            phase1 = phase1.getNextPhase();
        }

        ArenaPrePhaseChangeEvent arenaPrePhaseChange = eventRegistry.callEvent(new ArenaPrePhaseChangeEvent(this, currentPhase, phase1));

        if(arenaPrePhaseChange.isCancelled()) {
            return;
        }

        Phase oldPhase = currentPhase;

        currentPhase.stop();
        currentPhase = phase1;
        phaseTime = System.currentTimeMillis();
        currentPhase.start();

        eventRegistry.callEvent(new ArenaPhaseChangeEvent(this, oldPhase, currentPhase));
    }

    @SuppressWarnings("unchecked")
    protected <T extends Phase> T createPhase(Class<T> phaseClass) {
        T phase = injector.getInstance(phaseClass);
        phase.setArena(this);
        phase.configure();
        return phase;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Phase> T addPhase(Class<T> phaseClass) {
        T phase = injector.getInstance(phaseClass);
        phase.setArena(this);
        phase.configure();
        return addPhase(phase);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Phase> T addPhase(T phase) {
        if(firstPhase == null) {
            firstPhase = phase;
            currentPhase = phase;
        } else {
            Phase previousPhase = firstPhase;

            while (previousPhase.getNextPhase() != null) {
                previousPhase = previousPhase.getNextPhase();
            }

            previousPhase.setNextPhase(phase);
            phase.setPreviousPhase(previousPhase);

            if(phase.isPreviousPhaseFeatures()) {
                phase.getFeatures().putAll(previousPhase.getFeatures());
            }
        }

        return phase;
    }

    public Phase getPreviousPhase(Phase phase) {
        Phase phase1 = firstPhase;

        while (phase1.getNextPhase() != null && phase1.getNextPhase() != phase) {
            phase1 = phase1.getNextPhase();
        }

        return phase1;
    }

    public Phase getNextPhase() {
        return currentPhase.getNextPhase();
    }

    // -------------------------------------------- //
    // FEATURE
    // -------------------------------------------- //

    public <T extends Feature> T createFeature(Class<T> featureClass) {
        T feature = injector.getInstance(featureClass);
        feature.setArena(this);
        return feature;
    }

    @SuppressWarnings("unchecked")
    public <T extends Feature> T getFeature(Class<T> featureClass) {
        return (T) currentPhase.getFeatures().get(featureClass);
    }

    // -------------------------------------------- //
    // GAME METHODS
    // -------------------------------------------- //

    public abstract void checkWin();

    public void endArena(boolean regen) {
        // check if the arena is enabled
        if (!enabled) {
            // this method shouldn't have been called
            return;
        }

        ArenaPreRegenEvent arenaPreRegenEvent = plugin.callEvent(new ArenaPreRegenEvent(this));

        if(arenaPreRegenEvent.isCancelled()) {
            return;
        }

        // remove all the players
        spectators.forEach(this::removePlayer);
        players.forEach(this::removePlayer);
        lastTeams.clear();
        topKillers.clear();

        if (currentPhase != null) {
            currentPhase.stop();
        }

        updateSigns();

        // if regen the arena...
        if (regen) {
            regenArena();
        } else {
            loadFirstPhase();
        }
    }

    private void loadFirstPhase() {
        currentPhase = firstPhase;

        if(plugin.isEnabled() && currentPhase != null) {
            currentPhase.start();
        }

        updateSigns();
    }

    public void removePlayer(Player player) {
        removePlayer(player, false);
    }

    public void removePlayer(Player player, boolean offline) {
        ArenaPreLeaveEvent arenaPreLeaveEvent = new ArenaPreLeaveEvent(player, this);
        if(!offline) {
            plugin.getServer().getPluginManager().callEvent(arenaPreLeaveEvent);

            if(arenaPreLeaveEvent.isCancelled()) {
                return;
            }
        }

        IPlayer corePlayer = playerManager.getPlayer(player.getName());

        // if the player is dead...
        if (player.isDead()) {
            try {
                // ...try to respawn the player
                player.spigot().respawn();
            } catch (Throwable t) {
                // can't respawn the player (bukkit) so we kick him
                player.kickPlayer("Regenerating arena");
            }
        }

        // gets the team the player is in
        AbstractGameTeam gameTeam = getTeam(player);

        if (gameTeam != null) {
            gameTeam.removePlayer(player);
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

            if(getArenaState() == ArenaState.IN_GAME && players.contains(player)) {
                combatManager.killPlayer(player, false);

                if(!currentPhase.canJoin()) { // check if player is not spectating
                    eventRegistry.callEvent(new PlayerLooseEvent(player, this));
                    topKillers.put(player.getName(), corePlayer.getGameKills());
                    plugin.getServer().getPluginManager().callEvent(new ArenaLeaveMidGameEvent(player, this, gameTeam));
                }
            }
        }

        // If the player has a team preference...
        AbstractGameTeam preferenceTeam = getTeamPreference(player);
        if (preferenceTeam != null) {
            // ...remove the player from the preference list
            teamPreferences.get(preferenceTeam).remove(player);
        }

        // reset arena related stuff for the player
        corePlayer.getPlayerInventory().restoreInventory();
        corePlayer.setCurrentArena(null);
        corePlayer.setGameTeam(null);

        // teleport the player to the main lobby
        if (!arenaPreLeaveEvent.isCancelTeleport()) {
            teleportToLobby(player);
        }

        // remove the player from the arena
        players.remove(player);

        if(spectators.contains(player)) {
            spectators.remove(player);
            ghostFactory.removePlayer(player);
        }

        // If there are no players left on the arena, we end it
        if (getArenaState() == ArenaState.IN_GAME) {
            // if the player quit the game...
            if (offline && plugin.isEnabled()) { // isEnable check for players being kicked when server is shutting down
                // ...we schedule a task to check if the arena needs ending so that the player entity
                // is fully removed from the game
               pluginScheduler.runTask(this::checkWin);
            } else {
                // ...we check if it needs ending
                checkWin();
            }
        }

        // update the signs to display the new amount of players
        updateSigns();

        // call player leave on the current phase
        currentPhase.playerLeave(player);

        // call the PlayerLeaveArenaEvent event
        plugin.getServer().getPluginManager().callEvent(new ArenaLeaveEvent(player, this, gameTeam));
        plugin.getServer().getPluginManager().callEvent(new PlayerStateChangeEvent(player, this, PlayerStateChangeEvent.PlayerState.NONE));
        callLeaveEvent(player, gameTeam);
    }

    public void changeToSpectator(Player player, boolean shouldTeleport) {
        // NoBorderTrespassingFeature calls this method again, so we check if it isn't called twice
        if(spectators.contains(player)) {
            if(shouldTeleport) {
                // teleport the player and give items
                player.teleport(getSpectatorLocation());
            }

            return;
        }

        IPlayer corePlayer = playerManager.getPlayer(player);

        // clear inventory
        corePlayer.getPlayerInventory().clearPlayer();

        // set fly mode
        pluginScheduler.runTaskLater(() -> {
            player.setAllowFlight(true);
            player.setFlying(true);
        }, 1L);

        // gets the team the player is in
        AbstractGameTeam gameTeam = getTeam(player);

        if (gameTeam != null) {
            gameTeam.removePlayer(player);

            if(!currentPhase.canJoin() && getArenaState() == ArenaState.IN_GAME) {
                eventRegistry.callEvent(new PlayerLooseEvent(player, this));
                topKillers.put(player.getName(), corePlayer.getGameKills());
            }
        }

        // remove the player from the arena
        players.remove(player);
        spectators.add(player);

        if(shouldTeleport) {
            // teleport the player and give items
            player.teleport(currentPhase instanceof LobbyPhase ? lobbyLocation : spectatorLocation);
        }

        ghostFactory.addPlayer(player);
        plugin.getServer().getPluginManager().callEvent(new SpectateEvent(player, this));
        plugin.getServer().getPluginManager().callEvent(new PlayerStateChangeEvent(player, this, PlayerStateChangeEvent.PlayerState.SPECTATOR));

        // update the signs to display the new amount of players
        updateSigns();

        // call player leave on the current phase
        currentPhase.playerLeave(player);
    }

    public void changeToPlayer(Player player) {
        IPlayer corePlayer = playerManager.getPlayer(player);

        // clear inventory
        corePlayer.getPlayerInventory().clearPlayer();

        // remove the player from the arena
        spectators.remove(player);
        players.add(player);

        currentPhase.playerJoin(player);
        ghostFactory.removePlayer(player);

        plugin.getServer().getPluginManager().callEvent(new PlayerStateChangeEvent(player, this, PlayerStateChangeEvent.PlayerState.PLAYER));

        // update the signs to display the new amount of players
        updateSigns();
    }

    protected void teleportToLobby(Player player) {
        if (config.isUseMainLobby() && config.getMainLobby() != null) {
            player.teleport(config.getMainLobby());
            return;
        }

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
    }

    public void addPlayer(Player player) {
        Validate.isTrue(!playerManager.getPlayer(player).isInArena(), "Player is already in an arena.");
        ArenaPreJoinEvent arenaPreJoinEvent = eventRegistry.callEvent(new ArenaPreJoinEvent(player, this));
        if (!arenaPreJoinEvent.isCancelled()) {
            currentPhase.playerJoin(player);
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player);
        IPlayer iPlayer = playerManager.getPlayer(player);

        // sets the arena of the player and saves the player state
        iPlayer.setCurrentArena(this);

        Location teleportLocation = currentPhase instanceof LobbyPhase ? lobbyLocation : spectatorLocation;

        if(iPlayer.isFullyJoined()) {
            // teleport the player and give items
            player.teleport(teleportLocation);
        } else {
            iPlayer.setJoinLocation(teleportLocation);
        }

        iPlayer.executeWhenFullyLoaded(player1 -> {
            iPlayer.getPlayerInventory().savePlayer();

            // call events
            plugin.getServer().getPluginManager().callEvent(new GameJoinEvent(player, this));
            plugin.getServer().getPluginManager().callEvent(new SpectateEvent(player, this));

            ghostFactory.addPlayer(player);

            pluginScheduler.runTaskLater(() -> {
                // set fly mode
                player.setAllowFlight(true);
                player.setFlying(true);
            }, 3L);
        });

        // update signs
        signManager.updateSigns();
    }

    public boolean isInGame() {
        return currentPhase instanceof GamePhase;
    }

    /**
     * Special method for TheTowersRemasteredAPI
     * This allows the arena implementation to call the leave event
     * from the api
     *
     * @param player the player
     * @param team the team
     */
    protected void callLeaveEvent(Player player, AbstractGameTeam team) { }

    // -------------------------------------------- //
    // SETUP METHODS
    // -------------------------------------------- //

    public void enableArena() {
        // teleport the players on the world to the lobby
        getWorld().getPlayers().forEach(this::teleportToLobby);

        // save and unload the world
        getWorld().save();
        Bukkit.getServer().unloadWorld(getWorld(), true);

        pluginScheduler.runTaskLaterAsynchronously(() -> {
            // zip the world and delete the world folder
            Utils.zipIt(world.getTempWorld(), world.getWorldZipFile());
            Utils.deleteFolder(world.getTempWorld());

            // unzip and load the world
            unzipAndLoad(true, () -> {
                currentPhase.stop();
                currentPhase = firstPhase;
                currentPhase.start();
                updateSigns();
            });
        }, 20L);

        enabled = true;
    }

    public void disableArena() {
        // teleport the players on the world to the lobby
        getWorld().getPlayers().forEach(this::teleportToLobby);

        // end the game if there is one without regenerating
        endArena(false);

        // unload the world
        world.unloadWorld(false);

        pluginScheduler.runTaskLaterAsynchronously(() -> {
            // delete thr world
            world.deleteWorld();

            // unzip the world and load a fresh one
            unzipAndLoad(false, () -> {
                // delete the world zip
                world.getWorldZipFile().delete();
                eventRegistry.callEvent(new ArenaDisableEvent(this));
            });
        }, 20L);

        enabled = false;

        if (currentPhase != null) {
            currentPhase.stop();
        }
        currentPhase = createPhase(NullPhase.class);

        updateSigns();
    }

    // -------------------------------------------- //
    // ARENA WORLD STUFF
    // -------------------------------------------- //

    private boolean decompress() {
        return world.decompressWorld();
    }

    private void regenArena() {
        // If the plugin is disabled...
        if (!plugin.isEnabled()) {
            // ...only delete the world
            world.unloadWorld(false);
            world.deleteWorld();
        } else {
            // schedule a task to delete and load the world
            world.unloadWorld(false);
            pluginScheduler.runTaskLaterAsynchronously(() -> {
                world.deleteWorld();
                unzipAndLoad(true, this::loadFirstPhase);
            }, 20L);
            /*for (Chunk chunk : area.getChunks()) {
                chunk.unload(false);
            }
            loadFirstPhase();*/
        }
    }

    private void unzipAndLoad(boolean tempArena, Runnable onLoad) {
        // do not regen if the plugin is disabled
        if (!plugin.isEnabled()) {
            return;
        }

        // if plugin failed to unzip...
        boolean unzip;
        if (tempArena) {
            unzip = world.decompressWorld();
        } else {
            unzip = Utils.unZip(world.getWorldZipFile().getPath(), world.getTempWorld().getPath());
        }

        if (!unzip) {
            System.err.println("Could not unzip world folder!");
            // ...set the arena state to error and update signs
            updateSigns();
            return;
        }

        Runnable runnable = () -> {
            // prepare the world
            world.loadWorld(!tempArena);
            pluginScheduler.runTask(() -> {
                if (tempArena) {
                    // update state and signs
                    updateSigns();
                }

                onLoad.run();
            });
        };

        if (worldHandler.get().hasAsyncSupport() && tempArena) {
            pluginScheduler.runTaskAsynchronously(runnable);
        } else {
            pluginScheduler.runTask(runnable);
        }
    }

    public void delete() {
        arenaFile.delete();
        destroy();
    }

    public void save() {
        if (!disableSaving) {
            arenaFile.save();
        }
    }

    public void saveAndDestroy() {
        save();

        destroy();
    }

    public void destroy() {
        players.forEach(this::removePlayer);
        spectators.forEach(this::removePlayer);

        if (enabled && !(currentPhase instanceof EndPhase)) {
            world.unloadWorld(false);
            world.deleteWorld();
        } else if(!enabled) {
            world.unloadWorld(true);
        }

        // unregisters all arena listeners
        currentPhase.stop();
        currentPhase = firstPhase;
        while (currentPhase != null) {
            currentPhase.destroy();
            currentPhase = currentPhase.getNextPhase();
        }
        currentPhase = null;
        arenaManager.getArenas().remove(getWorldName());
        eventRegistry.callEvent(new ArenaUnLoadEvent(this));
    }

    @Override
    public Map<String, Object> serialize() {
        // map to store data
        Map<String, Object> objectMap = new HashMap<>();

        // serialize everything
        objectMap.put("enabled", enabled);

        if (lobbyLocation != null) {
            objectMap.put("lobby-location", lobbyLocation.serialize());
        }

        if (spectatorLocation != null) {
            objectMap.put("spectator-location", spectatorLocation.serialize());
        }

        if (area != null) {
            objectMap.put("area", getArea().serialize());
        }

        if (lobbyArea != null) {
            objectMap.put("lobby-area", lobbyArea.serialize());
        }

        List<Map<String, Object>> teams = serializeTeams();
        if(teams != null) {
            objectMap.put("teams", teams);
        }

        objectMap.put("permission-group", permissionGroup.getName());
        objectMap.put("minplayers", minPlayers);
        objectMap.put("maxplayers", getMaxPlayers());

        objectMap.put("server-version", ServerVersion.getVersion());

        for (ArenaOption arenaOption : arenaOptions) {
            arenaOption.serialize(objectMap);
        }

        if (arenaGroup != null) {
            objectMap.put("arenagroup", arenaGroup.getName());
        }

        return objectMap;
    }

    protected List<Map<String, Object>> serializeTeams() {
        List<Map<String, Object>> teamList = new ArrayList<>();
        teams.forEach((key, value) -> teamList.add(value.serialize()));
        return teamList;
    }

    public boolean canArenaBeEnabled(CommandSender sender) {
        boolean canBeEnabled = true;

        // check if the arena area is set
        if (area == null && needsArenaArea) {
            canBeEnabled = false;
            sender.sendMessage(CoreLang.SETUP_ERROR_SET_AREA.getMessage(sender));
        }

        // check if the lobby location is set
        if (lobbyLocation == null && needsLobbyLocation) {
            canBeEnabled = false;
            sender.sendMessage(CoreLang.SETUP_ERROR_LOBBY_LOCATION.getMessage(sender));
        }

        // check if the spectator location is set
        if (spectatorLocation == null && needsSpectatorLocation) {
            canBeEnabled = false;
            sender.sendMessage(CoreLang.SETUP_ERROR_SPECTATOR_LOCATION.getMessage(sender));
        }

        // check if the minimum amount of players is tw
        if (minPlayers < 2) {
            canBeEnabled = false;
            sender.sendMessage(CoreLang.SETUP_ERROR_MIN_PLAYERS.getMessage(sender));
        }

        // check if the minimum amount of players does not surpass the maximum amount
        if (getMaxPlayers() < minPlayers) {
            canBeEnabled = false;
            sender.sendMessage(CoreLang.SETUP_ERROR_MIN_MAX.getMessage(sender));
        }

        return canBeEnabled;
    }

    // -------------------------------------------- //
    // MESSAGES
    // -------------------------------------------- //

    public void sendMessageToArena(MessageCallback<Player> locale) {
        players.forEach(player -> {
            playerManager.getPlayer(player).executeWhenFullyLoaded(player1 -> player.sendMessage(locale.call(player)));
        });

        spectators.forEach(spectator -> {
            playerManager.getPlayer(spectator).executeWhenFullyLoaded(spectator1 -> spectator.sendMessage(locale.call(spectator)));
        });
    }

    public void sendActionBarToArena(MessageCallback<Player> locale) {
        players.forEach(player -> ActionBar.sendActionBar(player, locale.call(player)));
        spectators.forEach(player -> ActionBar.sendActionBar(player, locale.call(player)));
    }

    public void sendTitleToArena(DoubleCallback<Player, TitleBuilder> locale) {
        players.forEach(player -> {
            TitleBuilder titleBuilder = TitleBuilder.newTitle();
            locale.call(player, titleBuilder);
            titleBuilder.build().send(player);
        });
        spectators.forEach(player -> {
            TitleBuilder titleBuilder = TitleBuilder.newTitle();
            locale.call(player, titleBuilder);
            titleBuilder.build().send(player);
        });
    }

    public void playSound(Sounds sound) {
        for (Player player : players) {
            sound.play(player);
        }
        for (Player spectator : spectators) {
            sound.play(spectator);
        }
    }


    // -------------------------------------------- //
    // SIGNS
    // -------------------------------------------- //

    protected void updateSigns() {
        signManager.updateSigns();
    }

    // -------------------------------------------- //
    // TEAMS
    // -------------------------------------------- //

    private AbstractGameTeam getTeamPreference(Player player, AbstractGameTeam other) {
        return teamPreferences.keySet().stream().filter(gameTeam -> teamPreferences.get(gameTeam).contains(player)).findFirst().orElse(other);
    }

    public AbstractGameTeam getTeamPreference(Player player) {
        return getTeamPreference(player, null);
    }

    public AbstractGameTeam getTeam(Player player) {
        return teams.values().stream().filter(gameTeam -> gameTeam.hasPlayer(player)).findFirst().orElse(null);
    }

    public AbstractGameTeam getTeam(DyeColor color) {
        return teams.values().stream().filter(gameTeam -> gameTeam.getColor() == color).findFirst().orElse(null);
    }

    public AbstractGameTeam getNewTeamForPlayer(Player player) {
        AbstractGameTeam teamPreference = getTeamPreference(player);

        if (teamPreference != null) {
            teamPreferences.get(teamPreference).remove(player);
            return teamPreference;
        }

        for (Map.Entry<AbstractGameTeam, List<String>> teamListEntry : lastTeams.entrySet()) {
            if (teamListEntry.getValue().contains(player.getName())) {
                return teamListEntry.getKey();
            }
        }

        return getTeamWithLowestPlayers();
    }

    public AbstractGameTeam getTeamWithLowestPlayers() {
        return teams.values().stream().min(Comparator.comparingInt(o -> o.getPlayers().size())).orElseThrow(() -> new IllegalStateException("No teams found in arena " + name));
    }

    public void removeGameTeam(String name) {
        teams.remove(name);
    }

    public Map<String, AbstractGameTeam> getTeams() {
        return teams;
    }

    public boolean containsTeam(String name) {
        return getTeam(name) != null;
    }

    public AbstractGameTeam getTeam(String name) {
        return teams.get(name);
    }

    public void addTeam(AbstractGameTeam gameTeam) {
        teams.put(gameTeam.getName(), gameTeam);
    }

    public Map<AbstractGameTeam, List<Player>> getTeamPreferences() {
        return teamPreferences;
    }

    // -------------------------------------------- //
    // ABSTRACT METHODS
    // -------------------------------------------- //

    public abstract AbstractGameTeam createTeam(String name, DyeColor color);

    public abstract AbstractGameTeam createTeam(Map<String, Object> data);

    public abstract int getMinimumRequiredPlayers();

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    @Override
    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }

    @Override
    public void setPermissionGroup(PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean canJoin(Player player) {
        if (enabled && whitelist) {
            return whitelistedPlayers.contains(player.getName());
        }
        return enabled && publicJoinable && permissionGroup.hasPermission(player);
    }

    public boolean canSpectate(Player player) {
        if (enabled && whitelist) {
            return whitelistedPlayers.contains(player.getName()) || publicSpectable;
        }
        return enabled && (publicJoinable || publicSpectable);
    }

    public boolean isFull() {
        return players.size() >= getMaxPlayers();
    }

    public ArenaFile getArenaFile() {
        return arenaFile;
    }

    public PlayerList getPlayers() {
        return players;
    }

    public PlayerList getSpectators() {
        return spectators;
    }

    public ArenaLocation getLobbyLocation() {
        return lobbyLocation;
    }

    public ArenaLocation getSpectatorLocation() {
        return spectatorLocation;
    }

    public Cuboid getArea() {
        return area;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setArea(Cuboid area) {
        this.area = area;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public String getName() {
        return name;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    public boolean isWhitelist() {
        return whitelist;
    }

    public List<String> getWhitelistedPlayers() {
        return whitelistedPlayers;
    }

    public boolean isDisableSaving() {
        return disableSaving;
    }

    public void setDisableSaving(boolean disableSaving) {
        this.disableSaving = disableSaving;
    }

    public void setPublicJoinable(boolean publicJoinable) {
        this.publicJoinable = publicJoinable;
    }

    public boolean isPublicJoinable() {
        return publicJoinable;
    }

    public boolean isPublicSpectable() {
        return publicSpectable;
    }

    public void setPublicSpectable(boolean publicSpectable) {
        this.publicSpectable = publicSpectable;
    }

    public boolean isTeamSelector() {
        return teamSelector;
    }

    public void setTeamSelector(boolean teamSelector) {
        this.teamSelector = teamSelector;
    }

    public boolean isPrivateArena() {
        return privateArena;
    }

    public void setPrivateArena(boolean privateArena) {
        this.privateArena = privateArena;
    }

    public ArenaGroup getArenaGroup() {
        return arenaGroup;
    }

    public void setArenaGroup(ArenaGroup arenaGroup) {
        this.arenaGroup = arenaGroup;
    }

    /**
     * Will return the world name
     * <p>
     * WARNING: It <b>may</b> be different from the arena name. An example
     * as to when it could be different would be when the arena is forced to
     * have a different world name to have multiple arenas with the same map
     * running concurrently.
     * See {@link AdvancedBungeeMode}.
     *
     * @return the world name
     */
    public String getWorldName() {
        return worldName;
    }

    public String getDisplayName() {
        return displayName == null ? worldName : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public World getWorld() {
        return world.getWorld();
    }

    public ArenaWorld getArenaWorld() {
        return world;
    }

    public Phase getPhase() {
        return currentPhase;
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = new ArenaLocation(this, lobbyLocation);
    }

    public void setSpectatorLocation(Location spectatorLocation) {
        this.spectatorLocation = new ArenaLocation(this, spectatorLocation);
    }

    public Cuboid getLobbyArea() {
        return lobbyArea;
    }

    public void setLobbyArea(Cuboid lobbyArea) {
        this.lobbyArea = lobbyArea;
    }

    public boolean isDisableStats() {
        return disableStats;
    }

    public void setDisableStats(boolean disableStats) {
        this.disableStats = disableStats;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    /**
     * Gets topKillers
     *
     * @return value of topKillers
     */
    public Map<String, Integer> getTopKillers() {
        return topKillers;
    }

    public boolean canJoinArena() {
        switch (getArenaState()) {
            case WAITING:
            case STARTING:
            case IN_GAME:
                return true;
            case ENDING:
            case RESTARTING:
            case OTHER:
            case ERROR:
                return false;
        }

        throw new IllegalStateException("Unhandled arena state " + getArenaState());
    }

    public ArenaState getArenaState() {
        return currentPhase.getArenaState();
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public long getPhaseTime() {
        return phaseTime;
    }

    public long getTimePhaseHasBeenRunning() {
        return System.currentTimeMillis() - phaseTime;
    }

    public EventRegistry getEventRegistry() {
        return eventRegistry;
    }

    void addPlayerToLastTeam(AbstractGameTeam team, Player player) {
        if (!lastTeams.containsKey(team)) {
            lastTeams.put(team, new ArrayList<>());
        }

        lastTeams.get(team).add(player.getName());
    }

    public boolean hasLastTeam(Player player) {
        for (Map.Entry<AbstractGameTeam, List<String>> teamListEntry : lastTeams.entrySet()) {
            if (teamListEntry.getValue().contains(player.getName())) {
                return true;
            }
        }

        return false;
    }

    public <T extends ArenaOption> T getOption(Class<? extends T> arenaOptionClass) {
        for (ArenaOption arenaOption : arenaOptions) {
            if (arenaOption.getClass() == arenaOptionClass) {
                return (T) arenaOption;
            }
        }

        throw new IllegalArgumentException("Option " + arenaOptionClass.getName() + " is not registered!");
    }

    public List<ArenaOption> getArenaOptions() {
        return arenaOptions;
    }

    public Provider<WorldHandler> getWorldHandler() {
        return worldHandler;
    }

    public PluginScheduler getPluginScheduler() {
        return pluginScheduler;
    }

    public Vector getOffset() {
        return offset;
    }

    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    public void setArenaWorld(ArenaWorld arenaWorld) {
        this.world = arenaWorld;
    }
}
