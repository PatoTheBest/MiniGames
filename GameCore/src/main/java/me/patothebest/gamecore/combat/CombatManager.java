package me.patothebest.gamecore.combat;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.util.WrappedBukkitRunnable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class CombatManager extends WrappedBukkitRunnable implements ActivableModule, ListenerModule {

    private final Map<Player, CombatTracker> combatTrackers = new HashMap<>();
    private final CorePlugin plugin;
    private final EventRegistry eventRegistry;
    private final Map<Player, CombatDeathEvent> deathEvents = new HashMap<>();

    @Inject
    private CombatManager(CorePlugin plugin, EventRegistry eventRegistry) {
        this.plugin = plugin;
        this.eventRegistry = eventRegistry;
    }

    @Override
    public void onEnable() {
        runTaskTimer(plugin, 1L, 1L);
    }

    @Override
    public void onDisable() {
        cancel();
    }

    @Override
    public void run() {
        combatTrackers.values().forEach(CombatTracker::tick);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        combatTrackers.put(event.getPlayer(), new CombatTracker());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event) {
        combatTrackers.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        combatTrackers.get(player).trackDamage(event, false);

        if (event.getFinalDamage() >= (player).getHealth()) {
            Location playerLocation = player.getLocation();
            CombatDeathEvent combatDeathEvent = callDeathEvent(player);

            if (!combatDeathEvent.isCancelled()) {
                deathEvents.put(player, combatDeathEvent);
            } else {
                event.setDamage(0);
                for (ItemStack drop : combatDeathEvent.getDrops()) {
                    if(drop != null) {
                        player.getWorld().dropItemNaturally(playerLocation, drop);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        CombatDeathEvent combatDeathEvent = deathEvents.remove(event.getEntity());
        if (combatDeathEvent == null) {
            Utils.printError("Unhandled death event!", event.getEntity().getName(), event.getDeathMessage());
            return;
        }

        event.setDeathMessage(combatDeathEvent.getDeathMessage());
        event.getDrops().removeIf(itemStack -> !combatDeathEvent.getDrops().contains(itemStack));
    }

    public void damagePlayer(Player player, DamageCause damageCause, double damage) {
        combatTrackers.get(player).trackDamage(new EntityDamageEvent(player, DamageCause.getCause(damageCause), damage), true);

        if (player.getHealth() <= damage) {
            killPlayer(player, true);
        } else {
            player.damage(damage);
        }
    }

    /**
     * Kills a player
     * <p>
     * This will call {@link CombatDeathEvent}.
     * If the event is cancelled, the items will drop (if any) on the player's
     * death location.
     * If the event is not cancelled, the player's health will be set to 0,
     * triggering spigot's {@link PlayerDeathEvent} and shows the death screen.
     * <p>
     * The delegateEvent boolean is to override the isCancelled and never call
     * spigot's death event, regardless of the event state.
     *
     * @param player the player
     * @param delegateEvent whether to delegate the event to spigot
     */
    public void killPlayer(Player player, boolean delegateEvent) {
        Location playerLocation = player.getLocation();
        CombatDeathEvent combatDeathEvent = callDeathEvent(player);

        if (!combatDeathEvent.isCancelled() && delegateEvent) {
            deathEvents.put(player, combatDeathEvent);
            player.setHealth(0);
        } else {
            for (ItemStack drop : combatDeathEvent.getDrops()) {
                if(drop != null) {
                    player.getWorld().dropItemNaturally(playerLocation, drop);
                }
            }
        }
    }

    private CombatDeathEvent callDeathEvent(Player player) {
        CombatTracker combatTracker = combatTrackers.get(player);
        CombatDeathEvent combatDeathEvent = combatTracker.onDeath(player);
        eventRegistry.callEvent(combatDeathEvent);
        combatTracker.reset();
        return combatDeathEvent;
    }
}
