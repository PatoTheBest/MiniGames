package me.patothebest.gamecore.commands.admin;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.guis.AdminGUIFactory;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ChildOf(AdminCommand.class)
public class AdminArenaCommands implements RegisteredCommandModule {

    private final AdminGUIFactory adminGUIFactory;
    private final ArenaManager arenaManager;

    @Inject private AdminArenaCommands(AdminGUIFactory adminGUIFactory, ArenaManager arenaManager) {
        this.adminGUIFactory = adminGUIFactory;
        this.arenaManager = arenaManager;
    }

    @Command(
            aliases = {"join"},
            langDescription = @LangDescription(
                    element = "JOIN_ARENA",
                    langClass = CoreLang.class
            )
    )
    public List<String> joinArena(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        adminGUIFactory.createMenu(player);
        return null;
    }

    @Command(
            aliases = {"forcestart"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "FORCE_START",
                    langClass = CoreLang.class
            )
    )
    public List<String> forcestart(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            return CommandUtils.complete(args.getString(0), arenaManager
                    .getArenas()
                    .values()
                    .stream()
                    .filter(AbstractArena::isEnabled)
                    .filter(arena -> !arena.isInGame())
                    .map(AbstractArena::getName)
                    .collect(Collectors.toList()));
        }

        AbstractArena arena = CommandUtils.getEnabledArena(args, 0, arenaManager);
        CommandUtils.validateTrue(!arena.isInGame(), CoreLang.ARENA_ALREADY_STARTED);
        arena.nextPhase();
        CoreLang.ARENA_FORCE_STARTED.sendMessage(sender);
        return null;
    }

    @Command(
            aliases = {"forceend"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "FORCE_END",
                    langClass = CoreLang.class
            )
    )
    public List<String> forceend(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            return CommandUtils.complete(args.getString(0), arenaManager
                    .getArenas()
                    .values()
                    .stream()
                    .filter(AbstractArena::isEnabled)
                    .filter(AbstractArena::isInGame)
                    .map(AbstractArena::getName)
                    .collect(Collectors.toList()));
        }

        AbstractArena arena = CommandUtils.getEnabledArena(args, 0, arenaManager);
        CommandUtils.validateTrue(arena.isInGame(), CoreLang.ARENA_NOT_STARTED);
        // end the arena
        arena.endArena(true);
        CoreLang.ARENA_FORCE_ENDED.sendMessage(sender);
        return null;
    }
}
