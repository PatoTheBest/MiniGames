package me.patothebest.thetowers.command;

import com.google.inject.Singleton;
import me.patothebest.gamecore.commands.DebugCommand;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.commands.admin.AdminCommand;
import me.patothebest.gamecore.commands.setup.SetupCommand;
import me.patothebest.gamecore.commands.user.ArenaUserCommands;
import me.patothebest.gamecore.commands.user.LocaleCommand;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.sign.SignCommand;
import me.patothebest.thetowers.TheTowersRemastered;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

@Singleton
public class CommandManager implements RegisteredCommandModule, BaseCommand {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final TheTowersRemastered plugin;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private CommandManager(TheTowersRemastered plugin) {
        this.plugin = plugin;
    }

    // -------------------------------------------- //
    // PUBLIC METHODS
    // -------------------------------------------- //

    @Command(
            aliases = {"thetowers", "tt"},
            langDescription = @LangDescription(
                langClass = Lang.class,
                element = "COMMAND_DESCRIPTION"
            )
    )
    @NestedCommand(
            defaultToBody = true,
            value = {
                    SignCommand.Parent.class,
                    ArenaUserCommands.class,
                    LocaleCommand.class,
                    SetupCommand.class,
                    DebugCommand.class,
                    AdminCommand.class,
            }
    )
    public void theTowers(CommandContext args, CommandSender sender) throws CommandException {
        new CommandPagination(plugin.getCommandManager().getCommandManager(), args).showAdminInFooter(true).display(sender);
    }

    // -------------------------------------------- //
    // GETTERS
    // -------------------------------------------- //

    public TheTowersRemastered getPlugin() {
        return plugin;
    }
}
