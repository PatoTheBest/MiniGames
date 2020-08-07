package me.patothebest.gamecore.placeholder.placeholders.player.top;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.leaderboards.LeaderboardManager;
import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.StatsManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.stats.StatPeriod;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public abstract class AbstractTopPlaceholder implements PlaceHolder {

    private final StatsManager statsManager;
    private final LeaderboardManager leaderboardManager;

    public AbstractTopPlaceholder(StatsManager statsManager, LeaderboardManager leaderboardManager) {
        this.statsManager = statsManager;
        this.leaderboardManager = leaderboardManager;
    }

    @Override
    public final String replace(Player player, String args) {
        if(args == null || !args.contains("#") || !args.contains("_")) {
            return "USAGE eg. {top_name:week_kills#1}";
        }

        String period = args.substring(0, args.indexOf("_"));
        String statName = args.substring(args.indexOf("_") + 1, args.indexOf("#"));
        String number = args.substring(args.indexOf("#") + 1);

        if (!Utils.isNumber(number)) {
            return "USAGE eg. {top_name:week_kills#1}";
        }

        int position = Integer.parseInt(number) - 1;

        Statistic statistic = statsManager.getStatisticByName(statName);

        if(statistic == null) {
            return "Unknown stat " + statName;
        }

        if (position > 9) {
            return "Max top is #10!";
        }

        StatPeriod statTime = Utils.getEnumValueFromString(StatPeriod.class, period);

        if (statTime == null) {
            return "USAGE eg. {top_name:week_kills#1}";
        }

        if (!leaderboardManager.hasLoadedAll()) {
            return "Loading...";
        }

        LinkedList<TopEntry> topEntries = leaderboardManager.getTop10Map().get(statistic.getClass()).get(statTime);
        if (topEntries.size() <= position) {
            return "";
        }

        TopEntry topEntry = topEntries.get(position);
        return topEntry != null ? replace(topEntry) : "None";
    }

    abstract String replace(TopEntry topEntry);

    @Override
    public final String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
