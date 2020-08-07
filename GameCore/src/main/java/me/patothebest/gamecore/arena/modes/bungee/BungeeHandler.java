package me.patothebest.gamecore.arena.modes.bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModuleName;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@ModuleName("Bungee Handler")
public class BungeeHandler implements ActivableModule {

    private final CorePlugin corePlugin;

    @Inject private BungeeHandler(CorePlugin corePlugin) {
        this.corePlugin = corePlugin;
    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(corePlugin, "BungeeCord");
    }

    public void sendPlayer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(corePlugin, "BungeeCord", out.toByteArray());
    }
}
