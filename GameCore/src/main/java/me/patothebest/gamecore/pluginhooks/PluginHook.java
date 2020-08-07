package me.patothebest.gamecore.pluginhooks;

import org.bukkit.configuration.ConfigurationSection;

public abstract class PluginHook {

    private boolean lodaded = false;

    public final void load(ConfigurationSection pluginHookSection) {
        onHook(pluginHookSection);
        lodaded = true;
    }

    protected abstract void onHook(ConfigurationSection pluginHookSection);

    public abstract String getPluginName();

    public final boolean isLodaded() {
        return lodaded;
    }
}