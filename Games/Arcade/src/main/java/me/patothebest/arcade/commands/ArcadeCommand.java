package me.patothebest.arcade.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.arcade.Arcade;
import me.patothebest.arcade.lang.Lang;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.commands.DebugCommand;
import me.patothebest.gamecore.commands.admin.AdminCommand;
import me.patothebest.gamecore.commands.setup.SetupCommand;
import me.patothebest.gamecore.commands.user.ArenaUserCommands;
import me.patothebest.gamecore.commands.user.LocaleCommand;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import org.bukkit.command.CommandSender;

@Singleton
public class ArcadeCommand implements RegisteredCommandModule, BaseCommand {

    private final Arcade plugin;

    @Inject private ArcadeCommand(Arcade plugin) {
        this.plugin = plugin;
    }

    @Command(
            aliases = {"arcade", "sw"},
            langDescription = @LangDescription(
                    element = "COMMAND_DESC",
                    langClass = Lang.class
            )
    )
    @NestedCommand(
            defaultToBody = true,
            value = {
                    ArenaUserCommands.class,
                    LocaleCommand.class,
                    SetupCommand.class,
                    DebugCommand.class,
                    AdminCommand.class,
            }
    )
    public void skyWars(CommandContext args, CommandSender sender) throws CommandException {
        new CommandPagination(plugin.getCommandManager().getCommandManager(), args).showAdminInFooter(true).display(sender);
    }
}
