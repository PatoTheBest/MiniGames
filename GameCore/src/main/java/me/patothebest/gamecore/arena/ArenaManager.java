package me.patothebest.gamecore.arena;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.event.arena.ArenaLoadEvent;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.logger.Logger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.sign.SignManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Singleton
@ModuleName("Arena Manager")
public class ArenaManager implements ActivableModule, ReloadableModule {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    public final static File ARENA_DIRECTORY = new File(Utils.PLUGIN_DIR, "arenas");
    private final CorePlugin plugin;
    private final PluginScheduler pluginScheduler;
    private final ArenaFactory arenaFactory;
    private final Map<String, AbstractArena> arenas;
    private final Map<String, ArenaGroup> arenaGroups = new HashMap<>();
    private boolean loadMaps = true;
    @InjectLogger private Logger logger;

    // Sorted by:
    private final Comparator<AbstractArena> arenaComparator = (arena1, arena2) -> {
        if (arena1.isPublicJoinable() != arena2.isPublicJoinable()) {
            return Boolean.compare(arena2.isPublicJoinable(), arena1.isPublicJoinable());
        } else if (arena1.canJoinArena() != arena2.canJoinArena()){
            return Boolean.compare(arena2.canJoinArena(), arena1.canJoinArena());
        } else if (arena1.getPhase().canJoin() != arena2.getPhase().canJoin()) {
            return Boolean.compare(arena2.getPhase().canJoin(), arena1.getPhase().canJoin());
        } else {
            return Integer.compare(arena2.getPlayers().size(), arena1.getPlayers().size());
        }
    };

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private ArenaManager(CorePlugin plugin, PluginScheduler pluginScheduler, ArenaFactory arenaFactory, Set<ArenaGroup> arenaGroups) {
        this.plugin = plugin;
        this.pluginScheduler = pluginScheduler;
        this.arenaFactory = arenaFactory;
        for (ArenaGroup arenaGroup : arenaGroups) {
            this.arenaGroups.put(arenaGroup.getName().toUpperCase(), arenaGroup);
        }

        // case insensitive to avoid issues
        this.arenas = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

        // If the arena directory does not exist...
        if (!ARENA_DIRECTORY.exists()) {
            // ...create the directory
            ARENA_DIRECTORY.mkdirs();
        }
    }

    // -------------------------------------------- //
    // PUBLIC METHODS
    // -------------------------------------------- //


    @Override
    public void onPreEnable() {
        arenaGroups.forEach((groupName, arenaGroup) -> {
            logger.config("Registering group {0}", groupName);
        });
    }

    @Override
    public void onEnable() {
        if (!loadMaps) {
            return;
        }

        logger.info(ChatColor.YELLOW + "Loading arenas...");

        // If there are no files on the folder...
        if (ARENA_DIRECTORY.listFiles() == null) {
            // ...return
            return;
        }

        // iterate over every file
        for (File file : ARENA_DIRECTORY.listFiles()) {
            String name = file.getName().replace(".yml", "");
            loadArena(name);
        }
    }

    public AbstractArena reloadArena(AbstractArena arena) {
        arena.saveAndDestroy();
        arenas.remove(arena.getName());
        return loadArena(arena.getName());
    }

    public AbstractArena loadArena(String name) {
        return loadArena(name, name);
    }


    public AbstractArena loadArena(String name, String mapName) {
        return loadArena(name, mapName, true);
    }

    public AbstractArena loadArena(String name, String mapName, boolean initialize) {
        // try to load it
        try {
            logger.fine(ChatColor.YELLOW + "loading arena " + name);

            // create and load the arena into memory
            AbstractArena arena = createArena(name, mapName, initialize);

            if (arenas.containsValue(arena)) {
                pluginScheduler.ensureSync(() -> plugin.callEvent(new ArenaLoadEvent(arena)));
                logger.config("Loaded arena " + arena.getName());
                return arena;
            }
        } catch (Throwable t) {
            logger.log(Level.SEVERE, ChatColor.RED + "Could not load arena " + name + "!", t);
        }

        return null;
    }

    @Override
    public void onDisable() {
        // stop and save arenas
        arenas.values().forEach(arena -> {
            // try to save them
            try {
                arena.saveAndDestroy();
            } catch (Throwable t) {
                logger.log(Level.SEVERE, ChatColor.RED + "Could not save arena " + arena.getName() + "!", t);
            }
        });
        logger.info("Ended all arenas and saved them");
    }

    @Override
    public void onReload() {
        // stop arenas
        arenas.values().forEach(arena -> {
            try {
                arena.destroy();
            } catch (Throwable t) {
                logger.log(Level.SEVERE, ChatColor.RED + "Could not destroy arena " + arena.getName() + "!", t);
            }
        });
        arenas.clear();
        onEnable();
        plugin.getInjector().getInstance(SignManager.class).onReload();
    }

    @Override
    public String getReloadName() {
        return "arenas";
    }

    public AbstractArena createArena(String name) {
        return createArena(name, name, true);
    }

    public AbstractArena createArena(String name, String mapName, boolean initialize) {
        // create the object
        AbstractArena arena = arenaFactory.create(name, mapName);
        if (initialize) {
            arena.initializeData();
        }

        // store it in the map
        arenas.put(mapName, arena);
        return arena;
    }

    public boolean arenaExists(String arena) {
        return getArena(arena) != null;
    }

    public AbstractArena getArena(String arenaName) {
        return arenas.get(arenaName);
    }

    public Map<String, AbstractArena> getArenas() {
        return arenas;
    }


    public List<AbstractArena> getJoinableArenas(Player player) {
        return getJoinableArenas(player, t -> true);
    }

    public List<AbstractArena> getJoinableArenas(Player player, Predicate<AbstractArena> filter) {
        return arenas.values()
                .stream()
                .filter(arena -> arena.canJoin(player) && arena.getPhase().canJoin())
                .filter(filter)
                .sorted(arenaComparator)
                .collect(Collectors.toList());
    }

    public List<AbstractArena> getAvailableArenas(Player player) {
        return getAvailableArenas(player, t -> true);
    }

    public List<AbstractArena> getAvailableArenas(Player player, Predicate<AbstractArena> filter) {
        return arenas.values()
                .stream()
                .filter(arena -> arena.canJoin(player) || arena.canSpectate(player))
                .filter(filter)
                .sorted(arenaComparator)
                .collect(Collectors.toList());
    }

    public ArenaGroup getGroup(String name) {
        return arenaGroups.get(name.toUpperCase());
    }

    public boolean usesArenaGroups() {
        return arenaGroups.size() > 1;
    }

    public void setLoadMaps(boolean loadMaps) {
        this.loadMaps = loadMaps;
    }

    public Logger getLogger() {
        return logger;
    }
}
