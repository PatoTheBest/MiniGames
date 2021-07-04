package me.patothebest.gamecore.quests.types;

import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.quests.AbstractQuestType;
import org.bukkit.event.EventHandler;

public class KillPlayersQuestType extends AbstractQuestType {

    @EventHandler
    public void onCombatDeath(CombatDeathEvent event) {
        if (event.getKillerPlayer() == null) {
            return;
        }

        IPlayer player = playerManager.getPlayer(event.getKillerPlayer());
        updateProgress((CorePlayer) player, 1);
    }

    @Override
    public String getName() {
        return "kill_players";
    }
}
