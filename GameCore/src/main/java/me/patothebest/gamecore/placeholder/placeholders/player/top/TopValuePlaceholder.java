package me.patothebest.gamecore.placeholder.placeholders.player.top;

import com.google.inject.Inject;
import me.patothebest.gamecore.leaderboards.LeaderboardManager;
import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.stats.StatsManager;

public class TopValuePlaceholder extends AbstractTopPlaceholder {

    @Inject private TopValuePlaceholder(StatsManager statsManager, LeaderboardManager leaderboardManager) {
        super(statsManager, leaderboardManager);
    }

    @Override
    public String getPlaceholderName() {
        return "top_value";
    }

    @Override
    String replace(TopEntry topEntry) {
        return topEntry.getAmount() + "";
    }
}
