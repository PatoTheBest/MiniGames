package me.patothebest.gamecore.quests.ui;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.quests.ActiveQuest;
import me.patothebest.gamecore.quests.QuestManager;
import me.patothebest.gamecore.quests.QuestsStatus;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class QuestsGUI extends GUIPage {

    private final PlayerManager playerManager;
    private final QuestManager questManager;

    @AssistedInject QuestsGUI(Plugin plugin, @Assisted Player player, PlayerManager playerManager, QuestManager questManager) {
        super(plugin, player, CoreLang.GUI_QUEST_TITLE, Utils.transformToInventorySize(questManager.getQuestMap().size()));
        this.playerManager = playerManager;
        this.questManager = questManager;
        build();
    }

    @Override
    protected void buildPage() {
        IPlayer player = playerManager.getPlayer(this.player);

        questManager.getQuestMap().forEach((s, quest) -> {
            ActiveQuest activeQuest = player.getActiveQuest(quest);
            ItemStackBuilder item = new ItemStackBuilder()
                    .name(CoreLang.GUI_QUEST_ITEM_NAME.replace(this.player, quest.getDisplayName()))
                    .material(Material.MAP);
            if (activeQuest == null || activeQuest.hasExpired()) {
                item.lore(CoreLang.GUI_QUEST_STATUS_START.getMessage(player));
            } else if (activeQuest.getQuestsStatus() == QuestsStatus.IN_PROGRESS) {
                String deadline = Utils.createTime(player.getPlayer(), activeQuest.getDeadline() - System.currentTimeMillis());
                item.lore(CoreLang.GUI_QUEST_STATUS_STARTED.getMessage(player))
                        .blankLine()
                        .addLore(CoreLang.GUI_QUEST_STATUS_PROGRESS.replace(player, activeQuest.getProgress(), activeQuest.getQuest().getGoal()))
                        .blankLine()
                        .addLore(CoreLang.GUI_QUEST_STATUS_DEADLINE.replace(player, deadline));
            } else {
                String cooldown = Utils.createTime(player.getPlayer(), activeQuest.getDeadline() - System.currentTimeMillis());
                item.lore(CoreLang.GUI_QUEST_STATUS_COMPLETED.getMessage(player))
                        .blankLine()
                        .addLore(CoreLang.GUI_QUEST_STATUS_COOLDOWN.getMessage(player))
                        .addLore(CoreLang.GUI_QUEST_STATUS_COOLDOWN_2.replace(player, cooldown));
            }
            item.blankLine()
                    .addLore(CoreLang.GUI_QUEST_STATUS_EXP.replace(player, quest.getXpReward()))
                    .addLore(CoreLang.GUI_QUEST_STATUS_COINS.replace(player, quest.getMoneyReward()));

            for (String loreLine : quest.getExtraLore()) {
                item.addLore(ChatColor.translateAlternateColorCodes('&', loreLine));
            }

            addButton(new SimpleButton(item).action(() -> {
                if (activeQuest == null || activeQuest.hasExpired()) {
                    ActiveQuest newQuest = new ActiveQuest(player, quest, -1, System.currentTimeMillis(), 0, QuestsStatus.IN_PROGRESS);
                    player.activateQuest(newQuest);
                    CoreLang.GUI_QUEST_STARTED.replaceAndSend(player, quest.getDisplayName());
                    refresh();
                }
            }));
        });
    }
}
