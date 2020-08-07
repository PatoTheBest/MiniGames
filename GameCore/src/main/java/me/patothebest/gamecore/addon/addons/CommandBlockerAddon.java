package me.patothebest.gamecore.addon.addons;

import com.google.inject.Inject;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.addon.Addon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandBlockerAddon extends Addon {

    protected final List<String> commands = new ArrayList<>();
    private final PlayerManager playerManager;

    @Inject private CommandBlockerAddon(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void configure(ConfigurationSection addonConfigSection) {
        commands.addAll(addonConfigSection.getStringList("allowed-commands"));
    }

    @EventHandler
    private void blockCommands(final PlayerCommandPreprocessEvent event) {
        if(getPlayer(event.getPlayer()) == null) {
            return;
        }

        if(!getPlayer(event.getPlayer()).isInArena()) {
            return;
        }

        if(event.getPlayer().hasPermission(Permission.ADMIN.getBukkitPermission())) {
            return;
        }

        if(commands.stream().filter(command -> event.getMessage().toLowerCase().startsWith("/" + command.toLowerCase())).findAny().orElse(null) != null) {
            return;
        }

        event.setCancelled(true);
        CoreLang.YOU_CANNOT_EXECUTE_COMMANDS.sendMessage(event.getPlayer());
    }

    private IPlayer getPlayer(Player player) {
        return playerManager.getPlayer(player);
    }


    @Override
    public String getConfigPath() {
        return "command-blocker";
    }
}
