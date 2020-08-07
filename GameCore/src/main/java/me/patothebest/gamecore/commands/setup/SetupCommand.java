package me.patothebest.gamecore.commands.setup;

import com.google.inject.Inject;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import org.bukkit.command.CommandSender;

public class SetupCommand {

    private final CommandsManager<CommandSender> commandsManager;

    @Inject
    private SetupCommand(CommandsManager<CommandSender> commandsManager) {
        this.commandsManager = commandsManager;
    }

    @Command(
            aliases = "setup",
            langDescription = @LangDescription(
                langClass = CoreLang.class,
                element = "SETUP_COMMAND_DESCRIPTION"
            )
    )
    @CommandPermissions(permission = Permission.SETUP)
    @NestedCommand(value = {
            ArenaSetupCommands.class,
            GeneralSetupCommands.class
        },
        defaultToBody = true
    )
    public void signs(CommandContext args, CommandSender sender) throws CommandException {
        new CommandPagination(commandsManager, args).display(sender);
    }
}