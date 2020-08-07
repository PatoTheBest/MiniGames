package me.patothebest.gamecore.pluginhooks.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.placeholder.EzPlaceholderWrapper;
import me.patothebest.gamecore.placeholder.PlaceHolderManager;
import me.patothebest.gamecore.pluginhooks.PluginHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class PlaceholderAPIHook extends PluginHook {

    private final PlaceHolderManager placeHolderManager;

    @Inject public PlaceholderAPIHook(PlaceHolderManager placeHolderManager) {
        this.placeHolderManager = placeHolderManager;
    }

    @Override
    public void onHook(ConfigurationSection pluginHookSection) {
        placeHolderManager.registerClipsPlaceholders();
    }

    public String replaceString(Player player, String string) {
        if(PlaceholderAPI.containsBracketPlaceholders(string)) {
            string = PlaceholderAPI.setBracketPlaceholders(player, string);
        }

        if(PlaceholderAPI.containsPlaceholders(string)) {
            string = PlaceholderAPI.setPlaceholders(player, string);
        }

        return string;
    }

    @Override
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    public void register(CorePlugin plugin, EzPlaceholderWrapper ezPlaceholderWrapper) {
        ezPlaceholderWrapper.register();
    }
}
