package me.patothebest.gamecore.addon.addons;

import com.google.inject.Inject;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.addon.Addon;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class TeleportOnJoinAddon extends Addon {

    private final CoreConfig coreConfig;

    @Inject private TeleportOnJoinAddon(CoreConfig coreConfig) {
        this.coreConfig = coreConfig;
    }

    @Override
    public void configure(ConfigurationSection addonConfigSection) { }

    @EventHandler
    public void onJoin(PlayerSpawnLocationEvent event) {
        if(coreConfig.isUseMainLobby() && coreConfig.getMainLobby() != null) {
            event.setSpawnLocation(coreConfig.getMainLobby());
            return;
        }

        event.setSpawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
    }

    @Override
    public String getConfigPath() {
        return "join-teleport";
    }
}
