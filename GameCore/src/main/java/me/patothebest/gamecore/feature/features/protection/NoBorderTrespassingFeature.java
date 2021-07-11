package me.patothebest.gamecore.feature.features.protection;

import com.google.inject.Inject;
import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.combat.CombatManager;
import me.patothebest.gamecore.combat.DamageCause;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.lang.CoreLang;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NoBorderTrespassingFeature extends AbstractRunnableFeature {

    private final List<Player> players = new CopyOnWriteArrayList<>();
    private final CombatManager combatManager;
    private final CorePlugin plugin;
    private final CoreConfig config;

    private double damage;
    private boolean showParticles;

    @Inject private NoBorderTrespassingFeature(CombatManager combatManager, CorePlugin plugin, CoreConfig config) {
        this.combatManager = combatManager;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void initializeFeature() {
        this.damage = config.getDouble("border.damage");
        this.showParticles = config.getBoolean("border.show-particles");
        runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void run() {
        for (Player player : players) {
            if(!player.isOnline() || !isPlayingInArena(player) || player.isDead()) {
                players.remove(player);
                continue;
            }

            Location loc = player.getLocation();
            Location lowerNE = arena.getArea().getLowerNE();
            Location upperSW = arena.getArea().getUpperSW();
            double lX = lowerNE.getBlockX();
            double lY = lowerNE.getBlockY();
            double lZ = lowerNE.getBlockZ();

            double uX = upperSW.getBlockX();
            double uY = upperSW.getBlockY() + 5;
            double uZ = upperSW.getBlockZ();
            if (loc.getX() < lX || loc.getX() > uX || loc.getZ() < lZ || loc.getZ() > uZ || loc.getY() < lY || loc.getY() > uY) {
                if(arena.getSpectators().contains(player)) {
                    player.teleport(arena.getSpectatorLocation());
                    players.remove(player);
                    return;
                }

                if (damage > 0) {
                    combatManager.damagePlayer(player, DamageCause.MELTING, damage);
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 2));
                player.sendMessage(CoreLang.RETURN_PLAYABLE_AREA.getMessage(player));
            } else {
                players.remove(player);
            }
        }
    }

    @EventHandler
    public void checkBorder(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        if (!isPlayingInArena(player)) {
            return;
        }

        if (player.isDead()) {
            return;
        }

        if (event.getTo().getBlockY() <= 1) {
            combatManager.damagePlayer(player, DamageCause.VOID, 1000);
            players.remove(player);
            return;
        }

        if (players.contains(player)) {
            return;
        }

        if (!arena.getArea().contains(event.getTo())) {
            players.add(player);
            return;
        }

        if (!showParticles) {
            return;
        }

        double x = event.getTo().getBlockX() + 0.5;
        double y = event.getTo().getBlockY();
        double z = event.getTo().getBlockZ() + 0.5;

        Location lowerNE = arena.getArea().getLowerNE();
        Location upperSW = arena.getArea().getUpperSW();
        double lX = lowerNE.getBlockX() + 2.5;
        //double lY = lowerNE.getBlockY() + 2;
        double lZ = lowerNE.getBlockZ() + 2.5;

        double uX = upperSW.getBlockX() - 1.5;
        //double uY = upperSW.getBlockY() - 2;
        double uZ = upperSW.getBlockZ() - 1.5;

        if (x < lX) {
            FastParticle.spawnParticle(player, ParticleType.BARRIER, lX - 3, y, z, 1);
            FastParticle.spawnParticle(player, ParticleType.BARRIER, lX - 3, y + 1, z, 1);
        } else if (x > uX) {
            FastParticle.spawnParticle(player, ParticleType.BARRIER, uX + 3, y, z, 1);
            FastParticle.spawnParticle(player, ParticleType.BARRIER, uX + 3, y + 1, z, 1);
        }

        /*if (y < lY) {
            FastParticle.spawnParticle(player, ParticleType.BARRIER, x, lY - 2.5, z, 1);
        } else if (y > uY) {
            FastParticle.spawnParticle(player, ParticleType.BARRIER, x, uY + 4, z, 1);
        }*/

        if (z < lZ) {
            FastParticle.spawnParticle(player, ParticleType.BARRIER, x, y, lZ - 3, 1);
            FastParticle.spawnParticle(player, ParticleType.BARRIER, x, y + 1, lZ - 3, 1);
        } else if (z > uZ) {
            FastParticle.spawnParticle(player, ParticleType.BARRIER, x, y, uZ + 3, 1);
            FastParticle.spawnParticle(player, ParticleType.BARRIER, x, y + 1, uZ + 3, 1);
        }
    }

    @Override
    public void stopFeature() {
        super.stopFeature();
        players.clear();
    }
}