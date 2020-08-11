package me.patothebest.gamecore.privatearenas;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.event.arena.ArenaPreRegenEvent;
import me.patothebest.gamecore.event.player.ArenaPreLeaveEvent;
import me.patothebest.gamecore.feature.features.other.CountdownFeature;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.logger.Logger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.PlayerList;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
@ModuleName("Private Arenas Manager")
public class PrivateArenasManager implements ReloadableModule, ActivableModule, ListenerModule {

    private final Map<String, PrivateArena> privateArenaMap = new HashMap<>();
    private final List<AbstractArena> changingArenas = new ArrayList<>();
    private final ArenaManager arenaManager;
    private final PlayerManager playerManager;
    private final List<String> enabledArenas = new ArrayList<>();
    private final AtomicInteger id = new AtomicInteger(1);
    @InjectLogger private Logger logger;

    @Inject private PrivateArenasManager(ArenaManager arenaManager, PlayerManager playerManager) {
        this.arenaManager = arenaManager;
        this.playerManager = playerManager;
    }

    @Override
    public void onPreEnable() {
        enabledArenas.clear();
        File[] files = ArenaManager.ARENA_DIRECTORY.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            String name = file.getName().replace(".yml", "");
            String contents = Utils.readFileAsString(file);
            if (contents.contains("enabled: true")) {
                enabledArenas.add(name);
                logger.config("Adding arena {0} as an option", name);
            }
        }
    }

    @Override
    public void onReload() {
        onDisable();
        onPreEnable();
    }

    @Override
    public void onDisable() {
        for (PrivateArena value : privateArenaMap.values()) {
            removePrivateArena(value);
        }
    }

    @EventHandler
    public void onPreRegen(ArenaPreRegenEvent event) {
        for (PrivateArena value : privateArenaMap.values()) {
            if (event.getArena() == value.getArena()) {
                event.setCancelled(true);
                changeArena(value, Utils.getRandomElementFromList(enabledArenas));
                return;
            }
        }
    }

    @EventHandler
    public void onLeave(ArenaPreLeaveEvent event) {
        if (changingArenas.contains(event.getArena())) {
            event.setCancelTeleport(true);
        }
    }

    public void changeArena(PrivateArena arena, String newMap) {
        AbstractArena newArena = loadArena(arena.getOwnerName(), newMap);
        AbstractArena oldArena = arena.getArena();
        PlayerList playersToMove = oldArena.getPlayers();
        PlayerList spectatorsToMove = oldArena.getSpectators();
        newArena.setWhitelist(oldArena.isWhitelist());
        newArena.setDisableSaving(oldArena.isDisableSaving());
        newArena.setPublicJoinable(oldArena.isPublicJoinable());
        newArena.setPublicSpectable(oldArena.isPublicSpectable());
        newArena.setTeamSelector(oldArena.isTeamSelector());
        newArena.setDisplayName(oldArena.getDisplayName());
        arena.setArena(newArena);

        changingArenas.add(oldArena);
        for (Player player : playersToMove) {
            oldArena.removePlayer(player);
            newArena.addPlayer(player);
        }

        for (Player player : spectatorsToMove) {
            boolean canPlay = oldArena.canJoin(player);
            oldArena.removePlayer(player);
            if (canPlay) {
                newArena.addPlayer(player);
            } else {
                newArena.addSpectator(player);
            }
        }

        changingArenas.remove(oldArena);
        newArena.getWhitelistedPlayers().addAll(oldArena.getWhitelistedPlayers());

        arenaManager.getArenas().values().remove(oldArena);
        oldArena.getArenaWorld().unloadWorld(false);
        oldArena.getArenaWorld().deleteWorld();
        oldArena.destroy();
    }

    @Override
    public String getReloadName() {
        return "private-arenas";
    }

    public void destroy(PrivateArena arena) {
        AbstractArena oldArena = arena.getArena();
        arenaManager.getArenas().values().remove(oldArena);
        oldArena.destroy();
    }

    public PrivateArena createPrivateArena(Player player) {
        if (playerManager.getPlayer(player).isInArena()) {
            CoreLang.ALREADY_IN_ARENA.sendMessage(player);
            return null;
        }

        String randomArena = Utils.getRandomElementFromList(enabledArenas);

        if (randomArena == null) {
            return null;
        }

        AbstractArena abstractArena = loadArena(player.getName(), randomArena);
        PrivateArena privateArena = new PrivateArena(player.getName(), abstractArena);
        privateArenaMap.put(player.getName(), privateArena);
        abstractArena.addPlayer(player);
        return privateArena;
    }

    private AbstractArena loadArena(String playerName, String arenaName) {
        String worldName = "private_" + playerName + "_" + arenaName + "_" + id.getAndIncrement();
        logger.fine("Loading arena {0} for player {1}. WorldName: {2}", arenaName, playerName, worldName);
        AbstractArena arena = arenaManager.loadArena(arenaName, worldName, false);
        arena.setDisableSaving(true);
        arena.setDisableStats(true);
        arena.setWhitelist(true);
        arena.getWhitelistedPlayers().add(playerName);
        arena.setPublicJoinable(false);
        arena.setPublicSpectable(false);
        arena.setPrivateArena(true);
        arena.initializeData();
        arena.setDisplayName(playerName + "'s Private Arena");
        arena.getFeature(CountdownFeature.class).setOverrideRunning(true);
        return arena;
    }

    public Map<String, PrivateArena> getPrivateArenaMap() {
        return privateArenaMap;
    }

    public void removePrivateArena(PrivateArena privateArena) {
        logger.fine("Removing private arena of {0}", privateArena.getOwnerName());
        AbstractArena arena = privateArena.getArena();
        arena.getArenaWorld().deleteWorld();
        arena.destroy();
        arenaManager.getArenas().remove(arena.getWorldName());
        privateArenaMap.remove(privateArena.getOwnerName());
    }

    public List<String> getEnabledArenas() {
        return enabledArenas;
    }

    public PrivateArena getCurrentArena(Player player) {
        AbstractArena currentArena = playerManager.getPlayer(player).getCurrentArena();

        if (currentArena == null) {
            return null;
        }

        for (PrivateArena value : privateArenaMap.values()) {
            if (value.getArena() == currentArena) {
                return value;
            }
        }

        return null;
    }
}
