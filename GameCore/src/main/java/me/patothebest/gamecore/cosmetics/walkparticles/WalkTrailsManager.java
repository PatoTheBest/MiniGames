package me.patothebest.gamecore.cosmetics.walkparticles;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import me.patothebest.gamecore.cosmetics.shop.AbstractShopManager;
import me.patothebest.gamecore.event.player.PlayerDeSelectItemEvent;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.phase.phases.GamePhase;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.event.arena.ArenaPhaseChangeEvent;
import me.patothebest.gamecore.event.player.PlayerSelectItemEvent;
import me.patothebest.gamecore.event.player.PlayerStateChangeEvent;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

@Singleton
@ModuleName("Walk Trails Manager")
public class WalkTrailsManager extends AbstractShopManager<WalkTrail> {

    private final Map<Player, TrackedWalkTrail> walkTrails = new HashMap<>();

    @Inject private WalkTrailsManager(CorePlugin plugin, PlayerManager playerManager) {
        super(plugin, playerManager);

        shopItemTypeObjectProvider = WalkTrail::new;
    }

    @EventHandler
    public void onJoin(ArenaPhaseChangeEvent event) {
        if (!(event.getNewPhase() instanceof GamePhase)) {
            return;
        }

        for (Player player : event.getArena().getPlayers()) {
            trackPlayer(player);
        }
    }

    @EventHandler
    public void onDeath(PlayerStateChangeEvent event) {
        if (event.getPlayerState() == PlayerStateChangeEvent.PlayerState.PLAYER) {
            return;
        }

        walkTrails.remove(event.getPlayer());
    }

    @EventHandler
    public void onAlive(PlayerStateChangeEvent event) {
        if (event.getPlayerState() != PlayerStateChangeEvent.PlayerState.PLAYER) {
            return;
        }

        if (event.getArena().isInGame()) {
            trackPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        walkTrails.remove(event.getPlayer());
    }

    @EventHandler
    public void onSelect(PlayerSelectItemEvent event) {
        trackPlayer(event.getPlayer().getPlayer());
    }

    @EventHandler
    public void onDeselect(PlayerDeSelectItemEvent event) {
        walkTrails.remove(event.getPlayer().getPlayer());
    }

    private void trackPlayer(Player player) {
        IPlayer corePlayer = playerManager.getPlayer(player);

        if(!corePlayer.isPlaying()) {
            return;
        }

        WalkTrail selectedItem = corePlayer.getSelectedItem(WalkTrail.class);

        if(selectedItem == null) {
            return;
        }

        walkTrails.put(player, new TrackedWalkTrail(selectedItem));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        TrackedWalkTrail walkTrail = walkTrails.get(player);

        if (walkTrail == null) {
            return;
        }

        if (player.isSneaking()){
            return;
        }

        if(!walkTrail.canSpawn()) {
            return;
        }

        for (int i = 0; i < walkTrail.getAmount(); i++) {
            Location location = player.getLocation();
            double distance =  Utils.randDouble(0, 1);
            double angle = Utils.randDouble(0, Math.PI * 2);
            location.add(Math.cos(angle) * distance, Utils.randDouble(0, 0.375), Math.sin(angle) * distance);
            if (walkTrail.getParticleType() == ParticleType.NOTE) {
                FastParticle.spawnParticle(location.getWorld(), walkTrail.getParticleType(), location, 1, 0, 0, 0, Utils.randInt(0, 24));
            } else {
                FastParticle.spawnParticle(location.getWorld(), walkTrail.getParticleType(), location, 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public ILang getTitle() {
        return CoreLang.SHOP_WALK_TRAILS_TITLE;
    }

    @Override
    public ILang getName() {
        return CoreLang.SHOP_WALK_TRAILS_NAME;
    }

    @Override
    public String getShopName() {
        return "walk-trails";
    }
}
