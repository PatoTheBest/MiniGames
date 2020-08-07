package me.patothebest.gamecore.phase.phases;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.types.SpawneableArena;
import me.patothebest.gamecore.cosmetics.cage.Cage;
import me.patothebest.gamecore.cosmetics.cage.CageManager;
import me.patothebest.gamecore.cosmetics.cage.CageStructure;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.sign.SignManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CagePhase<Arena extends AbstractArena & SpawneableArena> extends WaitingPhase<Arena> {

    private final CageManager cageManager;
    private CageStructure cage = CageStructure.INDIVIDUAL;

    @Inject private CagePhase(CorePlugin plugin, Provider<NMS> nms, PlayerManager playerManager, SignManager signManager, CageManager cageManager) {
        super(plugin, nms, playerManager, signManager);
        this.cageManager = cageManager;
    }

    @Override
    public void stop() {
        usedLocations.values().forEach(location -> cage.createCage(nms, CageManager.NULL_CAGE, location.clone().subtract(cage.getOffset(), -1, cage.getOffset())));
        super.stop();
    }

    @Override
    protected void preTeleport(IPlayer iPlayer, Location location) {
        cage.createCage(nms, iPlayer.getSelectedItemOrDefault(Cage.class, cageManager.getDefaultItem()), location.clone().subtract(cage.getOffset(), -1, cage.getOffset()));
    }

    public void updateCage(Player player) {
        if(!usedLocations.containsKey(player)) {
            throw new IllegalArgumentException(player.getName() + " is not in arena " + arena.getName() + "!");
        }

        IPlayer iPlayer = playerManager.getPlayer(player);
        Location clone = usedLocations.get(player).clone();
        clone.setX(usedLocations.get(player).getBlockX() + 0.5);
        clone.setZ(usedLocations.get(player).getBlockZ() + 0.5);
        clone.setY(usedLocations.get(player).getBlockY() + 2);
        player.teleport(clone);
        cage.createCage(nms, iPlayer.getSelectedItemOrDefault(Cage.class, cageManager.getDefaultItem()), usedLocations.get(player).clone().subtract(cage.getOffset(), -1, cage.getOffset()));

    }

    @Override
    public void playerLeave(Player player) {
        // spectator leave check
        if (!usedLocations.containsKey(player)) {
            return;
        }
        cage.createCage(nms, CageManager.NULL_CAGE, usedLocations.remove(player).clone().subtract(cage.getOffset(), -1, cage.getOffset()));
    }

    public void setCageType(CageStructure cage) {
        this.cage = cage;
    }
}
