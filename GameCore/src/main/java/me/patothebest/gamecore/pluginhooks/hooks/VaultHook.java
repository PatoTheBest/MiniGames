package me.patothebest.gamecore.pluginhooks.hooks;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.pluginhooks.PluginHook;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.inject.Inject;

public class VaultHook extends PluginHook implements ListenerModule {

    private final CorePlugin plugin;

    // Vault objects
    private Economy economy;
    private Permission permissions;
    private Chat chat;

    @Inject public VaultHook(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onHook(ConfigurationSection pluginHookSection) {
        RegisteredServiceProvider<Economy> economyRegisteredServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if(economyRegisteredServiceProvider != null && pluginHookSection.getBoolean("economy")) {
          economy = economyRegisteredServiceProvider.getProvider();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }

        RegisteredServiceProvider<Permission> permissionRegisteredServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if(permissionRegisteredServiceProvider != null && pluginHookSection.getBoolean("permissions")) {
            permissions = permissionRegisteredServiceProvider.getProvider();
        }

        RegisteredServiceProvider<Chat> chatRegisteredServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if(chatRegisteredServiceProvider != null && pluginHookSection.getBoolean("chat")) {
            chat = chatRegisteredServiceProvider.getProvider();
        }
    }

    public Economy getEconomy() {
        return economy;
    }

    public Permission getPermissions() {
        return permissions;
    }

    public Chat getChat() {
        return chat;
    }

    @Override
    public String getPluginName() {
        return "Vault";
    }
}