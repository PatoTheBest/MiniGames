package me.patothebest.gamecore.pluginhooks.hooks;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.pluginhooks.PluginHook;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class HolographicDisplaysHook extends PluginHook {

    private final CorePlugin plugin;

    @Inject private HolographicDisplaysHook(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void onHook(ConfigurationSection pluginHookSection) { }

    @Override
    public String getPluginName() {
        return "HolographicDisplays";
    }

    public Hologram createHologram(Location location) {
        return HologramsAPI.createHologram(plugin, location);
    }
}
