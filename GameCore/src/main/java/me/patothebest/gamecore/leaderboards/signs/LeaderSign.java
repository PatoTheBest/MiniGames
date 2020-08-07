package me.patothebest.gamecore.leaderboards.signs;

import me.patothebest.gamecore.file.ParserValidations;
import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.stats.StatPeriod;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.StatsManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.util.SerializableObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LeaderSign implements SerializableObject {

    private final LeaderSignsManager leaderSignsManager;
    private final Location location;
    private final Statistic statistic;
    private final StatPeriod period;
    private final int place; // place number, not index
    private final Set<Attachment> attachmentSet = new HashSet<>();

    public LeaderSign(LeaderSignsManager leaderSignsManager, Location location, Statistic statistic, StatPeriod period, int place) {
        this.leaderSignsManager = leaderSignsManager;
        this.location = location;
        this.statistic = statistic;
        this.period = period;
        this.place = place;
    }

    @SuppressWarnings("unchecked")
    public LeaderSign(LeaderSignsManager leaderSignsManager, StatsManager statsManager, Map<String, Object> data) {
        this.leaderSignsManager = leaderSignsManager;
        this.location = Location.deserialize((Map<String, Object>) data.get("location"));
        this.statistic = statsManager.getStatisticByName((String) data.get("stat"));
        this.place = (int) data.get("place");
        this.period = Utils.getEnumValueFromString(StatPeriod.class, (String) data.get("period"));

        if (data.get("attachments") != null) {
            List<Map<String, Object>> attachments = (List<Map<String, Object>>) data.get("attachments");

            for (Map<String, Object> attachmentData : attachments) {
                String type = (String) attachmentData.get("type");
                AttachmentType attachmentType = Utils.getEnumValueFromString(AttachmentType.class, type);
                ParserValidations.isTrue(attachmentType.canBeUsed(), "Missing dependency for " + attachmentType.name() +  "!");
                Attachment attachment = leaderSignsManager.createAttachment(attachmentType);
                attachment.parse(attachmentData);
                attachmentSet.add(attachment);
            }
        }
    }

    public void update() {
        if(!(location.getBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) location.getBlock().getState();

        LinkedList<TopEntry> topEntries = leaderSignsManager.getLeaderboardManager().getTop10Map().get(statistic.getClass()).get(period);
        TopEntry topEntry = (topEntries.size() >= place ? topEntries.get(place - 1) : null);

        if (topEntry == null) {
            for (int i = 0; i < 4; i++) {
                String line = leaderSignsManager.getFallbackSignTemplate()[i];
                sign.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
            }
        } else {
            for (int i = 0; i < 4; i++) {
                String line = leaderSignsManager.getSignTemplate()[i]
                        .replace("%place%", String.valueOf(place))
                        .replace("%player_name%", topEntry.getName())
                        .replace("%stat_name%", statistic.getStatName())
                        .replace("%amount%", String.valueOf(topEntry.getAmount()));
                sign.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
            }
        }

        sign.update(true, false);

        for (Attachment attachment : attachmentSet) {
            attachment.update(this, topEntry);
        }
    }

    @Override
    public void serialize(Map<String, Object> data) {
        data.put("stat", statistic.getStatName());
        data.put("place", place);
        data.put("period", period.name());
        data.put("location", location.serialize());

        if (!attachmentSet.isEmpty()) {
            List<Map<String, Object>> attachments = new ArrayList<>();
            for (Attachment attachment : attachmentSet) {
                attachments.add(attachment.serialize());
            }
            data.put("attachments", attachments);
        }
    }

    @Override
    public String toString() {
        return "LeaderSign{" +
                "location=" + location +
                ", statistic=" + statistic +
                ", period=" + period +
                ", place=" + place +
                '}';
    }

    public Location getLocation() {
        return location;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public StatPeriod getPeriod() {
        return period;
    }

    public int getPlace() {
        return place;
    }

    public Set<Attachment> getAttachmentSet() {
        return attachmentSet;
    }

    public void destroy() {
        for (Attachment attachment : attachmentSet) {
            attachment.destroy();
        }
    }
}
