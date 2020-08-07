package me.patothebest.arcade.game.commands;

import com.google.inject.Inject;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.permission.Permission;
import org.bukkit.command.CommandSender;

public class GameCommand implements RegisteredCommandModule {

    private final CommandsManager<CommandSender> commandsManager;

    @Inject
    private GameCommand(CommandsManager<CommandSender> commandsManager) {
        this.commandsManager = commandsManager;
    }

    @Command(
            aliases = "games",
            langDescription = @LangDescription(
                langClass = CoreLang.class,
                element = "COMMAND_DESC"
            )
    )
    @NestedCommand(defaultToBody = true)
    @CommandPermissions(permission = Permission.SETUP)
    public void games(CommandContext args, CommandSender sender) throws CommandException {
        new CommandPagination(commandsManager, args).display(sender);
    }
}