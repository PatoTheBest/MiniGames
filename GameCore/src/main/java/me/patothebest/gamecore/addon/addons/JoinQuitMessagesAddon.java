package me.patothebest.gamecore.addon.addons;

import me.patothebest.gamecore.addon.Addon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitMessagesAddon extends Addon {

    private boolean hideJoinMessages;
    private boolean hideQuitMessages;

    @Override
    public void configure(ConfigurationSection addonConfigSection) {
        hideJoinMessages = addonConfigSection.getBoolean("hide-join-messages");
        hideQuitMessages = addonConfigSection.getBoolean("hide-quit-messages");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(hideJoinMessages) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent event) {
        if(hideQuitMessages) {
            event.setQuitMessage(null);
        }
    }

    @Override
    public String getConfigPath() {
        return "join-quit-messages";
    }
}
