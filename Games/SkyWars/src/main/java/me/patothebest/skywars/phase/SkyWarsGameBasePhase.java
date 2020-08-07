package me.patothebest.skywars.phase;

import com.google.inject.Provider;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.feature.features.chests.refill.ChestRefillFeature;
import me.patothebest.gamecore.feature.features.other.CompassRadarFeature;
import me.patothebest.gamecore.feature.features.other.DeathMessageFeature;
import me.patothebest.gamecore.feature.features.other.InformationalFeature;
import me.patothebest.gamecore.feature.features.other.LimitedTimePhaseFeature;
import me.patothebest.gamecore.feature.features.other.PetsFeature;
import me.patothebest.gamecore.feature.features.protection.GameProtectionFeature;
import me.patothebest.gamecore.feature.features.protection.NoBorderTrespassingFeature;
import me.patothebest.gamecore.feature.features.protection.NoInitialFallDamage;
import me.patothebest.gamecore.feature.features.protection.NoTeamDamageFeature;
import me.patothebest.gamecore.feature.features.spectator.DeathSpectatorFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorProtectionFeature;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.phase.phases.GamePhase;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.sign.SignManager;
import me.patothebest.skywars.arena.Arena;
import me.patothebest.skywars.arena.ArenaType;

import javax.inject.Inject;

public class SkyWarsGameBasePhase extends GamePhase<CorePlugin, Arena> implements SkyWarsPhase<Arena> {

    private int phaseTime;

    @Inject private SkyWarsGameBasePhase(CorePlugin plugin, Provider<NMS> nmsProvider, PlayerManager playerManager, SignManager signManager, UserGUIFactory userGUIFactory) {
        super(plugin, nmsProvider, signManager, playerManager, userGUIFactory);
    }

    @Override
    public void configure() {
        registerFeature(PetsFeature.class);
        registerFeature(DeathMessageFeature.class);
        registerFeature(NoBorderTrespassingFeature.class);
        registerFeature(ChestRefillFeature.class);
        registerFeature(NoInitialFallDamage.class);
        registerFeature(DeathSpectatorFeature.class);
        registerFeature(SpectatorProtectionFeature.class);
        registerFeature(SpectatorFeature.class);
        registerFeature(GameProtectionFeature.class);
        registerFeature(LimitedTimePhaseFeature.class);
        registerFeature(CompassRadarFeature.class);
        registerFeature(InformationalFeature.class);

        if(arena.getArenaType() == ArenaType.TEAM) {
            registerFeature(NoTeamDamageFeature.class);
        }
    }

    @Override
    public void start() {
        ((LimitedTimePhaseFeature)getFeatures().get(LimitedTimePhaseFeature.class)).setTimeUntilNextStage(phaseTime);
        super.start();
    }

    @Override
    public PhaseType getPhaseType() {
        return PhaseType.NONE;
    }

    /**
     * Sets the phsae time
     */
    @Override
    public void setTimeTilNextPhase(int phsaeTime) {
        this.phaseTime = phsaeTime;
    }
}