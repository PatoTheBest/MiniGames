package me.patothebest.gamecore.quests;

import me.patothebest.gamecore.util.Utils;

import java.util.Map;

public class Quest {

    private final String name;
    private final QuestType questType;
    private final int goal;
    private int duration;
    private int cooldown;

    // only two types of rewards
    private int xpReward;
    private int moneyReward;

    public Quest(String name, QuestType questType, int goal) {
        this.name = name;
        this.questType = questType;
        this.goal = goal;
    }

    public Quest(Map<String, Object> questData) {
        this.name = (String) questData.get("name");
        this.goal = (int) questData.get("goal");
        this.questType = Utils.getEnumValueFromString(QuestType.class, (String) questData.get("type"));
        this.duration = (int) questData.get("duration");
        this.cooldown = (int) questData.get("cooldown");
        this.xpReward = (int) questData.getOrDefault("xp-reward", 0);
        this.moneyReward = (int) questData.getOrDefault("money-reward", 0);
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

    public int getDuration() {
        return duration;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getXpReward() {
        return xpReward;
    }

    public int getMoneyReward() {
        return moneyReward;
    }
}
