package me.patothebest.gamecore.world;

import io.papermc.lib.PaperLib;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.option.options.EnvironmentOption;
import me.patothebest.gamecore.arena.option.options.TimeOfDayOption;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;

public class ArenaWorld {

    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //

    // the blank chunk generator
    // this generator will create a world with only one
    // block there is no need to create one per class,
    // so that is why it is static
    final static BlankChunkGenerator chunkGenerator = new BlankChunkGenerator();

    // the directory where all the world zip files
    // will be stored
    public final static File WORLD_DIRECTORY = new File(Utils.PLUGIN_DIR, "worlds" + File.separatorChar);

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final File worldZipFile;
    private final File tempWorld;
    private final String worldName;
    protected final AbstractArena arena;
    private World world;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public ArenaWorld(AbstractArena arena) {
        this(arena, arena.getWorldName());
    }

    public ArenaWorld(AbstractArena arena, String arenaWorldName) {
        this.arena = arena;
        // If the world directory doesn't exist...
        if(!WORLD_DIRECTORY.exists()) {
            // ...create the directory
            WORLD_DIRECTORY.mkdirs();
        }

        // the world zip file
        this.worldZipFile = new File(WORLD_DIRECTORY, arena.getName() + ".zip");

        // the world directory
        this.worldName = PluginConfig.WORLD_PREFIX + arenaWorldName;
        this.tempWorld = new File(worldName + File.separatorChar);
    }

    // -------------------------------------------- //
    // PUBLIC METHODS
    // -------------------------------------------- //

    public boolean decompressWorld() {
        // unzip the world
        return arena.getWorldHandler().get().decompressWorld(arena, worldZipFile, tempWorld);
    }

