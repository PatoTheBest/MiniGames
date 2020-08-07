package me.patothebest.gamecore.arena.modes.slimerandom;

import com.google.inject.Provider;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.internal.com.flowpowered.nbt.CompoundMap;
import com.grinderwolf.swm.internal.com.flowpowered.nbt.CompoundTag;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.grinderwolf.swm.plugin.SWMPlugin;
import me.patothebest.gamecore.arena.modes.random.RandomArenaGroup;
import me.patothebest.gamecore.pluginhooks.hooks.SlimeWorldManagerHook;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.pluginhooks.PluginHookManager;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.sign.SignManager;
import me.patothebest.gamecore.world.WorldHandler;
import me.patothebest.gamecore.world.infinity.InfinityWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class SlimeRandomArenaGroup extends RandomArenaGroup {

    private final String worldName;
    private final World world;
    private final SlimeWorldManagerHook hook;
    private final SlimeLoader slimeLoader;
    private final CraftSlimeWorld slimeWorld;

    int chunkXDirection = 1;
    int chunkZDirection = 0;
    int segmentLength = 1;

    int chunkX = 0;
    int chunkZ = 0;
    int segmentPassed = 0;

    public SlimeRandomArenaGroup(String groupName, Logger logger, PluginHookManager pluginHookManager, PluginScheduler pluginScheduler, SignManager signManager, ConfigurationSection configurationSection, ArenaManager arenaManager, Provider<WorldHandler> worldHandlerProvider, PlayerManager playerManager) {
        super(groupName, logger, pluginScheduler, signManager, configurationSection, arenaManager, worldHandlerProvider, playerManager);

        hook = pluginHookManager.getHook(SlimeWorldManagerHook.class);
        slimeLoader = hook.getSlimeLoader();

        SlimePropertyMap defaultProperties = hook.createDefaultProperties(World.Environment.NORMAL);
        worldName = (PluginConfig.WORLD_PREFIX + groupName).toLowerCase();
        slimeWorld = new CraftSlimeWorld(slimeLoader, worldName, new HashMap<>(), new CompoundTag("",
                new CompoundMap()), new ArrayList<>(), ((SWMPlugin) hook.getSlimePlugin()).getNms().getWorldVersion(),
                defaultProperties, true, false);
        hook.getSlimePlugin().generateWorld(slimeWorld);
        world = Bukkit.getWorld(worldName);
        world.setAutoSave(true);
        world.setKeepSpawnInMemory(false);
    }

    @Override
    protected AbstractArena loadArena(String arenaName, String worldName) {
        AbstractArena arena = arenaManager.loadArena(arenaName, worldName, false);

        int chunkXOffset = chunkX << 6;
        int chunkZOffset = chunkZ << 6;
        incrementChunks();

        arena.setOffset(new Vector(chunkXOffset << 4, 0, chunkZOffset << 4));
        arena.setArenaWorld(new InfinityWorld(arena, slimeLoader, slimeWorld, world));
        arena.setDisableSaving(true);
        arena.initializeData();

        return arena;
    }

    private void incrementChunks() {
        chunkX += chunkXDirection;
        chunkZ += chunkZDirection;
        ++segmentPassed;

        if (segmentPassed == segmentLength) {
            segmentPassed = 0;

            // 'rotate' directions
            int buffer = chunkXDirection;
            chunkXDirection = -chunkZDirection;
            chunkZDirection = buffer;

            // increase segment length if necessary
            if (chunkZDirection == 0) {
                ++segmentLength;
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        Bukkit.unloadWorld(world, false);
    }
}
