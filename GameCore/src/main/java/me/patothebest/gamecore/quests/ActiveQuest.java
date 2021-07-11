package me.patothebest.gamecore.quests;

import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.modifiers.QuestModifier;

public class ActiveQuest {

    private final IPlayer player;
    private final Quest quest;
    private final long startDate;
    private int entryId;
    private int progress;
    private QuestsStatus questsStatus;

    public ActiveQuest(IPlayer player, Quest quest, int entryId, long startDate, int progress, QuestsStatus questsStatus) {
        this.player = player;
        this.quest = quest;
        this.entryId = entryId;
        this.startDate = startDate;
        this.progress = progress;
        this.questsStatus = questsStatus;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public IPlayer getPlayer() {
        return player;
    }

    public Quest getQuest() {
        return quest;
    }

    public long getStartDate() {
        return startDate;
    }

    public int getProgress() {
        return progress;
    }

    public QuestsStatus getQuestsStatus() {
        return questsStatus;
    }

    public int getEntryId() {
        return entryId;
    }

    public void addProgress(int progress) {
        this.progress += progress;
        player.notifyObservers(QuestModifier.UPDATE_PROGRESS, this, progress);
    }

    public void setCompleted() {
        this.questsStatus = QuestsStatus.COMPLETED;
        player.notifyObservers(QuestModifier.UPDATE_STATUS, this);
    }

    public void setFailed() {
        this.questsStatus = QuestsStatus.FAILED;
        player.notifyObservers(QuestModifier.UPDATE_STATUS, this);
    }

    public long getDeadline() {
        return this.startDate + quest.getCooldown();
    }

    public boolean hasExpired() {
        return getDeadline() <= System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "ActiveQuest{" +
                "player=" + player +
                ", quest=" + quest +
                ", startDate=" + startDate +
                ", entryId=" + entryId +
                ", progress=" + progress +
                ", questsStatus=" + questsStatus +
                '}';
    }
}
