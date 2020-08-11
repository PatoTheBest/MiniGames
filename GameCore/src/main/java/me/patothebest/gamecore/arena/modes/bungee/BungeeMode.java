package me.patothebest.gamecore.arena.modes.bungee;

import com.google.inject.Inject;
import me.patothebest.gamecore.api.BungeeStateUpdate;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.event.arena.ArenaPhaseChangeEvent;
import me.patothebest.gamecore.event.arena.ArenaPreRegenEvent;
import me.patothebest.gamecore.event.player.ArenaPreLeaveEvent;
import me.patothebest.gamecore.event.player.PlayerLoginPrepareEvent;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.phase.phases.EndPhase;
import me.patothebest.gamecore.placeholder.PlaceHolderManager;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.scoreboard.CoreScoreboardType;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ModuleName("Bungee Mode")
public class BungeeMode implements ActivableModule, ListenerModule {

    private final PluginScheduler pluginScheduler;
    private final CoreConfig coreConfig;
    private final ArenaManager arenaManager;
    private final BungeeHandler bungeeHandler;
    private final EventRegistry eventRegistry;
    private final PlaceHolderManager placeHolderManager;
    private final Map<String, String> motdStatus = new HashMap<>();
    private final Map<String, AbstractArena> playerQueue = new ConcurrentHashMap<>();
    private final Set<String> enabledArenas = new HashSet<>();
    private final List<String> loadingPlayers = new ArrayList<>();
    private String repo;
    private boolean enabled;
    private boolean changingArena;
    private boolean restartASAP = false;
    private int restartAfter;
    private int arenasPlayed;
    private AbstractArena abstractArena;
    private String lobbyServer;

