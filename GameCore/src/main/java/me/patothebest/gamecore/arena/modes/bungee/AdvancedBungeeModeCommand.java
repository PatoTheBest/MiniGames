package me.patothebest.gamecore.arena.modes.bungee;

import com.google.inject.Inject;
import me.patothebest.gamecore.lang.CoreLang;
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
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AdvancedBungeeModeCommand implements ParentCommandModule {

    private final AdvancedBungeeMode bungeeMode;

    @Inject private AdvancedBungeeModeCommand(AdvancedBungeeMode bungeeMode) {
        this.bungeeMode = bungeeMode;
    }

    @ChildOf(BaseCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject
        private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = "bungee",
                langDescription = @LangDescription(
                        langClass = CoreLang.class,
                        element = "BUNGEE_COMMANDS_DESC"
                )
        )
        @CommandPermissions(permission = Permission.ADMIN)
        @NestedCommand(value = AdvancedBungeeModeCommand.class)
        public void signs(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"changemap"},
            usage = "<world> <arena>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "CHANGE_ARENA",
                    langClass = CoreLang.class
            )
    )
    public List<String> changeArena(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.validateTrue(bungeeMode.isEnabled(), CoreLang.NOT_IN_BUNGEE_MODE);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), bungeeMode.getArenas().keySet());
            } else if(args.getSuggestionContext().getIndex() == 1) {
                return CommandUtils.complete(args.getString(1), bungeeMode.getEnabledArenas());
            }

            return null;
        }

        String world = args.getString(0);
        String arena = args.getString(1);
        CommandUtils.validateTrue(bungeeMode.getArenas().containsKey(world), CoreLang.INVALID_ARENA);
        CommandUtils.validateTrue(bungeeMode.getEnabledArenas().contains(arena), CoreLang.INVALID_ARENA);
        bungeeMode.changeArena(world, arena);
        return null;
    }

    @Command(
            aliases = {"restart"},
            langDescription = @LangDescription(
                    element = "RESTART_SERVER",
                    langClass = CoreLang.class
            )
    )
    public void restartServer(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.validateTrue(bungeeMode.isEnabled(), CoreLang.NOT_IN_BUNGEE_MODE);

        if(Bukkit.getOnlinePlayers().size() == 0) {
            Bukkit.shutdown();
            return;
        }

        bungeeMode.restartASAP();
    }

    @Command(
            aliases = {"addplayer"},
            langDescription = @LangDescription(
                    element = "ADD_PLAYER",
                    langClass = CoreLang.class
            ),
            min = 2,
            max = 2
    )
    public List<String> addplayer(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.validateTrue(bungeeMode.isEnabled(), CoreLang.NOT_IN_BUNGEE_MODE);
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), bungeeMode.getArenas().keySet());
            }

            return null;
        }

        String world = args.getString(0);
        CommandUtils.validateTrue(bungeeMode.getArenas().containsKey(world), CoreLang.INVALID_ARENA);
        bungeeMode.getPlayerCache().put(args.getString(1), bungeeMode.getArenas().get(world));

        return null;
    }
}