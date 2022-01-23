package me.patothebest.gamecore.phase.phases;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.arena.types.SpawneableArena;
import me.patothebest.gamecore.event.player.LobbyJoinEvent;
import me.patothebest.gamecore.feature.features.other.CountdownFeature;
import me.patothebest.gamecore.feature.features.other.LobbyFeature;
import me.patothebest.gamecore.feature.features.other.WaitingPhaseFeature;
import me.patothebest.gamecore.feature.features.protection.PlayerProtectionFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorProtectionFeature;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.phase.AbstractPhase;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.sign.SignManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WaitingPhase<Arena extends AbstractArena & SpawneableArena> extends AbstractPhase<CorePlugin, Arena> {

    protected final List<Location> spawnLocations = new ArrayList<>();
    protected final Map<Player, Location> usedLocations = new HashMap<>();
    protected final Provider<NMS> nms;
    protected final PlayerManager playerManager;
    protected final SignManager signManager;
    protected boolean autoStart;

    @Inject WaitingPhase(CorePlugin plugin, Provider<NMS> nms, PlayerManager playerManager, SignManager signManager) {
        super(plugin);
        this.nms = nms;
        this.playerManager = playerManager;
        this.signManager = signManager;
    }

    @Override
    public void configure() {
        registerFeature(CountdownFeature.class);
        registerFeature(LobbyFeature.class);
        registerFeature(PlayerProtectionFeature.class);
        registerFeature(WaitingPhaseFeature.class);
        registerFeature(SpectatorProtectionFeature.class);
        registerFeature(SpectatorFeature.class);
    }

    @Override
    public void start() {
        super.start();
        if(autoStart) {
            CountdownFeature countdownFeature = arena.getFeature(CountdownFeature.class);
            countdownFeature.setOverrideRunning(true);
            countdownFeature.startCountdown(10);
        }

        this.spawnLocations.addAll(arena.getSpawns().stream().map(arenaLocation -> arenaLocation.clone().add(0, arena.getSpawnHeight(), 0)).collect(Collectors.toList()));

        // iterate over each player
        arena.getPlayers().forEach(player -> playerJoin(player, false));
    }

    @Override
    public void stop() {
        super.stop();
        this.spawnLocations.clear();
        this.usedLocations.clear();
    }

    @Override
    public void playerJoin(Player player) {
        playerJoin(player, true);
    }

    private void playerJoin(Player player, boolean newPlayer) {
        // checks if the player has the arena permission
        if(!arena.getPermissionGroup().hasPermission(player)) {
            // sends the no permission message to the player
            player.sendMessage(CoreLang.NO_PERMISSION_ARENA.getMessage(player));
            return;
        }

        IPlayer iPlayer = playerManager.getPlayer(player);

        Location spawnLocation;

        if(spawnLocations.isEmpty()) {
            spawnLocation = iPlayer.getGameTeam().getSpawn();
        } else {
            spawnLocation = Utils.getFromCollection(spawnLocations, t -> !usedLocations.containsValue(t));
        }

        preTeleport(iPlayer, spawnLocation);
        Location clone = spawnLocation.clone();
        clone.setX(spawnLocation.getBlockX() + 0.5);
        clone.setZ(spawnLocation.getBlockZ() + 0.5);
        clone.setY(spawnLocation.getBlockY() + 2);

        if(iPlayer.isFullyJoined()) {
            player.teleport(clone);
            player.setVelocity(new Vector(0, 0, 0));
        } else {
            iPlayer.setJoinLocation(clone);
        }

        usedLocations.put(player, spawnLocation);

        if(newPlayer) {
            // sets the arena of the player and saves the player state
            playerManager.getPlayer(player).setCurrentArena(arena);
            iPlayer.executeWhenFullyLoaded(player1 -> {
                player1.getPlayerInventory().savePlayer();
            });
        } else {
            player.getInventory().clear();
        }

        // give items
        iPlayer.executeWhenFullyLoaded(player1 -> {
            player.getInventory().setItem(0, new ItemStackBuilder().material(Material.NETHER_STAR).name(player, CoreLang.LOBBY_CHOOSE_KIT));
            player.getInventory().setItem(8, new ItemStackBuilder().material(Material.MAGMA_CREAM).name(player, CoreLang.LOBBY_LEAVE));

            // if the player has the admin permission
            if(player.hasPermission(Permission.ADMIN.getBukkitPermission()) || player.hasPermission(Permission.FORCE_START.getBukkitPermission())) {
                // give the admin the admin item
                player.getInventory().setItem(7, new ItemStackBuilder().material(Material.COMPARATOR).name(player, CoreLang.LOBBY_ADMIN_MENU));
            }
        });

        // add the player to the cooldown list
        // this prevents instantly leaving the arena if the slot selected
        // was the one with the leave item and the join method was by right
        // clicking a sign to join
        arena.getFeature(LobbyFeature.class).playerJoin(player);

        if(newPlayer) {
            // add the player to the arena
            arena.getPlayers().add(player);

            // send the player joined message to the arena
            arena.sendMessageToArena(locale -> CoreLang.PLAYER_JOINED.getMessage(locale).replace("%player%", player.getName()).replace("%players%", arena.getPlayers().size() + "").replace("%max_players%", arena.getMaxPlayers() + ""));

            iPlayer.executeWhenFullyLoaded(player1 -> {
                // call event
                plugin.getServer().getPluginManager().callEvent(new LobbyJoinEvent(player, arena));
            });
        }

        signManager.updateSigns();
    }

    protected void preTeleport(IPlayer player, Location location) { }

    @Override
    public ArenaState getArenaState() {
        return getPreviousPhase() == null ? ArenaState.WAITING : getPreviousPhase().getArenaState();
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }
}
