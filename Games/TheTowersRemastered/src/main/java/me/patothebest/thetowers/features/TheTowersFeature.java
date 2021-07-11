package me.patothebest.thetowers.features;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.combat.DeathCause;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.GameTeam;
import me.patothebest.thetowers.file.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class TheTowersFeature extends AbstractFeature {

    private final Cache<String, Byte> pointCooldown = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build();
    private final CorePlugin plugin;
    private final PlayerManager playerManager;
    private final KitManager kitManager;
    private final Config config;

    @Inject private TheTowersFeature(CorePlugin plugin, PlayerManager playerManager, KitManager kitManager, Config config) {
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.kitManager = kitManager;
        this.config = config;
    }

    @EventHandler
    public void onKill(CombatDeathEvent event) {
        Player killerPlayer = event.getKillerPlayer();
        if (killerPlayer == null) {
            return;
        }

        if (!isPlayingInArena(killerPlayer)) {
            return;
        }

        if (!((Arena)arena).getKills().containsPlayer(killerPlayer.getName())) {
            ((Arena)arena).getKills().addPlayer((GameTeam) arena.getTeam(killerPlayer), killerPlayer.getName());
        }
        ((Arena)arena).getKills().addPoints(killerPlayer.getName(), 1);
    }

    @EventHandler
    public void onDeath(CombatDeathEvent event) {
        if (!isPlayingInArena(event)) {
            return;
        }

        event.getDrops().removeIf(item -> item.getType() == Material.LEATHER_HELMET.parseMaterial() || item.getType() == Material.LEATHER_CHESTPLATE.parseMaterial() || item.getType() == Material.LEATHER_LEGGINGS.parseMaterial() || item.getType() == Material.LEATHER_BOOTS.parseMaterial());

        Player player = event.getPlayer();
        player.setHealth(20);
        player.setFallDistance(0);
        player.setVelocity(new Vector(0, 0, 0));

        if(event.getPlayer().getLocation().getY() < 1 ||
                !arena.getArea().contains(event.getPlayer().getLocation()) ||
                event.getDeathCause() == DeathCause.OUT_OF_WORLD) {
            event.getDrops().clear();
        }

        AbstractGameTeam gameTeam = arena.getTeam(event.getPlayer());

        if (gameTeam == null) { // can be null if the player disconnects
            return;
        }

        player.teleport(gameTeam.getSpawn());
        Utils.clearPlayer(player);

        kitManager.applyKit(event.getPlayer(), gameTeam);
        kitManager.applyPotionEffects(event.getPlayer());

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if (!isPlayingInArena(event)) {
            return;
        }

        event.setRespawnLocation(arena.getTeam(event.getPlayer()).getSpawn());

        Bukkit.getScheduler().runTaskLater(plugin, () -> event.getPlayer().addPotionEffects(config.getPotionEffects()), 1L);
    }

    @EventHandler
    public void dispenseEvent(BlockDispenseEvent e){
        if (e.getBlock().getType() == Material.DISPENSER.parseMaterial()) {
            String name = e.getItem().getType().name();
            if (name.contains("MONSTER_EGG") || name.contains("SPAWN_EGG")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void checkPointArea(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (!isPlayingInArena(event)) {
            return;
        }

        if (event.getPlayer().isDead()) {
            return;
        }

        GameTeam team = (GameTeam) arena.getTeam(event.getPlayer());

        for (AbstractGameTeam gameTeam : arena.getTeams().values()) {
            if (team.equals(gameTeam)) {
                continue;
            }

            ((GameTeam) gameTeam).getPointAreas().values().forEach(pointArea -> {
                if (!pointArea.contains(event.getTo())) {
                    return;
                }

                if (pointCooldown.getIfPresent(event.getPlayer().getName()) != null) {
                    System.out.println("Warning, " + event.getPlayer().getName() + " has scored less than 2 seconds ago and is already in " + team.getName() + " point area");
                    return;
                }

                pointCooldown.put(event.getPlayer().getName(), (byte) 0);
                ((GameTeam)playerManager.getPlayer(event.getPlayer()).getGameTeam()).scorePoint(event.getPlayer());
            });
        }
    }
}