package me.patothebest.gamecore.pluginhooks;

public class PluginHookProvider {

    private final Class<? extends PluginHook> pluginHookClass;

    public PluginHookProvider(Class<? extends PluginHook> pluginHookClass) {
        this.pluginHookClass = pluginHookClass;
    }

    public Class<? extends PluginHook> getPluginHookClass() {
        return pluginHookClass;
    }
}
