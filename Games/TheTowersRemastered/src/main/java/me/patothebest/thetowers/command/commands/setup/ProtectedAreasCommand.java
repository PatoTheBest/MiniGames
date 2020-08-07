package me.patothebest.thetowers.command.commands.setup;

import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.chat.Pagination;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.commands.setup.SetupCommand;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.Cuboid;
import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class ProtectedAreasCommand {

    private final ArenaManager arenaManager;
    private final Provider<SelectionManager> selectionManagerProvider;

    @Inject private ProtectedAreasCommand(ArenaManager arenaManager, Provider<SelectionManager> selectionManagerProvider) {
        this.arenaManager = arenaManager;
        this.selectionManagerProvider = selectionManagerProvider;
    }

    @ChildOf(SetupCommand.class)
    public static class Parent implements ParentCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = "protectedareas",
                langDescription = @LangDescription(
                        element = "PROTECTED_AREAS",
                        langClass = Lang.class
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = ProtectedAreasCommand.class)
        public void protectedAreas(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"list", "l"},
            usage = "<arena> [page]",
            min = 1,
            max = 2,
            langDescription = @LangDescription(
                    element = "PROTECTED_AREAS_LIST",
                    langClass = Lang.class
            )
    )
    public List<String> list(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);

        new Pagination<Cuboid>() {
            @Override
            protected String title() {
                return Lang.PROTECTED_AREA_LIST_HEADER.getMessage(player).replace("%arena%", arena.getName());
            }

            @Override
            protected String entry(Cuboid entry, int index, CommandSender commandSender) {
                return entry.toString(commandSender, index + 1);
            }
        }.display(sender, arena.getProtectedAreas(), args.getInteger(1, 1));
        return null;
    }


    @Command(
            aliases = {"protect", "protectarea"},
            usage = "<arena> <area name>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "PROTECT_AREA",
                    langClass = Lang.class
            )
    )
    public List<String> protect(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);

        Selection selection = selectionManagerProvider.get().getSelection(player);
        CommandUtils.validateTrue(selection != null && selection.arePointsSet(), CoreLang.SELECT_AN_AREA);

        // add the protected area
        arena.getProtectedAreas().add(selection.toCubiod(args.getString(1), arena));
        player.sendMessage(CoreLang.AREA_PROTECTED.getMessage(player));
        arena.save();
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"show", "outline", "s"},
            usage = "<arena> <area name>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "SHOW_PROTECTED_AREA",
                    langClass = Lang.class
            )
    )
    public List<String> show(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.complete(args.getString(0), arenaManager);
                case 1:
                    return CommandUtils.completeNameable(args.getString(1), ((Arena)CommandUtils.getArena(args, 0, arenaManager)).getProtectedAreas());
            }
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        Cuboid cuboid = Utils.getFromCollection(arena.getProtectedAreas(), args.getString(1));

        CommandUtils.validateNotNull(cuboid, CoreLang.AREA_DOES_NOT_EXIST);
        cuboid.show(player);
        player.sendMessage(Lang.PROTECTED_AREA_SHOW.getMessage(player));
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"hide", "h"},
            usage = "<arena> <area name>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "HIDE_PROTECTED_AREA",
                    langClass = Lang.class
            )
    )
    public List<String> hide(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.complete(args.getString(0), arenaManager);
                case 1:
                    return CommandUtils.completeNameable(args.getString(1), ((Arena)CommandUtils.getArena(args, 0, arenaManager)).getProtectedAreas());
            }
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        Cuboid cuboid = Utils.getFromCollection(arena.getProtectedAreas(), args.getString(1));

        CommandUtils.validateNotNull(cuboid, CoreLang.AREA_DOES_NOT_EXIST);
        cuboid.hide(player);
        player.sendMessage(Lang.PROTECTED_AREA_HIDE.getMessage(player));
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"remove", "delete"},
            usage = "<arena> <area name>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "REMOVE_PROTECTED_AREA",
                    langClass = Lang.class
            )
    )
    public List<String> delete(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.complete(args.getString(0), arenaManager);
                case 1:
                    return CommandUtils.completeNameable(args.getString(1), ((Arena)CommandUtils.getArena(args, 0, arenaManager)).getProtectedAreas());
            }
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        Cuboid cuboid = Utils.getFromCollection(arena.getProtectedAreas(), args.getString(1));

        CommandUtils.validateNotNull(cuboid, CoreLang.AREA_DOES_NOT_EXIST);

        // remove the protected area
        arena.getProtectedAreas().remove(cuboid);
        player.sendMessage(CoreLang.AREA_REMOVED.getMessage(player));
        arena.save();
        return null;
    }
}
