package me.patothebest.gamecore.feature.features.celebration;

import com.google.inject.Inject;
import me.patothebest.gamecore.cosmetics.victoryeffects.VictoryEffectItem;
import me.patothebest.gamecore.cosmetics.victoryeffects.VictoryManager;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;

public class CelebrationFeature extends AbstractFeature {

    private final PlayerManager playerManager;
    private final VictoryManager victoryManager;

    @Inject private CelebrationFeature(PlayerManager playerManager, VictoryManager victoryManager) {
        this.playerManager = playerManager;
        this.victoryManager = victoryManager;
    }

    @Override
    public void initializeFeature() {
        for (Player player : arena.getPlayers()) {
            player.setAllowFlight(true);
            player.setFlying(true);

            IPlayer player1 = playerManager.getPlayer(player);
            VictoryEffectItem victoryEffect = player1.getSelectedItemOrDefault(VictoryEffectItem.class, victoryManager.getDefaultItem());
            if (victoryEffect != null) {
                victoryEffect.display(player1);
            }
        }
    }
}
