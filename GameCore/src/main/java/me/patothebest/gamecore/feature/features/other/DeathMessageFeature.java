package me.patothebest.gamecore.feature.features.other;

import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.feature.AbstractFeature;
import org.bukkit.event.EventHandler;

public class DeathMessageFeature extends AbstractFeature {

    @EventHandler
    public void onDeath(CombatDeathEvent event) {
        if(!isPlayingInArena(event.getPlayer()) && !isSpectatingInArena(event.getPlayer())) {
            return;
        }

        arena.sendMessageToArena(locale -> event.getDeathMessage());
        event.setDeathMessage(null);
    }
}
