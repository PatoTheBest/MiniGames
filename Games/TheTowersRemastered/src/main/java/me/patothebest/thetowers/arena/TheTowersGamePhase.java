package me.patothebest.thetowers.arena;

import com.google.inject.Provider;
import me.patothebest.thetowers.features.DropperRunnable;
import me.patothebest.thetowers.features.ProtectionFeature;
import me.patothebest.thetowers.features.TheTowersFeature;
import me.patothebest.gamecore.arena.option.ArenaOption;
import me.patothebest.gamecore.feature.features.chests.regen.ChestRegenFeature;
import me.patothebest.gamecore.feature.features.kits.AntiShareKitFeature;
import me.patothebest.gamecore.feature.features.kits.RespawnWithKitFeature;
import me.patothebest.gamecore.feature.features.other.CompassRadarFeature;
import me.patothebest.gamecore.feature.features.other.DeathMessageFeature;
import me.patothebest.gamecore.feature.features.other.InformationalFeature;
import me.patothebest.gamecore.feature.features.other.PetsFeature;
import me.patothebest.gamecore.feature.features.protection.NoBorderTrespassingFeature;
import me.patothebest.gamecore.feature.features.protection.NoTeamDamageFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorProtectionFeature;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.phase.phases.GamePhase;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.sign.SignManager;
import me.patothebest.thetowers.TheTowersRemastered;
import net.megaplanet.thetowers.api.events.PlayerJoinArenaEvent;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class TheTowersGamePhase extends GamePhase<TheTowersRemastered, Arena> {

    @Inject private TheTowersGamePhase(TheTowersRemastered plugin, Provider<NMS> nmsProvider, PlayerManager playerManager, SignManager signManager, UserGUIFactory userGUIFactory) {
        super(plugin, nmsProvider, signManager, playerManager, userGUIFactory);
    }

    @Override
    public void configure() {
        registerFeature(DropperRunnable.class);
        registerFeature(PetsFeature.class);
        registerFeature(ChestRegenFeature.class);
        registerFeature(AntiShareKitFeature.class);
        registerFeature(DeathMessageFeature.class);
        registerFeature(RespawnWithKitFeature.class);
        registerFeature(NoBorderTrespassingFeature.class);
        registerFeature(NoTeamDamageFeature.class);
        registerFeature(ProtectionFeature.class);
        registerFeature(TheTowersFeature.class);
        registerFeature(SpectatorProtectionFeature.class);
        registerFeature(SpectatorFeature.class);
        registerFeature(CompassRadarFeature.class);
        registerFeature(InformationalFeature.class);
        for (ArenaOption arenaOption : arena.getArenaOptions()) {
            registerFeature(arenaOption);
        }

        setCanJoin(true);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void callJoinEvent(Player player) {
        super.callJoinEvent(player);
        plugin.getServer().getPluginManager().callEvent(new PlayerJoinArenaEvent(player, arena));
    }
}