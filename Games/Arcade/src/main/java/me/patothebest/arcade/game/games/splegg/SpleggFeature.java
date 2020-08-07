package me.patothebest.arcade.game.games.splegg;

import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class SpleggFeature extends AbstractFeature {

    private static final ItemStack SPLEGG_ITEM = new ItemStackBuilder().material(Material.IRON_SHOVEL).name(ChatColor.GREEN + "Splegger").lore(ChatColor.GREEN + "Click derecho para disparar huevos");

    @Override
    public void initializeFeature() {
        super.initializeFeature();
        for (Player player : getArena().getPlayers()) {
            player.getInventory().setItem(0, SPLEGG_ITEM);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player cur = event.getPlayer();
        final Vector offset = cur.getLocation().getDirection();
        if (offset.getY() < 0.0) {
            offset.setY(0);
        }

        final Egg egg = cur.getWorld().spawn(cur.getLocation().add(0.0, 0.5, 0.0).add(offset), Egg.class);
        egg.setVelocity(cur.getLocation().getDirection().add(new Vector(0.0, 0.2, 0.0)));
        egg.setShooter(cur);
        cur.getWorld().playSound(cur.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.5f, 1.0f);
    }

    @EventHandler
    public void onEggLand(ProjectileHitEvent event) {
        if(!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        if (!isPlayingInArena(player)) {
            return;
        }

        Block hitBlock = null;
        BlockIterator blockIterator = new BlockIterator(event.getEntity().getLocation().getWorld(), event.getEntity().getLocation().toVector(), event.getEntity().getVelocity().normalize(), 0.0D, 4);
        while (blockIterator.hasNext()) {
            hitBlock = blockIterator.next();
            if (hitBlock.getType() != Material.AIR.parseMaterial()) {
                break;
            }
        }

        event.getEntity().remove();

        if (hitBlock == null || hitBlock.getType() == Material.AIR.parseMaterial()) {
            return;
        }
//        hitBlock.getWorld().playEffect(hitBlock.getLocation(), Effect.STEP_SOUND, hitBlock.getTypeId());
        hitBlock.setType(Material.AIR.parseMaterial());
    }
}