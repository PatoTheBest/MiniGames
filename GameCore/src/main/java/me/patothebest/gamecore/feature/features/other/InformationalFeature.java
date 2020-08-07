package me.patothebest.gamecore.feature.features.other;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.actionbar.ActionBar;
import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.util.Sounds;
import org.bukkit.event.EventHandler;

public class InformationalFeature extends AbstractFeature {

    @EventHandler
    public void onKill(CombatDeathEvent event) {
        if (event.getKillerPlayer() == null) {
            return;
        }

        Sounds.ENTITY_EXPERIENCE_ORB_PICKUP.play(event.getKillerPlayer(), 10f, 1f);
        ActionBar.sendActionBar(event.getKillerPlayer(), CoreLang.YOU_KILLED_ACTION_BAR.replace(event.getKillerPlayer(), event.getPlayer().getName()));
    }

}
