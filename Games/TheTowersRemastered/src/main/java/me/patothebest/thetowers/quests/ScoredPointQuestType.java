package me.patothebest.thetowers.quests;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.quests.AbstractQuestType;
import net.megaplanet.thetowers.api.events.PlayerScoreEvent;
import org.bukkit.event.EventHandler;

public class ScoredPointQuestType extends AbstractQuestType {

    @EventHandler
    public void onScore(PlayerScoreEvent event) {
        IPlayer player = playerManager.getPlayer(event.getPlayer());
        updateProgress((CorePlayer) player, 1);
    }

    @Override
    public String getName() {
        return "score_points";
    }
}
