package me.patothebest.gamecore.sign;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.chat.Pagination;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChatColor;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.arena.ArenaManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

public class SignCommand implements Module {

    private final ArenaManager arenaManager;
    private final SignManager signManager;

    @Inject private SignCommand(ArenaManager arenaManager, SignManager signManager) {
        this.arenaManager = arenaManager;
        this.signManager = signManager;
    }

    @ChildOf(BaseCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = "signs",
                langDescription = @LangDescription(
                        langClass = CoreLang.class,
                        element = "SIGN_COMMAND_DESC"
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = SignCommand.class)
        public void signs(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"add", "createsign", "addsign"},
            usage = "<arena>",
            flags = "c",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "CREATE_SIGN"
            )
    )
    @CommandPermissions(permission = Permission.SETUP)
    public List<String> add(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Player player = CommandUtils.getPlayer(sender);
        AbstractArena arena = arenaManager.getArena(args.getString(0));
        if (arena == null && !args.hasFlag('c')) {
            CoreLang.ARENA_DOES_NOT_EXIST.sendMessage(player);
            CoreLang.SIGN_ADD_COMMAND_OVERRIDE.replaceAndSend(player, args.getString(0));
            return null;
        }

        // queue the player to select a sign
        signManager.getNewSigns().put(player.getName(), args.getString(0));
        player.sendMessage(CoreLang.SELECT_SIGN.getMessage(player));
        return null;
    }

    @Command(
            aliases = {"list", "listsigns", "signs", "l"},
            usage = "[arena] [page]",
            max = 2,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "LIST_SIGNS"
            )
    )
    @CommandPermissions(permission = Permission.SETUP)
    public List<String> list(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            return CommandUtils.complete(args.getString(0), Utils.toList(arenaManager.getArenas().values()));
        }

        List<ArenaSign> signs = signManager.getSigns();
        int page = 1;
        String arena = null;

        if(args.isInBounds(1)) {
            if(args.isInteger(0)) {
                page = args.getInteger(0);
                arena = args.getString(1);
            } else {
                page = args.getInteger(1);
                arena = args.getString(0);
            }
        } else if(args.isInBounds(0)) {
            if(args.isInteger(0)) {
                page = args.getInteger(0);
            } else {
                arena = args.getString(0);
            }
        }

        if(arena != null) {
            String finalArena = arena;
            signs.removeIf(arenaSign -> !arenaSign.getArena().equalsIgnoreCase(finalArena));
        }

        new Pagination<ArenaSign>() {
            @Override
            protected String title() {
                return "Signs";
            }

            @Override
            protected String entry(ArenaSign entry, int index, CommandSender commandSender) {
                return ChatColor.GRAY + "* " + ChatColor.GOLD.toString() + (index+1) + ". " + ChatColor.BLUE + entry.getArena()
                        + " " + ChatColor.YELLOW + " Location: " + Utils.locationToString(entry.getLocation(), sender);
            }
        }.display(sender, signs, page);

        return null;
    }
}
