package me.patothebest.gamecore.feature.features.other;

import com.google.inject.Inject;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.event.player.PlayerThrowTNTEvent;
import me.patothebest.gamecore.feature.AbstractFeature;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ThrowTNTFeature extends AbstractFeature {

    private final EventRegistry eventRegistry;

    @Inject private ThrowTNTFeature(EventRegistry eventRegistry) {
        this.eventRegistry = eventRegistry;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (!isPlayingInArena(event)) {
            return;
        }

        Player player = event.getPlayer();
        if(event.getPlayer().getItemInHand().getType() != Material.TNT) {
           return;
        }

        PlayerThrowTNTEvent throwTNTEvent = eventRegistry.callEvent(new PlayerThrowTNTEvent(player, arena));
        if (throwTNTEvent.isCancelled()) {
            return;
        }

        player.getInventory().removeItem(new ItemStack(Material.TNT, 1));
        TNTPrimed tnt = player.getWorld().spawn(player.getLocation(), TNTPrimed.class);
        tnt.setFuseTicks(40);
        tnt.setVelocity(player.getLocation().getDirection().normalize().multiply(0.5));
        event.setCancelled(true);
    }
}
