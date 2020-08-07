package me.patothebest.gamecore.cosmetics.victoryeffects.types;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.patothebest.gamecore.cosmetics.victoryeffects.RepeatingVictoryEffect;
import me.patothebest.gamecore.itemstack.StainedClay;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.Sounds;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RainDiscoVictoryEffect extends RepeatingVictoryEffect {

    private final List<FallingBlock> blocks = new ArrayList<>();
    @Inject private Provider<NMS> nmsProvider;

    @EventHandler
    public void onEntityChangeBlock(final EntityChangeBlockEvent e) {
        if (!(e.getEntity() instanceof FallingBlock)) {
            return;
        }

        if (this.blocks.contains(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @Override
    public void displayEffect(IPlayer player) {
        final Location loc = player.getLocation().add(0.5, 1.5, 0.5);
        for (int i = 0; i < 15; ++i) {
            final FallingBlock block = nmsProvider.get().spawnFallingBlock(loc, StainedClay.getRandomMaterial());
            block.setDropItem(false);
            block.setVelocity(new Vector(Utils.randDouble(-0.5, 0.5), Utils.getRandom().nextDouble() + 0.5, Utils.randDouble(-0.5, 0.5)));
            player.playSound(Sounds.ENTITY_ITEM_PICKUP);
            this.blocks.add(block);
        }
    }

    @Override
    public long getPeriod() {
        return 10;
    }
}
