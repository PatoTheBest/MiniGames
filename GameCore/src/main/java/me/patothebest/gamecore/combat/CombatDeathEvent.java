package me.patothebest.gamecore.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class CombatDeathEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final List<ItemStack> drops;
    private final CombatEntry lastDamage;
    private final LinkedList<CombatEntry> combatEntries;
    private final ItemStack itemKilledWith;
    private final Entity killer;
    private final Player killerPlayer;
    private String deathMessage = "";
    private boolean cancelled = false;

    public CombatDeathEvent(Player player, List<ItemStack> drops, CombatEntry lastDamage, LinkedList<CombatEntry> combatEntries, ItemStack itemKilledWith, Entity killer, Player killerPlayer, String deathMessage) {
        super(player);
        this.drops = drops;
        this.lastDamage = lastDamage;
        this.combatEntries = combatEntries;
        this.itemKilledWith = itemKilledWith;
        this.killer = killer;
        this.killerPlayer = killerPlayer;
        this.deathMessage = deathMessage;
    }

    public DeathCause getDeathCause() {
        if (lastDamage == null) {
            return DeathCause.GENERIC;
        }

        return lastDamage.getDeathCause();
    }

    @Nullable
    public ItemStack getItemKilledWith() {
        return itemKilledWith;
    }

    @Nullable
    public Entity getKiller() {
        return killer;
    }

    /**
     * This will return the player if the killer is a player, or
     * will return the entity owner (pet owner)
     * @return the player killer
     */
    @Nullable
    public Player getKillerPlayer() {
        return killerPlayer;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public CombatEntry getLastDamage() {
        return lastDamage;
    }

    public LinkedList<CombatEntry> getCombatEvents() {
        return combatEntries;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
