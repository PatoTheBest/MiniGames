package me.patothebest.gamecore.pluginhooks.hooks;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.pluginhooks.hooks.worldedit.WorldEdit6SelectionManager;
import me.patothebest.gamecore.pluginhooks.hooks.worldedit.WorldEdit7SelectionManager;
import me.patothebest.gamecore.logger.InjectParentLogger;
import me.patothebest.gamecore.pluginhooks.PluginHook;
import me.patothebest.gamecore.pluginhooks.PluginHookManager;
import me.patothebest.gamecore.selection.SelectionManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import javax.inject.Inject;
import java.util.logging.Logger;

public class WorldEditHook extends PluginHook {

    private final CorePlugin plugin;
    @InjectParentLogger(parent = PluginHookManager.class) private Logger logger;

    @Inject public WorldEditHook(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onHook(ConfigurationSection pluginHookSection) {
        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        CoreLang.SELECT_AN_AREA.transferMessage(CoreLang.SELECT_AN_AREA_WORLDEDIT);
        SelectionManager selectionManager;
        try {
            selectionManager = new WorldEdit6SelectionManager(worldEdit);
            logger.info("Detected WorldEdit version 7!");
        } catch (Throwable t) {
            selectionManager = new WorldEdit7SelectionManager(worldEdit);
            logger.info("Detected WorldEdit version 6!");
        }
        plugin.setSelectionManager(selectionManager);
    }

    @Override
    public String getPluginName() {
        return "WorldEdit";
    }

}
