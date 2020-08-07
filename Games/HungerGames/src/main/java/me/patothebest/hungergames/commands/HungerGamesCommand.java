package me.patothebest.hungergames.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
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
import me.patothebest.hungergames.lang.Lang;
import me.patothebest.hungergames.HungerGames;
import org.bukkit.command.CommandSender;

@Singleton
public class HungerGamesCommand implements RegisteredCommandModule, BaseCommand {

    private final HungerGames plugin;

    @Inject private HungerGamesCommand(HungerGames plugin) {
        this.plugin = plugin;
    }

    @Command(
            aliases = {"hungergames", "hg"},
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
                    JoinArenaOverrideCommand.class
            }
    )
    public void hungerGames(CommandContext args, CommandSender sender) throws CommandException {
        new CommandPagination(plugin.getCommandManager().getCommandManager(), args).showAdminInFooter(true).display(sender);
    }
}
