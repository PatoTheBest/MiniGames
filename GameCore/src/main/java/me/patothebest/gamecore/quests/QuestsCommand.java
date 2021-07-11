package me.patothebest.gamecore.quests;

import com.google.inject.Inject;
import me.patothebest.gamecore.command.*;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.quests.ui.QuestGUIFactory;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@ChildOf(BaseCommand.class)
public class QuestsCommand implements RegisteredCommandModule {

    private final QuestGUIFactory questGUIFactory;

    @Inject private QuestsCommand(QuestGUIFactory questGUIFactory) {
        this.questGUIFactory = questGUIFactory;
    }

    @Command(
            aliases = {"quests"},
            langDescription = @LangDescription(
                    element = "QUESTS_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public void quests(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        questGUIFactory.createQuestGUI(player);
    }


}
