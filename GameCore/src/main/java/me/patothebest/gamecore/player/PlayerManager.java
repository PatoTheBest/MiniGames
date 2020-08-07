package me.patothebest.gamecore.player;

import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.storage.StorageManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@ModuleName("Player Manager")
public class PlayerManager implements ListenerModule, ActivableModule {

    private static PlayerManager instance;

    private final Map<String, CorePlayer> players = new HashMap<>();
    private final List<String> dupedPlayers = new ArrayList<>();
    private final StorageManager storageManager;
    private final CoreConfig coreConfig;
    private final PlayerFactory playerFactory;

    @Inject private PlayerManager(CoreConfig coreConfig, PlayerFactory playerFactory, StorageManager storageManager) {
        if(instance != null) {
            throw new RuntimeException("Cannot redefine PlayerManager");
        }

        instance = this;
        this.coreConfig = coreConfig;
        this.playerFactory = playerFactory;
        this.storageManager = storageManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void dupeCheck(AsyncPlayerPreLoginEvent event) {
        if(!players.containsKey(event.getName())) {
            return;
        }

        if (Bukkit.getPlayerExact(event.getName()) == null) {
            players.remove(event.getName());
            return;
        }

        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.setKickMessage("You are already connected to this server!");
        dupedPlayers.add(event.getName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if(event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        CorePlayer player = playerFactory.create(coreConfig.getDefaultLocale());
        player.setPlayerIdentity(new PlayerIdentity(event.getName(), event.getUniqueId()));
        players.put(player.getName(), player);
        storageManager.getStorage().load(player, false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if(event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if(dupedPlayers.contains(event.getName())) {
            dupedPlayers.remove(event.getName());
            return;
        }

        players.remove(event.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLoginEarly(PlayerLoginEvent event) {
        if(!players.containsKey(event.getPlayer().getName())) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("Server is still starting up!");
            return;
        }

        players.get(event.getPlayer().getName()).setPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if(event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            players.get(event.getPlayer().getName()).loginPrepare();
            return;
        }

        players.remove(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        players.get(event.getPlayer().getName()).joinPrepare();
    }

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent event) {
        Location joinLocation = players.get(event.getPlayer().getName()).getJoinLocation();

        if(joinLocation != null) {
            event.setSpawnLocation(joinLocation);
        }
    }

    public void loadPlayer(Player bukkitPlayer) {
        if(bukkitPlayer == null || !bukkitPlayer.isOnline()) {
            return;
        }

        CorePlayer player = playerFactory.create(coreConfig.getDefaultLocale());
        player.setPlayerIdentity(new PlayerIdentity(bukkitPlayer.getName(), bukkitPlayer.getUniqueId()));
        players.put(bukkitPlayer.getName(), player);
        storageManager.getStorage().load(player, false);
        player.setPlayer(bukkitPlayer);
        player.loginPrepare();
        player.joinPrepare();
    }

    public IPlayer getOfflinePlayer(String name) {
        if (players.containsKey(name)) {
            throw new IllegalStateException("Player is online!");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        CorePlayer player = playerFactory.create(coreConfig.getDefaultLocale());
        player.setPlayerIdentity(new PlayerIdentity(offlinePlayer.getName(), offlinePlayer.getUniqueId()));
        players.put(offlinePlayer.getName(), player);
        storageManager.getStorage().load(player, false);
        return player;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerKickEvent event) {
        removePlayer(event.getPlayer());
    }

    public void removePlayer(Player bukkitPlayer) {
        CorePlayer player = players.get(bukkitPlayer.getName());

        if(player == null) {
            return;
        }

        if(player.getCurrentArena() != null) {
            player.getCurrentArena().removePlayer(bukkitPlayer, true);
        }

        destroyPlayer(bukkitPlayer);
    }

    public void destroyPlayer(Player bukkitPlayer) {
        CorePlayer player = players.get(bukkitPlayer.getName());

        if(player == null) {
            return;
        }

        players.remove(bukkitPlayer.getName());

        if(storageManager.doPlayersNeedSaving()) {
            storageManager.getStorage().save(player);
        }

        storageManager.getStorage().unCache(player);
        player.destroy();
    }

    public IPlayer getPlayer(String name) {
        return players.get(name);
    }

    public IPlayer getPlayer(Player player) {
        Validate.notNull(player, "Player must not be null!");
        return getPlayer(player.getName());
    }

    @Override
    public void onDisable() {
        // save players
        Bukkit.getOnlinePlayers().forEach(this::removePlayer);
    }

    public Collection<CorePlayer> getPlayers() {
        return players.values();
    }

    public static PlayerManager get() {
        return instance;
    }
}