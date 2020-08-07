package me.patothebest.gamecore.combat;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.ref.WeakReference;

public class CombatEntry {

    private final int tick;
    private final DamageCause damageCause;
    private final DeathCause deathCause;
    private final DamageOption[] damageOptions;
    private final double damage;
    private WeakReference<Player> playerKiller = null;
    private WeakReference<Entity> killer = null;
    private ItemStack itemKilledWith = null;

    @SuppressWarnings("unchecked")
    CombatEntry(int tick, EntityDamageEvent event) {
        this.tick = tick;
        this.damageCause = DamageCause.getCause(event.getCause());

        if (!damageCause.getDeathCauseTranslator().getEventClass().isAssignableFrom(event.getClass())) {
            throw new IllegalArgumentException(damageCause.getDeathCauseTranslator().getEventClass() + " is not assignable from " + event.getClass() + "! DamageCause: " + damageCause);
        }

        this.deathCause = damageCause.getDeathCauseTranslator().getDeathCause(event);
        this.damage = event.getFinalDamage();
        this.damageOptions = damageCause.getDeathCauseTranslator().getDamageOptions(deathCause);

        Entity tempKiller = null;
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;

            for (DamageOption damageOption : damageOptions) {
                if (damageOption == DamageOption.ITEM_OPTIONAL) {
                    if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                        itemKilledWith = ((Player) entityDamageByEntityEvent.getDamager()).getItemInHand();
                        if (itemKilledWith == null || itemKilledWith.getType() == Material.AIR) {
                            itemKilledWith = null;
                        }
                    }
                } else if(damageOption == DamageOption.ENTITY_NAME_OPTIONAL || damageOption == DamageOption.ENTITY_NAME_REQUIRED) {
                    if (entityDamageByEntityEvent.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                        if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                            tempKiller = entityDamageByEntityEvent.getDamager();
                        } else {
                            Projectile projectile = (Projectile) entityDamageByEntityEvent.getDamager();
                            Entity shooter = (projectile.getShooter() instanceof LivingEntity) ? (Entity) projectile.getShooter() : null;

                            if (shooter != null) {
                                tempKiller = shooter;
                            }
                        }
                    } else {
                        tempKiller = entityDamageByEntityEvent.getDamager();
                    }
                }
            }
        }

        if (tempKiller != null) {
            if (tempKiller instanceof Player) {
                playerKiller = new WeakReference<>((Player) tempKiller);
            } else if (tempKiller.hasMetadata("PetOwner") && !tempKiller.getMetadata("PetOwner").isEmpty()) {
                Player petOwner = (Player) tempKiller.getMetadata("PetOwner").get(0).value();
                if (petOwner != null) {
                    playerKiller = new WeakReference<>(petOwner);
                }
            }
            killer = new WeakReference<>(tempKiller);
        }
    }

    public int getTick() {
        return tick;
    }

    public DamageCause getDamageCause() {
        return damageCause;
    }

    public DeathCause getDeathCause() {
        return deathCause;
    }

    public DamageOption[] getDamageOptions() {
        return damageOptions;
    }

    public boolean hasOption(DamageOption damageOption) {
        for (DamageOption option : damageOptions) {
            if (damageOption == option) {
                return true;
            }
        }

        return false;
    }

    public double getDamage() {
        return damage;
    }

    public WeakReference<Entity> getKiller() {
        return killer;
    }

    public WeakReference<Player> getPlayerKiller() {
        return playerKiller;
    }

    public ItemStack getItemKilledWith() {
        return itemKilledWith;
    }

}