    public void loadWorld(boolean permanentWorld) {
        // WorldCreator
        World.Environment environment = arena.getOption(EnvironmentOption.class).getEnvironment();

        if (tempWorld.exists()) {
            if (environment != World.Environment.NORMAL) {
                String dimFolderName = "DIM" + environment.getId();
                String otherDim = "DIM" + (environment.getId() == -1 ? "1" : "-1");
                File dimFolder = new File(tempWorld, dimFolderName);
                File otherDimFolder = new File(tempWorld, otherDim);
                if (!dimFolder.exists()) {
                    dimFolder.mkdir();

                    if (otherDimFolder.exists()) {
                        try {
                            System.out.println(arena.getName() + ": Detected old dimension " + otherDim + ". Moving " + otherDim + " to " + dimFolderName);
                            Files.move(new File(otherDimFolder, "poi").toPath(), new File(dimFolder, "poi").toPath(), StandardCopyOption.REPLACE_EXISTING);
                            Files.move(new File(otherDimFolder, "region").toPath(), new File(dimFolder, "region").toPath(), StandardCopyOption.REPLACE_EXISTING);
                            Utils.deleteFolder(otherDimFolder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (new File(tempWorld, "region").exists()) {
                            try {
                                System.out.println(arena.getName() + ": Detected old dimension NORMAL. Moving NORMAL to " + dimFolderName);
                                Files.move(new File(tempWorld, "poi").toPath(), new File(dimFolder, "poi").toPath(), StandardCopyOption.REPLACE_EXISTING);
                                Files.move(new File(tempWorld, "region").toPath(), new File(dimFolder, "region").toPath(), StandardCopyOption.REPLACE_EXISTING);
                                Utils.deleteFolder(new File(tempWorld, "poi"));
                                Utils.deleteFolder(new File(tempWorld, "region"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                if (!new File(tempWorld, "region").exists()) {
                    for (int dim : new int[]{-1, 1}) {
                        File dimFolder = new File(tempWorld, "DIM" + dim);
                        if (dimFolder.exists()) {
                            try {
                                System.out.println(arena.getName() + ": Detected old dimension DIM" + dim + ". Moving DIM" + dim + " to NORMAL");
                                Files.move(new File(tempWorld, "poi").toPath(), new File(dimFolder, "poi").toPath(), StandardCopyOption.REPLACE_EXISTING);
                                Files.move(new File(tempWorld, "region").toPath(), new File(dimFolder, "region").toPath(), StandardCopyOption.REPLACE_EXISTING);
                                Utils.deleteFolder(dimFolder);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        // create the world
        if (!permanentWorld) {
            try {
                world = arena.getWorldHandler().get().loadWorld(arena, worldName, environment).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Could not load world " + worldName + "!", e);
            }
        } else {
            WorldCreator worldCreator = new WorldCreator(worldName);
            worldCreator.environment(environment);
            worldCreator.generateStructures(false);
            worldCreator.generator(chunkGenerator);
            world = Bukkit.createWorld(worldCreator);
        }

        if (environment == World.Environment.THE_END) {
            if (!Bukkit.getVersion().contains("1.8")) {
                // In 1.9 a dragon fight system was implemented, this allowed portals to be automatically created
                // The problem is that the portal spawns with the dragon and we don't want that, so we must remove
                // the EnderDragonBattle object from the world provider
                Object worldServer = Utils.invokeMethod(world, "getHandle", new Class[]{});

                if (Bukkit.getVersion().contains("1.16")) {
                    try {
                        Utils.setFinalField(Utils.getFieldOrNull(Utils.getNMSClass("WorldServer"), "dragonBattle"), worldServer, null);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    Object worldProvider = Utils.invokeMethod(worldServer, Utils.getMethodNotDeclaredValue(worldServer.getClass(), "getWorldProvider"), new Class[]{});

                    try {
                        Utils.setFinalField(Utils.getFieldOrNull(Utils.getNMSClass("WorldProviderTheEnd"), "g"), worldProvider, null);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

       arena.getPluginScheduler().ensureSync(() -> {
           // set the time to day
           world.setTime(arena.getOption(TimeOfDayOption.class).getTime().getTick());

           // no rain on the world
           world.setThundering(false);
           world.setStorm(false);
           world.setKeepSpawnInMemory(false);
           world.setAutoSave(permanentWorld);
           world.setWeatherDuration(Integer.MAX_VALUE);

           // default game rules
           world.setGameRuleValue("doMobSpawning", "false");
           world.setGameRuleValue("mobGriefing", "true");
           world.setGameRuleValue("doDaylightCycle", "false");

           // difficulty hard
           world.setDifficulty(Difficulty.HARD);

           // set the world spawn location
           world.setSpawnLocation(0, 101, 0);
       });

        if (arena.getArea() != null && !permanentWorld) {
            arena.getPluginScheduler().runTaskAsynchronously(() -> {
                if (PaperLib.isPaper()) {
                    for (Location location : arena.getArea().getChunkLocations()) {
                        try {
                            PaperLib.getChunkAtAsync(location).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void unloadWorld(boolean save) {
        if(world == null) {
            return;
        }

        world.getPlayers().forEach(player -> {
            // if the player is dead...
            if(player.isDead()) {
                try {
                    // ...try to respawn the player
                    player.spigot().respawn();
                } catch(Throwable t) {
                    // can't respawn the player (bukkit) so we kick him
                    player.kickPlayer("Regenerating arena");
                }
            }

            // teleport the player out of the world so we can unload it
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        });

        // attempt to unload the world
        boolean unloaded = save ? Bukkit.unloadWorld(world, save) : arena.getWorldHandler().get().unloadWorld(world);
        world = null;

        // If the world could not be unloaded...
       if(!unloaded) {
           // ...throw an exception
           throw new RuntimeException("Could not unload world " + world.getName());
       }
    }

    public void deleteWorld() {
        arena.getWorldHandler().get().deleteWorld(tempWorld);
    }

    // -------------------------------------------- //
    // GETTERS
    // -------------------------------------------- //

    public World getWorld() {
        return world;
    }

    public File getTempWorld() {
        return tempWorld;
    }

    public File getWorldZipFile() {
        return worldZipFile;
    }

    @Override
    public String toString() {
        return "ArenaWorld{" +
                "worldZipFile=" + worldZipFile +
                ", tempWorld=" + tempWorld +
                ", worldName='" + worldName + '\'' +
                ", world=" + world +
                '}';
    }
}
