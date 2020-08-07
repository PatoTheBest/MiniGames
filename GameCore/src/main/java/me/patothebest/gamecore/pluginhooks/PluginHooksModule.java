package me.patothebest.gamecore.pluginhooks;

import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.pluginhooks.hooks.FastAsyncWorldEditHook;
import me.patothebest.gamecore.pluginhooks.hooks.FeatherBoardHook;
import me.patothebest.gamecore.pluginhooks.hooks.MVdWPlaceholderAPIHook;
import me.patothebest.gamecore.pluginhooks.hooks.PlaceholderAPIHook;
import me.patothebest.gamecore.pluginhooks.hooks.SlimeWorldManagerHook;
import me.patothebest.gamecore.pluginhooks.hooks.VaultHook;
import me.patothebest.gamecore.pluginhooks.hooks.WorldEditHook;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.pluginhooks.hooks.CitizensPluginHook;
import me.patothebest.gamecore.pluginhooks.hooks.HolographicDisplaysHook;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class PluginHooksModule extends AbstractBukkitModule<CorePlugin> {

    public PluginHooksModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        MapBinder<String, PluginHookProvider> pluginHooks = MapBinder.newMapBinder(binder(), String.class, PluginHookProvider.class);

        registerHook("MVdWPlaceholderAPI", MVdWPlaceholderAPIHook.class, pluginHooks);
        registerHook("PlaceholderAPI", PlaceholderAPIHook.class, pluginHooks);
        registerHook("WorldEdit", WorldEditHook.class, pluginHooks);
        registerHook("FastAsyncWorldEdit", FastAsyncWorldEditHook.class, pluginHooks);
        registerHook("SlimeWorldManager", SlimeWorldManagerHook.class, pluginHooks);
        registerHook("Vault", VaultHook.class, pluginHooks);
        registerHook("FeatherBoard", FeatherBoardHook.class, pluginHooks);
        registerHook("Citizens", CitizensPluginHook.class, pluginHooks);
        registerHook("HolographicDisplays", HolographicDisplaysHook.class, pluginHooks);

        registerModule(PluginHookManager.class);
    }

    private void registerHook(String pluginHookName, Class<? extends PluginHook> pluginHookClass, MapBinder<String, PluginHookProvider> pluginHooks) {
        pluginHooks.addBinding(pluginHookName).toInstance(new PluginHookProvider(pluginHookClass));
    }

    @Provides private Economy getEconomy(PluginHookManager pluginHookManager) {
        if(!pluginHookManager.isHookLoaded(VaultHook.class)) {
           return null;
        }

        return pluginHookManager.getHook(VaultHook.class).getEconomy();
    }

    @Provides private Chat getChat(PluginHookManager pluginHookManager) {
        if(!pluginHookManager.isHookLoaded(VaultHook.class)) {
            return null;
        }

        return pluginHookManager.getHook(VaultHook.class).getChat();
    }

    @Provides private Permission getPermissions(PluginHookManager pluginHookManager) {
        if(!pluginHookManager.isHookLoaded(VaultHook.class)) {
            return null;
        }

        return pluginHookManager.getHook(VaultHook.class).getPermissions();
    }
}