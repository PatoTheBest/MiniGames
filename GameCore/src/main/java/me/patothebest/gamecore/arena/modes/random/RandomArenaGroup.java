package me.patothebest.gamecore.arena.modes.random;

import com.google.inject.Provider;
import me.patothebest.gamecore.event.player.ArenaPreJoinEvent;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.event.arena.ArenaPhaseChangeEvent;
import me.patothebest.gamecore.event.arena.ArenaPreRegenEvent;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.sign.ArenaSign;
import me.patothebest.gamecore.sign.SignManager;
import me.patothebest.gamecore.util.NameableObject;
import me.patothebest.gamecore.util.PlayerList;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.world.WorldHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomArenaGroup implements Listener, NameableObject, Runnable {

    private final Logger logger;
    private final String groupName;
    private final PluginScheduler pluginScheduler;
    private final SignManager signManager;
    protected final ArenaManager arenaManager;
    private final Provider<WorldHandler> worldHandlerProvider;
    private final PlayerManager playerManager;
    private final Set<String> enabledArenas = new HashSet<>();
    private final Set<String> allArenas = new HashSet<>();
    private final Queue<String> nextMaps = new ConcurrentLinkedQueue<>();
    private final Map<String, List<Player>> playerQueue = new HashMap<>();
    private final PlayerList playersList = new PlayerList();
    private final PlayerList toRemoveList = new PlayerList();
    private final Map<String, AbstractArena> arenas = new ConcurrentHashMap<>();
    private final AtomicInteger loadingArenas = new AtomicInteger(0);
    private int id = 0;
    private int maxArenas;
    private int concurrentArenas;

    protected RandomArenaGroup(String groupName, Logger logger, PluginScheduler pluginScheduler, SignManager signManager, ConfigurationSection configurationSection, ArenaManager arenaManager, Provider<WorldHandler> worldHandlerProvider, PlayerManager playerManager) {
        this.groupName = groupName;
        this.logger = logger;
        this.pluginScheduler = pluginScheduler;
        this.signManager = signManager;
        this.arenaManager = arenaManager;
        this.worldHandlerProvider = worldHandlerProvider;
        this.playerManager = playerManager;

        List<String> arenas = configurationSection.getStringList("arenas");
        List<String> temp = new ArrayList<>();

        if (ArenaManager.ARENA_DIRECTORY.listFiles() == null) {
            return;
        }

        for (File file : ArenaManager.ARENA_DIRECTORY.listFiles()) {
            temp.add(file.getName().replace(".yml", ""));
        }

        for (String arena : arenas) {
            if (temp.contains(arena)) {
                enabledArenas.add(arena);
                allArenas.add(arena);
                logger.log(Level.CONFIG, "Using arena " + arena + " for random group " + groupName);
            } else {
                Utils.printError("Unknown arena " + arena + " in random-arena-mode");
            }
        }

        concurrentArenas = configurationSection.getInt("concurrent-arenas");
        maxArenas = configurationSection.getInt("max-arenas", concurrentArenas);
    }

    public void init() {
        for (int i = 0; i < concurrentArenas; i++) {
            if(loadRandomArena() == null) {
                return;
            }
        }
    }

    @Override
    public void run() {
        if (loadingArenas.get() > 0) {
            loadingArenas.decrementAndGet();
            loadRandomArena();
        }
    }

    private void queueLoadRandomArena() {
        if (worldHandlerProvider.get().hasAsyncSupport()) {
            loadingArenas.incrementAndGet();
        } else {
            pluginScheduler.ensureSync(this::loadRandomArena);
        }
    }

    private AbstractArena loadRandomArena() {
        if (!nextMaps.isEmpty()) {
            return loadArena(nextMaps.poll());
        }

        return loadArena(Utils.getRandomElementFromCollection(enabledArenas));
    }

    private synchronized AbstractArena loadArena(String name) {
        AbstractArena abstractArena = loadArena(name, name + "-" + ++id);

        if(abstractArena == null) {
            Utils.printError("Could not find an arena for random-mode!");
        } else {
            logger.log(Level.INFO, "Loading random arena '" + name + "' with world name '" + abstractArena.getWorldName() + "'");
            enabledArenas.remove(name);
            if (!abstractArena.isEnabled()) {
                pluginScheduler.ensureSync(abstractArena::destroy);
                return loadRandomArena();
            }
            arenas.put(name, abstractArena);

            for (ArenaSign sign : signManager.getSigns()) {
                if (sign.getCurrentArena().equalsIgnoreCase(groupName)) {
                    sign.setCurrentArena(abstractArena.getWorldName());
                    break;
                }
            }

            if (playerQueue.containsKey(name)) {
                pluginScheduler.ensureSync(() -> {
                    List<Player> players = playerQueue.remove(name);
                    for (Player player : players) {
                        playersList.remove(player);
                        toRemoveList.remove(player);

                        if (abstractArena.isFull()) {
                            continue;
                        }

                        if (playerManager.getPlayer(player).isInArena()) {
                            playerManager.getPlayer(player).getCurrentArena().removePlayer(player);
                        }
                        abstractArena.addPlayer(player);
                    }
                });
            }

            signManager.updateSigns();
        }

        return abstractArena;
    }

    protected AbstractArena loadArena(String arenaName, String worldName) {
        return arenaManager.loadArena(arenaName, worldName);
    }

    @EventHandler
    public void onPhaseChange(ArenaPhaseChangeEvent event) {
        if (needsAnotherArena()) {
            queueLoadRandomArena();
        }
    }

    @EventHandler
    public void onPreRegen(ArenaPreRegenEvent event) {
        if (!arenas.containsKey(event.getArena().getName())) {
            return;
        }

        event.setCancelled(true);
        destroyArena(event.getArena(), false);
    }

    @EventHandler
    public void onArenaPreJoin(ArenaPreJoinEvent event) {
        if (toRemoveList.contains(event.getPlayer())) {
            CoreLang.PLAYER_QUEUE_LEAVE.replaceAndSend(event.getPlayer(), removePlayer(event.getPlayer()));
            return;
        }

        if (playersList.contains(event.getPlayer())) {
            toRemoveList.add(event.getPlayer());
            CoreLang.PLAYER_QUEUED.sendMessage(event.getPlayer());
            event.setCancelled(true);
        }
    }

    private boolean needsAnotherArena() {
        // count loading arenas as joinable arenas
        // to account for arenas that are queued to
        // be loaded and joinable
        int joinableArenas = loadingArenas.get();
        for (AbstractArena value : arenas.values()) {
            if (value.getPhase().canJoin() && !value.isFull()) {
                joinableArenas++;
            }
        }

        return joinableArenas < concurrentArenas && arenas.size() < maxArenas;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    private String removePlayer(Player player) {
        String s = "";
        for (String map : playerQueue.keySet()) {
            List<Player> players = playerQueue.get(map);
            if (playerQueue.get(map).contains(player) ){
                s = map;
                players.remove(player);
                if (players.isEmpty()) {
                    playerQueue.remove(map);
                    nextMaps.remove(map);
                }
            }
        }
        toRemoveList.remove(player);
        playersList.remove(player);
        return s;
    }

    public void destroyArena(AbstractArena arena, boolean loadAnotherOverride) {
        logger.log(Level.INFO, "Destroying arena " + arena.getWorldName());
        for (Player player : arena.getPlayers()) {
            arena.removePlayer(player);
        }
        for (Player player : arena.getSpectators()) {
            arena.removePlayer(player);
        }

        pluginScheduler.runTaskLater(() -> {
            // unload the world
            arena.getArenaWorld().unloadWorld(false);

            pluginScheduler.runTaskLaterAsynchronously(() -> {
                arena.getArenaWorld().deleteWorld();
                pluginScheduler.ensureSync(arena::destroy);
                arenas.values().remove(arena);
                enabledArenas.add(arena.getName());
                arenaManager.getArenas().remove(arena.getWorldName());

                removeSign(arena);

                if (loadAnotherOverride || needsAnotherArena()) {
                    queueLoadRandomArena();
                }}, 10L);
        }, 1L);
    }

    public void destroy() {
        getArenas().values().forEach(arena -> {
            arena.destroy();
            removeSign(arena);
        });
    }

    private void removeSign(AbstractArena arena) {
        for (ArenaSign sign : signManager.getSigns()) {
            if (!sign.getArena().equalsIgnoreCase(groupName)) {
                continue;
            }

            if (sign.getCurrentArena().equalsIgnoreCase(arena.getWorldName())) {
                sign.setCurrentArena(groupName);
            }
        }
        signManager.updateSigns();
    }

    public Set<String> getEnabledArenas() {
        return enabledArenas;
    }

    public Set<String> getAllArenas() {
        return allArenas;
    }

    public Map<String, AbstractArena> getArenas() {
        return arenas;
    }

    public boolean isQueued(Player player) {
        return playersList.contains(player);
    }

    public void addPlayerToQueue(Player player, String arena) {
        if (isQueued(player)) {
            removePlayer(player);
        }

        if (!nextMaps.contains(arena)) {
            nextMaps.add(arena);
            playerQueue.put(arena, new ArrayList<>());
        }
        playerQueue.get(arena).add(player);
        playersList.add(player);
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public String getName() {
        return groupName;
    }
}
