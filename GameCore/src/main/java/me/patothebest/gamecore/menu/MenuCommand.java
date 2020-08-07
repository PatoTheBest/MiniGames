package me.patothebest.gamecore.menu;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

public class MenuCommand implements Module {

    private final MenuManager menuManager;
    private final PlayerManager playerManager;

    @Inject private MenuCommand(MenuManager menuManager, PlayerManager playerManager) {
        this.menuManager = menuManager;
        this.playerManager = playerManager;
    }


    @ChildOf(BaseCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = "menu",
                langDescription = @LangDescription(
                        langClass = CoreLang.class,
                        element = "MENU_COMMAND_DESC"
                )
        )
        @NestedCommand(value = MenuCommand.class)
        public void signs(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"open"},
            usage = "<menu>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "MENU_COMMAND_OPEN",
                    langClass = CoreLang.class
            )
    )
    public List<String> open(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        if (args.getSuggestionContext() != null) {
            return CommandUtils.complete(args.getString(0), menuManager.getMenus().keySet());
        }

        Menu menu = menuManager.getMenus().get(args.getString(0));
        CommandUtils.validateNotNull(menu, CoreLang.MENU_NOT_FOUND);
        menu.open(player);
        return null;
    }
}
