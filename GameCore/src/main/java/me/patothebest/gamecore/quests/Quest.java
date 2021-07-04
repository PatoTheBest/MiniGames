package me.patothebest.gamecore.quests;

import me.patothebest.gamecore.file.ParserException;
import me.patothebest.gamecore.util.Utils;

import java.util.Map;

public class Quest {

    private final String name;
    private final String displayName;
    private final QuestType questType;
    private final int goal;
    private final long duration;
    private final long cooldown;

    // only two types of rewards
    private final int xpReward;
    private final int moneyReward;

    public Quest(QuestManager questManager, Map<String, Object> questData) {
        this.name = (String) questData.get("name");
        this.displayName = (String) questData.get("display-name");
        this.goal = (int) questData.get("goal");
        this.questType = questManager.getQuestType((String) questData.get("type"));
        this.duration = Utils.dateStringToMillis((String) questData.get("duration"));
        this.cooldown = Utils.dateStringToMillis((String) questData.get("cooldown"));
        this.xpReward = (int) questData.getOrDefault("xp-reward", 0);
        this.moneyReward = (int) questData.getOrDefault("money-reward", 0);

        if (this.questType == null) {
            throw new ParserException("Invalid quest-type: " + questData.get("type"));
        }
    }

    public String getName() {
        return name;
    }

    public QuestType getQuestType() {
        return questType;
    }

    public int getGoal() {
        return goal;
    }

    public long getDuration() {
        return duration;
    }

    public long getCooldown() {
        return cooldown;
    }

    public int getXpReward() {
        return xpReward;
    }

    public int getMoneyReward() {
        return moneyReward;
    }

    public String getDisplayName() {
        return displayName;
    }
}
