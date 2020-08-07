package me.patothebest.gamecore.leaderboards.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.leaderboards.LeaderboardManager;
import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.stats.StatPeriod;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.StatsManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.util.SerializableObject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.Map;

public class HolographicStat implements SerializableObject {

    private final Hologram hologram;
    private final LeaderHologram parent;
    private final Statistic statistic;
    private final StatPeriod period;
    private final String title;
    private final String style;
    private final String none;
    private final String change;
    private final LeaderboardManager leaderboardManager;

    @AssistedInject private HolographicStat(Plugin plugin, CoreConfig config, @Assisted LeaderHologram parent, @Assisted Statistic statistic, @Assisted StatPeriod period, @Assisted String title, LeaderboardManager leaderboardManager) {
        this.parent = parent;
        this.statistic = statistic;
        this.period = period;
        this.title = title;
        this.leaderboardManager = leaderboardManager;
        this.hologram = HologramsAPI.createHologram(plugin, parent.getHologramLocation());
        this.style = config.getString("stat-holograms.style");
        this.none = config.getString("stat-holograms.none");
        this.change = config.getString("stat-holograms.change");
    }

    @AssistedInject private HolographicStat(Plugin plugin, CoreConfig config, @Assisted LeaderHologram parent, StatsManager statsManager, @Assisted Map<String, Object> data, LeaderboardManager leaderboardManager) {
        this.parent = parent;
        this.leaderboardManager = leaderboardManager;
        this.hologram = HologramsAPI.createHologram(plugin, parent.getHologramLocation());
        this.style = config.getString("stat-holograms.style");
        this.none = config.getString("stat-holograms.none");
        this.change = config.getString("stat-holograms.change");

        this.statistic = statsManager.getStatisticByName((String) data.get("stat"));
        this.period = Utils.getEnumValueFromString(StatPeriod.class, (String) data.get("period"));
        this.title = (String) data.get("title");
    }

    public void updateStats() {
        hologram.clearLines();
        hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', title))
                .setTouchHandler(parent.getTouchHandler());
        LinkedList<TopEntry> topEntries = leaderboardManager.getTop10Map().get(statistic.getClass()).get(period);

        for (int i = 0; i < parent.getAmountToDisplay(); i++) {
            String base;
            if (topEntries.size() > i) {
                TopEntry topEntry = topEntries.get(i);
                base = style.replace("%player_name%", topEntry.getName())
                            .replace("%amount%", String.valueOf(topEntry.getAmount()));
            } else {
                base = none;
            }
            base = base.replace("%place%", String.valueOf(i + 1));
            hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', base))
                    .setTouchHandler(parent.getTouchHandler());
        }

        if (parent.hasPages()) {
            hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', change))
                    .setTouchHandler(parent.getTouchHandler());
        }
    }

    public void updateLocation() {
        hologram.teleport(parent.getHologramLocation());
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void serialize(Map<String, Object> data) {
        data.put("stat", statistic.getName());
        data.put("period", period.name());
        data.put("title", title);
    }

    public void setDefaultVisible(boolean value) {
        hologram.getVisibilityManager().setVisibleByDefault(value);
    }

    public void showToPlayer(Player player) {
        hologram.getVisibilityManager().showTo(player);
    }

    public void hideFromPlayer(Player player) {
        hologram.getVisibilityManager().hideTo(player);
    }

    public void destroy() {
        hologram.delete();
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public StatPeriod getPeriod() {
        return period;
    }
}
