package me.patothebest.gamecore.combat;

import me.patothebest.gamecore.util.Tickable;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CombatTracker implements Tickable {

    private final LinkedList<CombatEntry> entries = new LinkedList<>();
    private int aliveTicks = 0;
    private boolean noTrack = false;

    @Override
    public void tick() {
        if (!entries.isEmpty()) {
            if (aliveTicks - entries.getLast().getTick() > 300) {
                entries.clear();
            }
        }

        aliveTicks++;
    }

    public void trackDamage(EntityDamageEvent entityDamageEvent, boolean noTrack) {
        if (this.noTrack) {
            this.noTrack = false;
            return;
        }
        this.noTrack = noTrack;
        entries.add(new CombatEntry(aliveTicks, entityDamageEvent));
    }

    public CombatDeathEvent onDeath(Player player) {
        List<ItemStack> drops = new ArrayList<>();
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if(itemStack != null && itemStack.getType() != Material.AIR) {
                drops.add(itemStack);
            }
        }

        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if(itemStack != null && itemStack.getType() != Material.AIR) {
                drops.add(itemStack);
            }
        }

        if (entries.isEmpty()) {
            return new CombatDeathEvent(player, drops, null, entries, null, null, null, "");
        }

        CombatEntry reason = entries.getLast();
        WeakReference<Entity> killer = reason.getKiller();
        WeakReference<Player> playerKiller = reason.getPlayerKiller();
        ItemStack killedWith = reason.getItemKilledWith();

        int prevId = entries.size() - 1;
        CombatEntry prev = reason;
        while (killer == null && prevId > 0 && prev.hasOption(DamageOption.WHILE_ESCAPING_OPTIONAL)) {
            prev = entries.get(--prevId);
            killer = prev.getKiller();
            playerKiller = prev.getPlayerKiller();
            killedWith = prev.getItemKilledWith();
        }

        Entity killerEntity = killer == null ? null : killer.get();
        if (killerEntity != null && killerEntity.isDead()) {
            killerEntity = null;
        }

        Player killerPlayer = playerKiller == null ? null : playerKiller.get();
        if (killerPlayer != null && (killerPlayer.isDead() || !killerPlayer.isOnline())) {
            killerPlayer = null;
        }
        return new CombatDeathEvent(player, drops, reason, entries, killedWith, killerEntity, killerPlayer, "");
    }

    public void reset() {
        this.entries.clear();
        this.aliveTicks = 0;
        this.noTrack = false;
    }
}
