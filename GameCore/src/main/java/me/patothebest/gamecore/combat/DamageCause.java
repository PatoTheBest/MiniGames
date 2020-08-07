package me.patothebest.gamecore.combat;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * An enum to specify the cause of the damage
 */
public enum DamageCause {

    /**
     * Damage caused by being in the area when a block explodes.
     * <p>
     * Damage: variable
     */
    BLOCK_EXPLOSION(DeathCause.EXPLOSION, DamageOption.ENTITY_NAME_OPTIONAL, DamageOption.ITEM_OPTIONAL),
    /**
     * Damage caused when an entity contacts a block such as a Cactus.
     * <p>
     * Damage: 1 (Cactus)
     */
    CONTACT(new DeathCauseTranslator<EntityDamageByBlockEvent>(EntityDamageByBlockEvent.class) {
        @Override
        public DeathCause getDeathCause(EntityDamageByBlockEvent event) {
            Player player = (Player) event.getEntity();
            Block block = event.getDamager();

            if (block == null) {
                Utils.printError("Could not find block the player had contact with!", "Player: " + player.getName(), "Damage Cause: Contact");
                return DeathCause.GENERIC;
            }

            if (block.getType() == Material.CACTUS.parseMaterial()) {
                return DeathCause.CACTUS;
            } else if (block.getType() == Material.SWEET_BERRY_BUSH.parseMaterial()) {
                return DeathCause.SWEET_BERRY_BUSH;
            }else if (block.getType() == Material.BARRIER.parseMaterial()) {
                return DeathCause.BORDER;
            }

            Utils.printError("Could not find block the player had contact with!", "Player: " + player.getName(), "Damage Cause: Contact");
            return DeathCause.GENERIC;
        }

        @Override
        public DamageOption[] getDamageOptions(DeathCause deathCause) {
            return new DamageOption[] { DamageOption.WHILE_ESCAPING_OPTIONAL};
        }
    }),
    /**
     * Damage caused when an entity is colliding with too many entities due
     * to the maxEntityCramming game rule.
     * <p>
     * Damage: 6
     */
    CRAMMING(DeathCause.CRAMMING, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Custom damage.
     * <p>
     * Damage: variable
     */
    CUSTOM(DeathCause.GENERIC),
    /**
     * Damage caused by a dragon breathing fire.
     * <p>
     * Damage: variable
     */
    DRAGON_BREATH(DeathCause.DRAGON_BREATH, DamageOption.ENTITY_NAME_OPTIONAL),
    /**
     * Damage caused by running out of air while in water
     * <p>
     * Damage: 2
     */
    DROWNING(DeathCause.DROWN, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused when an entity that should be in water is not.
     * <p>
     * Damage: 1
     */
    // Not supposed to be called for players
    @Deprecated
    DRYOUT(DeathCause.GENERIC),
    /**
     * Damage caused when an entity attacks another entity.
     * <p>
     * Damage: variable
     */
    ENTITY_ATTACK(new DeathCauseTranslator<EntityDamageByEntityEvent>(EntityDamageByEntityEvent.class) {
        @Override
        public DeathCause getDeathCause(EntityDamageByEntityEvent event) {
            EntityType type = event.getDamager().getType();
            if (type == EntityType.PLAYER) {
                return DeathCause.PLAYER;
            } else if (type == EntityType.FIREWORK) {
                return DeathCause.FIREWORKS;
            } else if (type.name().equals("BEE")) {
                return DeathCause.STING;
            } else {
                return DeathCause.MOB;
            }
        }

        @Override
        public DamageOption[] getDamageOptions(DeathCause deathCause) {
            if (deathCause == DeathCause.FIREWORKS || deathCause == DeathCause.STING) {
                return new DamageOption[] { DamageOption.ENTITY_NAME_OPTIONAL};
            }

            return new DamageOption[] { DamageOption.ENTITY_NAME_REQUIRED, DamageOption.ITEM_OPTIONAL };
        }
    }),
    /**
     * Damage caused by being in the area when an entity, such as a
     * Creeper, explodes.
     * <p>
     * Damage: variable
     */
    ENTITY_EXPLOSION(DeathCause.EXPLOSION, DamageOption.ENTITY_NAME_OPTIONAL, DamageOption.ITEM_OPTIONAL),
    /**
     * Damage caused when an entity attacks another entity in a sweep attack.
     * <p>
     * Damage: variable
     */
    ENTITY_SWEEP_ATTACK(new DeathCauseTranslator<EntityDamageByEntityEvent>(EntityDamageByEntityEvent.class) {
        @Override
        public DeathCause getDeathCause(EntityDamageByEntityEvent event) {
            EntityType type = event.getDamager().getType();
            if (type == EntityType.PLAYER) {
                return DeathCause.PLAYER;
            } else {
                return DeathCause.MOB;
            }
        }

        @Override
        public DamageOption[] getDamageOptions(DeathCause deathCause) {
            return new DamageOption[] { DamageOption.ENTITY_NAME_REQUIRED, DamageOption.ITEM_OPTIONAL };
        }
    }),
    /**
     * Damage caused when an entity falls a distance greater than 3 blocks
     * <p>
     * Damage: fall height - 3.0
     */
    FALL(DeathCause.FALL, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused by being hit by a falling block which deals damage
     * <p>
     * <b>Note:</b> Not every block deals damage
     * <p>
     * Damage: variable
     */
    FALLING_BLOCK(new DeathCauseTranslator<EntityDamageByEntityEvent>(EntityDamageByEntityEvent.class) {
        @Override
        public DeathCause getDeathCause(EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (damager instanceof FallingBlock) {
                // TODO: Fix pre 1.13 (FallingBlock.getMaterial is now FallingBlock.getBlockData().getMaterial())
                Material material = Material.matchMaterial(((FallingBlock) damager).getBlockData().getMaterial());
                if (material == Material.ANVIL) {
                    return DeathCause.ANVIL;
                }
            }

            return DeathCause.FALLING_BLOCK;
        }

        @Override
        public DamageOption[] getDamageOptions(DeathCause deathCause) {
            return new DamageOption[] { DamageOption.WHILE_ESCAPING_OPTIONAL};
        }
    }),
    /**
     * Damage caused by direct exposure to fire
     * <p>
     * Damage: 1
     */
    FIRE(DeathCause.IN_FIRE, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused due to burns caused by fire
     * <p>
     * Damage: 1
     */
    FIRE_TICK(DeathCause.ON_FIRE, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused when an entity runs into a wall.
     * <p>
     * Damage: variable
     */
    FLY_INTO_WALL(DeathCause.FLY_INTO_WALL, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused when an entity steps on {@link Material#MAGMA_BLOCK}.
     * <p>
     * Damage: 1
     */
    HOT_FLOOR(DeathCause.HOT_FLOOR, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused by direct exposure to lava
     * <p>
     * Damage: 4
     */
    LAVA(DeathCause.LAVA, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused by being struck by lightning
     * <p>
     * Damage: 5
     */
    LIGHTNING(DeathCause.LIGHTNING_BOLT, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused by being hit by a damage potion or spell
     * <p>
     * Damage: variable
     */
    MAGIC(DeathCause.MAGIC, DamageOption.ENTITY_NAME_OPTIONAL, DamageOption.ITEM_OPTIONAL),
    /**
     * Damage caused due to a snowman melting
     * <p>
     * Damage: 1
     */
    // This is not a cause for players, so we use it for the border
    MELTING(DeathCause.BORDER, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused due to an ongoing poison effect
     * <p>
     * Damage: 1
     */
    // Shouldn't be a death cause
    @Deprecated
    POISON(DeathCause.MAGIC),
    /**
     * Damage caused when attacked by a projectile.
     * <p>
     * Damage: variable
     */
    PROJECTILE(new DeathCauseTranslator<EntityDamageByEntityEvent>(EntityDamageByEntityEvent.class) {
        @Override
        public DeathCause getDeathCause(EntityDamageByEntityEvent event) {
            // - fix for fishing rods -
            // apparently fishing rod damage is caused by a projectile
            // but the projectile is a player cause the fishing bob isn't
            // a projectile
            if (event.getDamager() instanceof Player) {
                return DeathCause.THROWN;
            }

            Projectile projectile = (Projectile) event.getDamager();
            String name = projectile.getType().name();
            switch (name) {
                case "ARROW":
                case "SPECTRAL_ARROW": {
                    return DeathCause.ARROW;
                }
                case "TRIDENT": {
                    return DeathCause.TRIDENT;
                }
                case "FIREBALL":
                case "SMALL_FIREBALL": {
                    return DeathCause.FIREBALL;
                }
                case "SHULKER_BULLET":
                case "EGG":
                case "SNOWBALL":
                case "FISHING_HOOK":
                case "ENDER_PEARL": {
                    return DeathCause.THROWN;
                }
            }
            Utils.printError("Unknown projectile damage source: "+ name);
            return DeathCause.THROWN;
        }

        @Override
        public DamageOption[] getDamageOptions(DeathCause deathCause) {
            return new DamageOption[] { DamageOption.ENTITY_NAME_REQUIRED, DamageOption.ITEM_OPTIONAL };
        }
    }),
    /**
     * Damage caused by starving due to having an empty hunger bar
     * <p>
     * Damage: 1
     */
    STARVATION(DeathCause.STARVE, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused by being put in a block
     * <p>
     * Damage: 1
     */
    SUFFOCATION(DeathCause.IN_WALL, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused by committing suicide using the command "/kill"
     * <p>
     * Damage: 1000
     */
    SUICIDE(DeathCause.GENERIC),
    /**
     * Damage caused in retaliation to another attack by the Thorns
     * enchantment.
     * <p>
     * Damage: 1-4 (Thorns)
     */
    THORNS(DeathCause.THORNS, DamageOption.ENTITY_NAME_REQUIRED, DamageOption.ITEM_OPTIONAL),
    /**
     * Damage caused by falling into the void
     * <p>
     * Damage: 4 for players
     */
    VOID(DeathCause.OUT_OF_WORLD, DamageOption.WHILE_ESCAPING_OPTIONAL),
    /**
     * Damage caused by Wither potion effect
     */
    WITHER(DeathCause.WITHER, DamageOption.WHILE_ESCAPING_OPTIONAL);

    private static final BiMap<EntityDamageEvent.DamageCause, DamageCause> DAMAGE_CAUSE_MAP = HashBiMap.create();
    private final DeathCauseTranslator deathCauseTranslator;

    DamageCause(DeathCause deathCause, DamageOption... damageOptions) {
        this.deathCauseTranslator = new DeathCauseTranslator<EntityDamageEvent>(EntityDamageEvent.class) {
            @Override
            public DeathCause getDeathCause(EntityDamageEvent event) {
                return deathCause;
            }

            @Override
            public DamageOption[] getDamageOptions(DeathCause deathCause) {
                return damageOptions;
            }
        };
    }

    DamageCause(DeathCauseTranslator deathCauseTranslator) {
        this.deathCauseTranslator = deathCauseTranslator;
    }

    public DeathCauseTranslator getDeathCauseTranslator() {
        return deathCauseTranslator;
    }

    public static DamageCause getCause(EntityDamageEvent.DamageCause damageCause) {
        return DAMAGE_CAUSE_MAP.get(damageCause);
    }

    public static EntityDamageEvent.DamageCause getCause(DamageCause damageCause) {
        return DAMAGE_CAUSE_MAP.inverse().get(damageCause);
    }

    static {
        for (EntityDamageEvent.DamageCause value : EntityDamageEvent.DamageCause.values()) {
            DamageCause damageCause = DamageCause.valueOf(value.name());
            //noinspection ConstantConditions
            if (damageCause == null) {
                Utils.printError("Unknown damage cause " + value.name());
                damageCause = DamageCause.CUSTOM;
            }

            DAMAGE_CAUSE_MAP.put(value, damageCause);
        }
    }
}