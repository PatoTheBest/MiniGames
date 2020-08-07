package me.patothebest.gamecore.feature.features.kits;

import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.feature.AbstractFeature;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AntiShareKitFeature extends AbstractFeature {

    @EventHandler
    public void onDeath(CombatDeathEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        event.getDrops().removeIf(itemStack -> {
            if(!itemStack.hasItemMeta()) {
                return false;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta.getLore() == null || itemMeta.getLore().isEmpty()) {
                return false;
            }

            for (String s : itemMeta.getLore()) {
                if(s.contains(ChatColor.YELLOW + "Kit ")) {
                    return true;
                }
            }

            return false;
        });
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        if(!event.getItemDrop().getItemStack().hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = event.getItemDrop().getItemStack().getItemMeta();
        if(itemMeta.getLore() == null || itemMeta.getLore().isEmpty()) {
            return;
        }

        for (String s : itemMeta.getLore()) {
            if(s.contains(ChatColor.YELLOW + "Kit ")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMoveItem(InventoryClickEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        if (event.getInventory().getType() == InventoryType.PLAYER || event.getInventory().getType() == InventoryType.CRAFTING) {
            return;
        }

        if(!event.getAction().name().contains("HOTBAR") && !event.getAction().name().contains("PLACE") && !event.getAction().name().contains("SWAP") && event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (event.getAction().name().contains("HOTBAR")) {
            item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
        } else if(event.getAction().name().contains("PLACE") || event.getAction().name().contains("SWAP")) {
            item = event.getCursor();
        }

        if(event.getRawSlot() > event.getView().getTopInventory().getSize()-1 && event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return;
        }

        if(item == null) {
            return;
        }

        if(!item.hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta.getLore() == null || itemMeta.getLore().isEmpty()) {
            return;
        }

        for (String s : itemMeta.getLore()) {
            if(s.contains(ChatColor.YELLOW + "Kit ")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDragItem(InventoryDragEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        if (event.getInventory().getType() == InventoryType.PLAYER || event.getInventory().getType() == InventoryType.CRAFTING) {
            return;
        }

        ItemStack item = event.getOldCursor();

        if(event.getRawSlots().stream().filter(integer -> integer < event.getView().getTopInventory().getSize()-1).findFirst().orElse(null) == null) {
            return;
        }

        if(item == null) {
            return;
        }

        if(!item.hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta.getLore() == null || itemMeta.getLore().isEmpty()) {
            return;
        }

        for (String s : itemMeta.getLore()) {
            if(s.contains(ChatColor.YELLOW + "Kit ")) {
                event.setCancelled(true);
            }
        }
    }
}