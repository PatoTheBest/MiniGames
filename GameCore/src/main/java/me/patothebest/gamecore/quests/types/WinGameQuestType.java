package me.patothebest.gamecore.quests.types;

import me.patothebest.gamecore.event.arena.GameEndEvent;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.quests.AbstractQuestType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class WinGameQuestType extends AbstractQuestType {

    @EventHandler
    public void onWin(GameEndEvent event) {
        for (Player winner : event.getWinners()) {
            IPlayer player = playerManager.getPlayer(winner);
            updateProgress((CorePlayer) player, 1);
        }
    }

    @Override
    public String getName() {
        return "win_games";
    }
}
