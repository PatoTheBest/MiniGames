package me.patothebest.gamecore.quests.ui;

import me.patothebest.gamecore.quests.ui.QuestsGUI;
import org.bukkit.entity.Player;

public interface QuestGUIFactory {

    QuestsGUI createQuestGUI(Player player);

}
