package me.patothebest.hungergames.commands;

import me.patothebest.gamecore.actionbar.ActionBar;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.chat.Pagination;
import me.patothebest.gamecore.command.ChatColor;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.commands.setup.SetupCommand;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.ArenaLocation;
import me.patothebest.hungergames.arena.Arena;
import me.patothebest.hungergames.lang.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class SupplyDropsCommand implements ListenerModule {

    private final Map<Player, Arena> playerArenaMap = new HashMap<>();
    private final ArenaManager arenaManager;

    @Inject
    private SupplyDropsCommand(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @ChildOf(SetupCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject
        private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = {"supplydrops", "drops"},
                langDescription = @LangDescription(
                        element = "SUPPLY_DROP_COMMAND",
                        langClass = Lang.class
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = SupplyDropsCommand.class)
        public void supplyDrops(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }


    @EventHandler
    public void listen(PlayerInteractEvent event) {
        if (!playerArenaMap.containsKey(event.getPlayer())) {
            return;
        }

        if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR.parseMaterial()) {
            return;
        }

        Arena arena = playerArenaMap.remove(event.getPlayer());
        ArenaLocation supplyDropLocation = new ArenaLocation(arena, event.getClickedBlock().getLocation());

        if (!arena.getSupplyDrops().contains(supplyDropLocation)) {
            arena.getSupplyDrops().add(supplyDropLocation);
            ActionBar.sendActionBar(event.getPlayer(), Lang.SUPPLY_DROP_ADDED.getMessage(event.getPlayer()));
        } else {
            int i = arena.getSupplyDrops().indexOf(supplyDropLocation);
            arena.getSupplyDrops().remove(supplyDropLocation);
            ActionBar.sendActionBar(event.getPlayer(), Lang.SUPPLY_DROP_REMOVED.getMessage(event.getPlayer()));
        }
        event.setCancelled(true);
    }

    @Command(
            aliases = {"add"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "SUPPLY_DROP_ADD",
                    langClass = Lang.class
            )
    )
    public List<String> addSupplyDrop(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        playerArenaMap.put(player, arena);
        Lang.SUPPLY_DROP_CLICK_TO_ADD.replaceAndSend(player);
        return null;
    }

    @Command(
            aliases = {"remove", "delete"},
            usage = "<arena> <id>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "SUPPLY_DROP_REMOVE",
                    langClass = Lang.class
            )
    )
    public List<String> removeSupplyDrop(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        int index = args.getInteger(1);
        CommandUtils.validateTrue(index <= arena.getSupplyDrops().size() && index > 0, CoreLang.OUT_OF_BOUNDS);

        arena.getSupplyDrops().remove(index-1);
        Lang.SUPPLY_DROP_REMOVED.replaceAndSend(player, index);
        return null;
    }

    @Command(
            aliases = {"list", "ls"},
            usage = "<arena> [page]",
            min = 1,
            max = 2,
            langDescription = @LangDescription(
                    element = "SUPPLY_DROP_LIST",
                    langClass = Lang.class
            )
    )
    public List<String> listSupplyDrops(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        int page = args.getInteger(1, 1);

        new Pagination<ArenaLocation>() {
            @Override
            protected String title() {
                return "Supply Drops";
            }

            @Override
            protected String entry(ArenaLocation entry, int index, CommandSender commandSender) {
                return ChatColor.GRAY + "* " + ChatColor.GOLD.toString() + (index+1) + ". " + ChatColor.BLUE + "Drop "
                        + ChatColor.YELLOW + " Location: " + Utils.locationToCoords(entry, sender);
            }
        }.display(sender, arena.getSupplyDrops(), page);
        return null;
    }

    @Command(
            aliases = {"clear", "cl"},
            usage = "<arena>",
            min = 1,
            langDescription = @LangDescription(
                    element = "SUPPLY_DROP_CLEAR",
                    langClass = Lang.class
            )
    )
    public List<String> clearSupplydrops(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        arena.getSupplyDrops().clear();
        Lang.SUPPLY_DROP_CLEARED.sendMessage(player);
        return null;
    }
}
