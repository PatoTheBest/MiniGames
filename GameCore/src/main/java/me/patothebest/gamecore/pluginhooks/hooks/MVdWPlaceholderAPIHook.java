package me.patothebest.gamecore.pluginhooks.hooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.placeholder.PlaceHolderManager;
import me.patothebest.gamecore.pluginhooks.PluginHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MVdWPlaceholderAPIHook extends PluginHook {

    private final CorePlugin plugin;
    private final PlaceHolderManager placeHolderManager;

    @Inject public MVdWPlaceholderAPIHook(CorePlugin plugin, PlaceHolderManager placeHolderManager) {
        this.plugin = plugin;
        this.placeHolderManager = placeHolderManager;
    }

    @Override
    public void onHook(ConfigurationSection pluginHookSection) {
        placeHolderManager.registerMDWPlaceholder();
    }

    public void registerPlaceholder(PlaceHolder placeHolder) {
        PlaceholderAPI.registerPlaceholder(plugin, PluginConfig.PLACEHOLDER_PREFIX + "_" + placeHolder.getPlaceholderName(), placeholderReplaceEvent -> placeHolder.replace(placeholderReplaceEvent.getPlayer(), null));
    }

    @Override
    public String getPluginName() {
        return "MVdWPlaceholderAPI";
    }

    public String replace(Player player, String string) {
        return PlaceholderAPI.replacePlaceholders(player, string);
    }
}