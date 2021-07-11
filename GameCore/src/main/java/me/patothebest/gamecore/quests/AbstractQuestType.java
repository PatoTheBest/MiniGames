package me.patothebest.gamecore.quests;

import com.google.inject.Inject;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.PlayerManager;

import java.util.Set;

public abstract class AbstractQuestType implements QuestType {

    @Inject protected PlayerManager playerManager;
    @Inject protected EventRegistry eventRegistry;

    protected void updateProgress(CorePlayer player, int amount) {
        Set<ActiveQuest> activeQuests = player.getActiveQuests(this);
        for (ActiveQuest activeQuest : activeQuests) {
            if (activeQuest.getQuestsStatus() == QuestsStatus.IN_PROGRESS) {
                activeQuest.addProgress(amount);
                if (activeQuest.getProgress() >= activeQuest.getQuest().getGoal()) {
                    CoreLang.LINE_SEPARATOR.sendMessage(player);
                    player.sendMessage("");
                    CoreLang.QUEST_COMPLETED.replaceAndSend(player, activeQuest.getQuest().getDisplayName());
                    player.sendMessage("");
                    player.addExperience(activeQuest.getQuest().getXpReward());
                    player.giveMoney(activeQuest.getQuest().getMoneyReward());
                    activeQuest.setCompleted();
                    player.sendMessage("");
                    CoreLang.LINE_SEPARATOR.sendMessage(player);
                }
            }
        }
    }
}
