package me.patothebest.gamecore.quests;

import me.patothebest.gamecore.file.ParserException;
import me.patothebest.gamecore.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Quest {

    private final String name;
    private final String displayName;
    private final List<String> extraLore;
    private final QuestType questType;
    private final int goal;
    private final long duration;
    private final long cooldown;

    // only two types of rewards
    private final int xpReward;
    private final int moneyReward;
    private final List<String> commandReward;

    public Quest(QuestManager questManager, String name, Map<String, Object> questData) {
        this.name = name;
        this.displayName = (String) questData.get("display-name");
        this.goal = (int) questData.get("goal");
        this.questType = questManager.getQuestType((String) questData.get("type"));
        this.duration = Utils.dateStringToMillis((String) questData.get("duration"));
        this.cooldown = Utils.dateStringToMillis((String) questData.get("cooldown"));
        this.xpReward = (int) questData.getOrDefault("xp-reward", 0);
        this.moneyReward = (int) questData.getOrDefault("money-reward", 0);
        Object commandRewardObject = questData.get("command-reward");
        Object extraLore = questData.get("extra-lore");

        if (this.questType == null) {
            throw new ParserException("Invalid quest-type: " + questData.get("type"));
        }

        this.commandReward = parseList(commandRewardObject);
        this.extraLore = parseList(extraLore);
    }

    private List<String> parseList(Object object) {
        if (object == null) {
            return Collections.emptyList();
        } else if (object instanceof String) {
            return Collections.singletonList(((String) object));
        } else if (object instanceof List) {
            return (List<String>) object;
        } else {
            throw new ParserException("Invalid command-reward: " + object);
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

    public List<String> getCommandReward() {
        return commandReward;
    }

    public List<String> getExtraLore() {
        return extraLore;
    }
}
