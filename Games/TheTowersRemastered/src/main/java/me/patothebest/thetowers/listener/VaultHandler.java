package me.patothebest.thetowers.listener;

import com.google.inject.Inject;
import me.patothebest.thetowers.file.Config;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.player.PlayerManager;
import net.megaplanet.thetowers.api.events.PlayerScoreEvent;
import org.bukkit.event.EventHandler;

public class VaultHandler implements ListenerModule {

    // Vault objects
    private final Config config;
    private final PlayerManager playerManager;

    @Inject private VaultHandler(Config config, PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.config = config;
    }

    @EventHandler
    public void onScore(PlayerScoreEvent event) {
        playerManager.getPlayer(event.getPlayer()).giveMoney(config.getMoneyToScore());
    }
}