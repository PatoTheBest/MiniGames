package me.patothebest.gamecore.leaderboards.holograms;

import me.patothebest.gamecore.stats.StatPeriod;
import me.patothebest.gamecore.stats.Statistic;
import org.bukkit.Location;

import java.util.Map;

public interface HolographicFactory {

    HolographicStat createHoloStat(LeaderHologram parent, Statistic statistic, StatPeriod period, String title);

    HolographicStat createHoloStat(LeaderHologram parent, Map<String, Object> data);

    LeaderHologram createLeaderHologram(String name, Location location);

    LeaderHologram createLeaderHologram(Map<String, Object> data);
}
