package me.patothebest.gamecore.quests;

import me.patothebest.gamecore.player.IPlayer;

import java.util.Date;

public class ActiveQuest {

    private final IPlayer player;
    private final Quest quest;
    private final Date startDate;
    private double progress;
    private boolean completed;

    public ActiveQuest(IPlayer player, Quest quest, Date startDate) {
        this.player = player;
        this.quest = quest;
        this.startDate = startDate;
    }

    public IPlayer getPlayer() {
        return player;
    }

    public Quest getQuest() {
        return quest;
    }

    public Date getStartDate() {
        return startDate;
    }

    public double getProgress() {
        return progress;
    }

    public boolean isCompleted() {
        return completed;
    }
}
