package me.patothebest.gamecore.addon.addons;

import com.google.inject.Inject;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.file.InventoryItem;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.addon.Addon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class JoinItemsAddon extends Addon {


    private final Map<Integer, InventoryItem> inventoryItems = new HashMap<>();
    private final PlayerManager playerManager;

    @Inject private JoinItemsAddon(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void configure(ConfigurationSection addonConfigSection) {
        // read all items from config
        addonConfigSection.getConfigurationSection("items").getKeys(false).forEach(s -> {
            if (addonConfigSection.getConfigurationSection("items." + s) == null) {
                return;
            }

            // create the item by using the Map<String, Object>
            // constructor and add it to the map
            InventoryItem inventoryItem = new InventoryItem(s, addonConfigSection.getConfigurationSection("items." + s).getValues(true));
            inventoryItems.put(inventoryItem.getSlot(), inventoryItem);
        });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (playerManager.getPlayer(event.getPlayer()) == null) {
            return;
        }

        if (playerManager.getPlayer(event.getPlayer()).isInArena()) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        int heldItemSlot = event.getPlayer().getInventory().getHeldItemSlot();
        InventoryItem inventoryItem = inventoryItems.get(heldItemSlot);

        if (inventoryItem == null) {
            return;
        }

        if (inventoryItem.getCommand() == null) {
            return;
        }

        event.getPlayer().chat("/" + inventoryItem.getCommand());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        IPlayer player = playerManager.getPlayer((Player) event.getWhoClicked());

        if (player == null) {
            return;
        }

        if (player.isInArena()) {
            return;
        }

        if(event.getCurrentItem() == null) {
            return;
        }

        if(event.getClickedInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        int clickedSlot = event.getSlot();
        InventoryItem inventoryItem = inventoryItems.get(clickedSlot);

        if (inventoryItem == null) {
            return;
        }

        if (inventoryItem.getCommand() == null) {
            return;
        }

        player.getPlayer().chat("/" + inventoryItem.getCommand());
        event.setCancelled(true);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        inventoryItems.forEach((key, value) -> player.getInventory().setItem(key, value.getItemStack(player)));
    }

    @EventHandler
    public void onLeave(ArenaLeaveEvent event) {
        Player player = event.getPlayer();
        inventoryItems.forEach((key, value) -> player.getInventory().setItem(key, value.getItemStack(player)));
    }

    @Override
    public String getConfigPath() {
        return "join-items";
    }
}