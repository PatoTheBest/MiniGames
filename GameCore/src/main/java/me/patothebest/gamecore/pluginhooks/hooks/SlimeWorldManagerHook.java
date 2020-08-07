package me.patothebest.gamecore.pluginhooks.hooks;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.InvalidWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.exceptions.WorldLoadedException;
import com.grinderwolf.swm.api.exceptions.WorldTooBigException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import me.patothebest.gamecore.event.arena.ArenaDisableEvent;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.pluginhooks.PluginHook;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.world.WorldHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.NotDirectoryException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SlimeWorldManagerHook extends PluginHook implements WorldHandler, Listener {

    private static final FilenameFilter WORLD_FILE_FILTER = (dir, name) -> name.endsWith(".slime");
    private final static File SLIME_WORLD_DIRECTORY = new File(Utils.PLUGIN_DIR, "slime-worlds");
    private final CorePlugin corePlugin;
    private final PluginScheduler pluginScheduler;
    private SlimePlugin slimePlugin;
    private FileLoader slimeLoader;

    @Inject public SlimeWorldManagerHook(CorePlugin plugin, PluginScheduler pluginScheduler) {
        this.corePlugin = plugin;
        this.pluginScheduler = pluginScheduler;
    }

    @Override
    public void onHook(ConfigurationSection pluginHookSection) {
        if (!SLIME_WORLD_DIRECTORY.exists()) {
            SLIME_WORLD_DIRECTORY.mkdirs();
        }

        corePlugin.setWorldHandler(this);
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        slimeLoader = new FileLoader(SLIME_WORLD_DIRECTORY);
        slimePlugin.registerLoader(PluginConfig.PLUGIN_NAME, slimeLoader);
        corePlugin.registerListener(this);
    }

    @Override
    public String getPluginName() {
        return "SlimeWorldManager";
    }

    @EventHandler
    public void onDisable(ArenaDisableEvent event) {
        try {
            slimeLoader.deleteWorld(event.getArena().getName());
        } catch (UnknownWorldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean decompressWorld(AbstractArena arena, File worldZipFile, File tempWorld) {
        if (slimeLoader.worldExists(arena.getName())) {
            return true;
        }

        Utils.unZip(worldZipFile.getPath(), tempWorld.getPath());
        new File(tempWorld, "uid.dat").delete();
        try {
            slimePlugin.importWorld(tempWorld, arena.getName(), slimeLoader);
            Utils.deleteFolder(tempWorld);
            return true;
        } catch (WorldAlreadyExistsException | InvalidWorldException | WorldLoadedException | WorldTooBigException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void deleteWorld(File tempWorld) {
        File file = new File("temp_" + tempWorld.getName());
        if (file.exists()) {
            Utils.deleteFolder(file);
        }
    }

    @Override
    public boolean unloadWorld(World world) {
        return Bukkit.unloadWorld(world, false);
    }

    @Override
    /* Note that this method should be called asynchronously */
    public synchronized CompletableFuture<World> loadWorld(AbstractArena arena, String worldName, World.Environment environment) {
        CompletableFuture<World> completableFuture = new CompletableFuture<>();

        try {
            SlimePropertyMap slimePropertyMap = createDefaultProperties(environment);
            slimeLoader.realWorldNames.put(worldName.toLowerCase(), arena.getName());
            SlimeWorld world = slimePlugin.loadWorld(slimeLoader, worldName.toLowerCase(), true, slimePropertyMap);

            pluginScheduler.ensureSync(() -> {
                try {
                    // This method must be called synchronously
                    slimePlugin.generateWorld(world);
                    completableFuture.complete(Bukkit.getWorld(worldName.toLowerCase()));
                } catch (Throwable t) {
                    completableFuture.completeExceptionally(t);
                }
            });

        } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException ex) {
            completableFuture.completeExceptionally(ex);
        }
        return completableFuture;
    }

    public SlimePropertyMap createDefaultProperties(World.Environment environment) {
        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        slimePropertyMap.setString(SlimeProperties.DIFFICULTY, "hard");
        slimePropertyMap.setInt(SlimeProperties.SPAWN_X, 0);
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Y, 101);
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Z, 0);
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        slimePropertyMap.setBoolean(SlimeProperties.PVP, true);
        slimePropertyMap.setString(SlimeProperties.ENVIRONMENT, environment.name().toLowerCase());
        return slimePropertyMap;
    }

    @Override
    public boolean hasAsyncSupport() {
        return true;
    }

    public static class FileLoader implements SlimeLoader {

        private final Map<String, RandomAccessFile> worldFiles = new HashMap<>();
        private final Map<String, String> realWorldNames = new HashMap<>();
        private final File worldDir;

        public FileLoader(File worldDir) {
            this.worldDir = worldDir;
            worldDir.mkdirs();
        }

        @Override
        // All worlds will be readOnly
        public byte[] loadWorld(String worldName, boolean readOnly) throws UnknownWorldException, IOException {
            String realWorldName = realWorldNames.getOrDefault(worldName, worldName);

            if (!worldExists(realWorldName)) {
                throw new UnknownWorldException(realWorldName);
            }

            RandomAccessFile file = worldFiles.computeIfAbsent(worldName, (world) -> {

                try {
                    return new RandomAccessFile(new File(worldDir, realWorldName + ".slime"), "rw");
                } catch (FileNotFoundException ex) {
                    return null; // This is never going to happen as we've just checked if the world exists
                }

            });

            if (file.length() > Integer.MAX_VALUE) {
                throw new IndexOutOfBoundsException("World is too big!");
            }

            byte[] serializedWorld = new byte[(int) file.length()];
            file.seek(0); // Make sure we're at the start of the file
            file.readFully(serializedWorld);

            return serializedWorld;
        }

        @Override
        public boolean worldExists(String worldName) {
            return new File(worldDir, realWorldNames.getOrDefault(worldName, worldName) + ".slime").exists();
        }

        @Override
        public List<String> listWorlds() throws NotDirectoryException {
            String[] worlds = worldDir.list(WORLD_FILE_FILTER);

            if(worlds == null) {
                throw new NotDirectoryException(worldDir.getPath());
            }

            return Arrays.stream(worlds).map((c) -> c.substring(0, c.length() - 6)).collect(Collectors.toList());
        }

        @Override
        public void saveWorld(String worldName, byte[] serializedWorld, boolean lock) throws IOException {
            RandomAccessFile worldFile = worldFiles.get(worldName);
            boolean tempFile = worldFile == null;

            if (tempFile) {
                worldFile = new RandomAccessFile(new File(worldDir, worldName + ".slime"), "rw");
            }

            worldFile.seek(0); // Make sure we're at the start of the file
            worldFile.setLength(0); // Delete old data
            worldFile.write(serializedWorld);

            if (tempFile) {
                worldFile.close();
            }
        }

        @Override
        public void unlockWorld(String worldName) throws UnknownWorldException {
            if (!worldExists(worldName)) {
                throw new UnknownWorldException(worldName);
            }

            worldFiles.remove(worldName);
            realWorldNames.remove(worldName);
        }

        @Override
        public boolean isWorldLocked(String worldName) {
            return false;
        }

        @Override
        public void deleteWorld(String worldName) throws UnknownWorldException {
            if (!worldExists(worldName)) {
                throw new UnknownWorldException(worldName);
            }

            new File(worldDir, worldName + ".slime").delete();
        }
    }

    public SlimePlugin getSlimePlugin() {
        return slimePlugin;
    }

    public FileLoader getSlimeLoader() {
        return slimeLoader;
    }
}
