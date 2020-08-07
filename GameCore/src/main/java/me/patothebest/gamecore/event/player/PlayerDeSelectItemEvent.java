package me.patothebest.gamecore.event.player;

import me.patothebest.gamecore.cosmetics.shop.ShopItem;
import me.patothebest.gamecore.player.IPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDeSelectItemEvent extends Event {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private static final HandlerList handlers = new HandlerList();
    private final IPlayer player;
    private final ShopItem shopItem;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public PlayerDeSelectItemEvent(IPlayer player, ShopItem shopItem) {
        this.player = player;
        this.shopItem = shopItem;
    }

    // -------------------------------------------- //
    // GETTERS
    // -------------------------------------------- //

    /**
     * Get's the player that has been loaded
     *
     * @return the player object
     */
    public IPlayer getPlayer() {
        return player;
    }

    /**
     * Gets the shopItem
     *
     * @return the shopItem
     */
    public ShopItem getShopItem() {
        return shopItem;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
