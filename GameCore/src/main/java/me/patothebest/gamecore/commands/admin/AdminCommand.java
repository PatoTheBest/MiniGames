package me.patothebest.gamecore.commands.admin;

import com.google.inject.Inject;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import org.bukkit.command.CommandSender;

import java.util.List;

@ChildOf(BaseCommand.class)
public class AdminCommand implements RegisteredCommandModule {

    private final CommandsManager<CommandSender> commandsManager;

    @Inject private AdminCommand(CommandsManager<CommandSender> commandsManager) {
        this.commandsManager = commandsManager;
    }


    @Command(
            aliases = {"admin"},
            langDescription = @LangDescription(
                    element = "ADMIN_COMMAND_DESC",
                    langClass = CoreLang.class
            )
    )
    @NestedCommand(defaultToBody = true)
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> execute(CommandContext args, CommandSender sender) throws CommandException {
        new CommandPagination(commandsManager, args).display(sender);
        return null;
    }
}