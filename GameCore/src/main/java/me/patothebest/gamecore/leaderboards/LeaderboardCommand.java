package me.patothebest.gamecore.leaderboards;

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
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.permission.Permission;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

@ChildOf(BaseCommand.class)
public class LeaderboardCommand implements RegisteredCommandModule {

    private final CommandsManager<CommandSender> commandsManager;

    @Inject
    private LeaderboardCommand(CommandsManager<CommandSender> commandsManager) {
        this.commandsManager = commandsManager;
    }

    @Command(
            aliases = "leaderboard",
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "LEADERBOARD_COMMAND_DESC"
            )
    )
    @CommandPermissions(permission = Permission.SETUP)
    @NestedCommand
    public void leaderboards(CommandContext args, CommandSender sender) throws CommandException {
        new CommandPagination(commandsManager, args).display(sender);
    }
}