    @Inject private BungeeMode(PluginScheduler pluginScheduler, CoreConfig coreConfig, ArenaManager arenaManager, BungeeHandler bungeeHandler, EventRegistry eventRegistry, PlaceHolderManager placeHolderManager) {
        this.pluginScheduler = pluginScheduler;
        this.coreConfig = coreConfig;
        this.arenaManager = arenaManager;
        this.bungeeHandler = bungeeHandler;
        this.eventRegistry = eventRegistry;
        this.placeHolderManager = placeHolderManager;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPreEnable() {
        ConfigurationSection bungeeSection = coreConfig.getConfigurationSection("bungee-mode");

        if(bungeeSection == null) {
            return;
        }

        if(!bungeeSection.getBoolean("enabled")) {
            return;
        }

        String arena = bungeeSection.getString("arena");

        if(arena.equalsIgnoreCase("random")) {
            if(ArenaManager.ARENA_DIRECTORY.listFiles() == null) {
                Utils.printError("Could not find arena directory!");
                return;
            }

            for (File file : ArenaManager.ARENA_DIRECTORY.listFiles()) {
                enabledArenas.add(file.getName().replace(".yml", ""));
            }

            if(!loadRandomArena()) {
                return;
            }
        } else {
            abstractArena = arenaManager.loadArena(arena);

            if(abstractArena == null) {
                Utils.printError("Could not find arena " + arena + " for bungee-mode!");
                return;
            }
        }

        bungeeSection.getConfigurationSection("motd").getValues(true).forEach((status, motd) -> motdStatus.put(status, (String) motd));

        lobbyServer = bungeeSection.getString("leave-server");
        restartAfter = bungeeSection.getInt("restart-after");
        repo = bungeeSection.getString("repo");

        enabled = true;
        arenaManager.setLoadMaps(false);
    }

    private boolean loadRandomArena() {
        if(repo != null && !repo.isEmpty()) {
            try {
                String list = Utils.urlToString(new URL(repo), StandardCharsets.UTF_8);
                String[] split = list.split("\n");
                ArrayList<String> arenaList = new ArrayList<>(enabledArenas);
                arenaList.retainAll(Arrays.asList(split));
                return loadArena(Utils.getRandomElementFromCollection(arenaList));
            } catch (IOException e) {
                e.printStackTrace();
                return loadArena(Utils.getRandomElementFromCollection(enabledArenas));
            }
        } else {
            return loadArena(Utils.getRandomElementFromCollection(enabledArenas));
        }
    }

    private boolean loadArena(String name) {
        abstractArena = arenaManager.loadArena(name);

        if(abstractArena == null) {
            Utils.printError("Could not find an arena for bungee-mode!");
        }

        arenasPlayed++;

        try {
            Utils.setMaxPlayers(abstractArena.getMaxPlayers());
        } catch (ReflectiveOperationException e) {
            Utils.printError("Could not set max players!");
            e.printStackTrace();
        }

        return abstractArena != null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!enabled) {
            return;
        }

        if(event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if(abstractArena.canJoinArena()) {
            if(abstractArena.getPhase().canJoin()) {
                loadingPlayers.add(event.getName());
            }
            return;
        }

        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.setKickMessage(CoreLang.ARENA_IS_RESTARTING.getDefaultMessage());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPreLogin2(AsyncPlayerPreLoginEvent event) {
        if(!enabled) {
            return;
        }

        if(event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if(loadingPlayers.contains(event.getName())) {
            if(!abstractArena.getPhase().canJoin()) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(CoreLang.ARENA_HAS_STARTED.getDefaultMessage());
            }

            loadingPlayers.remove(event.getName());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPrepare(PlayerLoginPrepareEvent event) {
        if(!enabled) {
            return;
        }

        event.getPlayer().setScoreboardToShow(CoreScoreboardType.NONE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPrepare2(PlayerLoginPrepareEvent event) {
        if(!enabled) {
            return;
        }

        if(abstractArena.getPhase().canJoin()) {
            abstractArena.addPlayer(event.getPlayer().getPlayer());
        } else if(abstractArena.canJoinArena()){
            abstractArena.addSpectator(event.getPlayer().getPlayer());
        }
    }

    @EventHandler
    public void onArenaPhaseChange(ArenaPhaseChangeEvent event) {
        if(!enabled) {
            return;
        }

        eventRegistry.callEvent(new BungeeStateUpdate(getMOTD()));
    }

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        if(!enabled) {
            return;
        }

        event.setMotd(getMOTD());
    }

    @EventHandler
    public void onArenaLeave(ArenaPreLeaveEvent event) {
        if(!enabled) {
            return;
        }

        if(changingArena) {
            return;
        }

        bungeeHandler.sendPlayer(event.getPlayer(), lobbyServer);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPreRegen(ArenaPreRegenEvent event) {
        if(!enabled) {
            return;
        }

        if(changingArena) {
            return;
        }

        event.setCancelled(true);

        if(Bukkit.getOnlinePlayers().isEmpty()) {
            scheduleRegen();
        } else for (Player player : Bukkit.getOnlinePlayers()) {
            bungeeHandler.sendPlayer(player, lobbyServer);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(!enabled) {
            return;
        }

        if(Bukkit.getOnlinePlayers().size() > 1) {
            return;
        }

        if(restartASAP) {
            Bukkit.shutdown();
            return;
        }

        if(abstractArena.getPhase() instanceof EndPhase) {
           scheduleRegen();
        }
    }

    private void scheduleRegen() {
        pluginScheduler.runTaskLater(() -> {
            // unload the world
            abstractArena.getArenaWorld().unloadWorld(false);

            pluginScheduler.runTaskLater(() -> {
                abstractArena.getArenaWorld().deleteWorld();
                abstractArena.destroy();

                if(restartAfter != -1 && arenasPlayed >= restartAfter) {
                    Bukkit.shutdown();
                }

                if(!loadRandomArena()) {
                    Bukkit.getServer().shutdown();
                }
            }, 10L);
        }, 1L);
    }

    void changeArena(String arena) {
        changingArena = true;
        abstractArena.destroy();
        loadArena(arena);
        Bukkit.getOnlinePlayers().forEach(player -> abstractArena.addPlayer(player));
        changingArena = false;
    }

    private String getMOTD() {
        String motd = motdStatus.get(abstractArena.getArenaState().getName().toLowerCase());

        if(motd == null || motd.isEmpty()) {
            Utils.printError("Could not find MOTD for state " + abstractArena.getArenaState().getName().toLowerCase());
            return null;
        }

        return placeHolderManager.replace(abstractArena, motd);
    }

    public Set<String> getEnabledArenas() {
        return enabledArenas;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void restartASAP() {
        this.restartAfter = -1;
        this.restartASAP = true;
    }

    public Map<String, AbstractArena> getPlayerQueue() {
        return playerQueue;
    }
}