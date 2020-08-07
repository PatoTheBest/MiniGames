package me.patothebest.gamecore.feature.features.other;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.feature.features.protection.NoTeamDamageFeature;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.permission.PermissionGroup;
import me.patothebest.gamecore.permission.PermissionGroupManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PetsFeature extends AbstractFeature {

    private final WeakHashMap<Player, List<WeakReference<Entity>>> pets = new WeakHashMap<>();
    private final Map<PermissionGroup, Integer> petLimits = new HashMap<>();
    private final CorePlugin plugin;
    private final Provider<NMS> nms;
    private final PermissionGroupManager permissionGroupManager;
    private final PlayerManager playerManager;
    private final CoreConfig coreConfig;

    private boolean limitEnabled;
    private int defaultLimit = 5;

    @Inject private PetsFeature(CorePlugin plugin, Provider<NMS> nms, PermissionGroupManager permissionGroupManager, PlayerManager playerManager, CoreConfig coreConfig) {
        this.plugin = plugin;
        this.nms = nms;
        this.permissionGroupManager = permissionGroupManager;
        this.playerManager = playerManager;
        this.coreConfig = coreConfig;
    }

    @Override
    public void initializeFeature() {
        super.initializeFeature();

        petLimits.clear();
        limitEnabled = coreConfig.getBoolean("pets.limit.enabled");

        if(limitEnabled) {
            Map<String, Object> limits = coreConfig.getConfigurationSection("pets.limit.groups").getValues(true);
            limits.forEach((string, limit) -> {
                if(string.equalsIgnoreCase("default")) {
                    defaultLimit = (Integer) limit;
                }

                if(!(limit instanceof Integer)) {
                    return;
                }

                PermissionGroup permissionGroup = permissionGroupManager.getOrCreatePermissionGroup(string);
                petLimits.put(permissionGroup, (Integer) limit);
            });
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onSpawnPet(PlayerInteractEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(event.getItem() == null) {
            return;
        }

        Player player = event.getPlayer();
        AbstractGameTeam gameTeam = playerManager.getPlayer(player).getGameTeam();

        EntityType entityFromEgg = nms.get().getEntityFromEgg(event.getItem());

        if(entityFromEgg == null) {
            return;
        }

        if(entityFromEgg == EntityType.HORSE) {
            return;
        }

        if(limitEnabled) {
            int totalAlivePets = getTotalAlivePets(player);

            if(totalAlivePets >= defaultLimit) {
                final boolean[] canHaveMore = {false};

                petLimits.forEach((permissionGroup, limit) -> {
                    if(limit > totalAlivePets) {
                        if(permissionGroup.hasPermission(player)) {
                            canHaveMore[0] = true;
                        }
                    }
                });

                if(!canHaveMore[0]) {
                    player.sendMessage(CoreLang.YOU_HAVE_REACHED_PET_LIMIT.getMessage(player));
                    event.setCancelled(true);
                    return;
                }
            }
        }

        Entity entity = player.getWorld().spawnEntity(event.getClickedBlock().getLocation().add(0.5, 1, 0.5), entityFromEgg);
        nms.get().makePet((LivingEntity) entity, player.getUniqueId(), Utils.getColorFromDye(gameTeam.getColor()) + "Mascota de " +  player.getName());
        entity.setMetadata("PetOwner", new FixedMetadataValue(plugin, player));
        getPets(player).add(new WeakReference<>(entity));

        if(player.getGameMode() != GameMode.CREATIVE) {
            if (player.getItemInHand().getAmount() > 1) {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            } else {
                player.setItemInHand(new ItemStackBuilder(Material.AIR));
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamagePet(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) {
            return;
        }

        if(!isEventInArena(event)) {
            return;
        }

        Player player = (Player) event.getDamager();

        if(!canAttack(player, event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamagePet2(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        if(!isEventInArena(event)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if(!canAttack(player, event.getDamager())) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onAttackProjectile(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Projectile)) {
            return;
        }

        Projectile projectile = (Projectile) event.getDamager();

        if(!(projectile.getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) projectile.getShooter();

        if(!canAttack(player, event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAttackProjectilePet(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Projectile)) {
            return;
        }

        Projectile projectile = (Projectile) event.getDamager();
        ProjectileSource projectileShooter = projectile.getShooter();

        if(!(projectileShooter instanceof LivingEntity)) {
            return;
        }

        LivingEntity shooter = (LivingEntity) projectileShooter;

        if(event.getEntity() instanceof Player) {
            if(!canAttack((Player) event.getEntity(), shooter)) {
                event.setCancelled(true);
            }

            return;
        }

        if(hasPlayerOwner(shooter) && hasPlayerOwner(event.getEntity())) {
            Player damagerOwner = getPlayerOwner(shooter);
            Player damagedOwner = getPlayerOwner(event.getEntity());

            if(damagedOwner == damagerOwner) {
                event.setCancelled(true);
                return;
            }

            AbstractArena arena = playerManager.getPlayer(damagedOwner).getCurrentArena();
            AbstractGameTeam damagerTeam = arena.getTeam(damagerOwner);
            AbstractGameTeam damagedTeam = arena.getTeam(damagedOwner);

            if(damagedTeam.equals(damagerTeam)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion projectile = event.getPotion();
        ProjectileSource projectileShooter = projectile.getShooter();

        if(!(projectileShooter instanceof LivingEntity)) {
            return;
        }

        LivingEntity shooter = (LivingEntity) projectileShooter;

        event.getAffectedEntities().forEach(affectedEntity -> {
            if(affectedEntity instanceof Player) {
                if(!canAttack((Player) affectedEntity, shooter)) {
                    event.setIntensity(affectedEntity, 0);
                }

                return;
            }

            if(hasPlayerOwner(shooter) && hasPlayerOwner(affectedEntity)) {
                Player damagerOwner = getPlayerOwner(shooter);
                Player damagedOwner = getPlayerOwner(affectedEntity);

                if(damagedOwner == damagerOwner) {
                    event.setIntensity(affectedEntity, 0);
                    return;
                }

                AbstractArena arena = playerManager.getPlayer(damagedOwner).getCurrentArena();
                AbstractGameTeam damagerTeam = arena.getTeam(damagerOwner);
                AbstractGameTeam damagedTeam = arena.getTeam(damagedOwner);

                if(damagedTeam.equals(damagerTeam)) {
                    event.setIntensity(affectedEntity, 0);
                }
            }
        });
    }

    @EventHandler
    public void killPets(CombatDeathEvent event) {
        killPets(event.getPlayer());

        for (List<WeakReference<Entity>> weakReferences : pets.values()) {
            for (WeakReference<Entity> weakReference : weakReferences) {
                Entity entity = weakReference.get();

                if(entity == null) {
                    continue;
                }

                if(entity instanceof Creature) {
                    if(((Creature) entity).getTarget() == event.getPlayer()) {
                        ((Creature) entity).setTarget(null);
                    }
                }
            }
        }
    }

    @EventHandler
    public void killPets(ArenaLeaveEvent event) {
        killPets(event.getPlayer());
        pets.remove(event.getPlayer());
    }

    private void killPets(Player player) {
        if(!isPlayingInArena(player.getName())) {
            return;
        }

        List<WeakReference<Entity>> pets = getPets(player);

        for (WeakReference<Entity> pet : pets) {
            if(pet.get() == null) {
                pets.remove(pet);
                continue;
            }

            if(pet.get().isDead()) {
                pets.remove(pet);
                continue;
            }

            pet.get().remove();
            pets.remove(pet);
        }

        pets.clear();
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if(!(event.getTarget() instanceof Player)) {
            return;
        }

        if(!isEventInArena(event)) {
            return;
        }

        Player player = (Player) event.getTarget();

        if(!canAttack(player, event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        pets.values().forEach(entities -> entities.forEach(entity -> {
            if(event.getEntity() == entity.get()) {
                event.setCancelled(true);
            }
        }));
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent event) {
        if (!event.getEntity().hasMetadata("PetOwner")) {
            return;
        }

        event.getEntity().setFallDistance(0);
    }

    private boolean canAttack(Player player, Entity entity) {
        final boolean[] can = {true};
        pets.forEach((playerIterated, entities) -> {
            for (WeakReference<Entity> entityWeakReference : entities) {
                if(entityWeakReference.get() == entity) {
                    IPlayer corePlayer = playerManager.getPlayer(player);
                    if(corePlayer == null) {
                        can[0] = false;
                        return;
                    }

                    if(corePlayer.getGameTeam() == null) {
                        can[0] = false;
                        return;
                    }

                    if(playerManager.getPlayer(playerIterated).getGameTeam() == null) {
                        can[0] = false;
                        return;
                    }

                    if(arena.getFeature(NoTeamDamageFeature.class) != null) {
                        if(corePlayer.getGameTeam() == playerManager.getPlayer(playerIterated).getGameTeam()) {
                            can[0] = false;
                            return;
                        }
                    }
                }
            }
        });

        return can[0];
    }

    private int getTotalAlivePets(Player player) {
        int pets = 0;

        List<WeakReference<Entity>> pets1 = getPets(player);
        for (WeakReference<Entity> entityWeakReference : pets1) {
            if(entityWeakReference.get() == null) {
                pets1.remove(entityWeakReference);
                continue;
            }

            if(entityWeakReference.get().isDead()) {
                pets1.remove(entityWeakReference);
                continue;
            }

            pets++;
        }

        return pets;
    }

    private List<WeakReference<Entity>> getPets(Player player) {
        if(pets.containsKey(player)) {
            return pets.get(player);
        }

        List<WeakReference<Entity>> petList = new CopyOnWriteArrayList<>();
        pets.put(player, petList);
        return petList;
    }

    private boolean hasPlayerOwner(Entity entity) {
        return getPlayerOwner(entity) != null;
    }

    private Player getPlayerOwner(Entity entity) {
        final Player[] player = {null};
        pets.forEach((player1, weakReferences) -> {
            for (WeakReference<Entity> weakReference : weakReferences) {
                if(weakReference.get() == entity) {
                    player[0] = player1;
                    return;
                }
            }
        });

        return player[0];
    }

    public boolean isPlayingInArena(String playerName) {
        return playerManager.getPlayer(playerName).isInArena() && playerManager.getPlayer(playerName).getCurrentArena().isInGame();
    }
}