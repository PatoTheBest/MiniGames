package me.patothebest.gamecore.pluginhooks.hooks;

import com.boydti.fawe.bukkit.wrapper.AsyncWorld;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.pluginhooks.PluginHook;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;

import javax.inject.Inject;

public class FastAsyncWorldEditHook extends PluginHook {

    private final CorePlugin plugin;

    @Inject public FastAsyncWorldEditHook(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onHook(ConfigurationSection pluginHookSection) {
        //plugin.setWorldHandler(this);
    }

    @Override
    public String getPluginName() {
        return "FastAsyncWorldEdit";
    }

    public World createWorld(WorldCreator creator) {
        AsyncWorld asyncWorld = AsyncWorld.create(creator);

        // When you are done
        asyncWorld.commit();
        Bukkit.getWorlds().add(asyncWorld);

        return asyncWorld.getBukkitWorld();
    }

    public boolean hasAsyncSupport() {
        return true;
    }
}
