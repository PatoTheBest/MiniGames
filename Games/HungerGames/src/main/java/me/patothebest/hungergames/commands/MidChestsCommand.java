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
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.ArenaLocation;
import me.patothebest.gamecore.vector.Cuboid;
import me.patothebest.hungergames.arena.Arena;
import me.patothebest.hungergames.lang.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class MidChestsCommand implements ListenerModule {

    private final Map<Player, Arena> playerArenaMap = new HashMap<>();
    private final ArenaManager arenaManager;
    private final PluginScheduler pluginScheduler;
    private final Provider<SelectionManager> selectionManagerProvider;

    @Inject
    private MidChestsCommand(ArenaManager arenaManager, PluginScheduler pluginScheduler, Provider<SelectionManager> selectionManagerProvider) {
        this.arenaManager = arenaManager;
        this.pluginScheduler = pluginScheduler;
        this.selectionManagerProvider = selectionManagerProvider;
    }

    @ChildOf(SetupCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject
        private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = {"middlechest", "midchest"},
                langDescription = @LangDescription(
                        element = "MIDCHEST_DESC",
                        langClass = Lang.class
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = MidChestsCommand.class)
        public void midChest(CommandContext args, CommandSender sender) throws CommandException {
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

        if (event.getClickedBlock().getType() != Material.CHEST.parseMaterial() || event.getClickedBlock().getType() != Material.TRAPPED_CHEST.parseMaterial()) {
            return;
        }

        Arena arena = playerArenaMap.get(event.getPlayer());
        ArenaLocation arenaLocation = new ArenaLocation(arena, event.getClickedBlock().getLocation());

        if (!arena.getMidChestLocations().contains(arenaLocation)) {
            arena.getMidChestLocations().add(arenaLocation);
            ActionBar.sendActionBar(event.getPlayer(), Lang.MID_CHEST_ADDED);
            event.setCancelled(true);
        } else {
            arena.getMidChestLocations().remove(arenaLocation);
            ActionBar.sendActionBar(event.getPlayer(), Lang.MID_CHEST_REMOVED);
            event.setCancelled(true);
        }
    }

    @Command(
            aliases = {"toggle"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "MID_CHEST",
                    langClass = Lang.class
            )
    )
    public List<String> toggle(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        if (playerArenaMap.containsKey(player)) {
            playerArenaMap.remove(player);
            Lang.ADD_STOP_MID_CHEST.sendMessage(player);
        } else {
            playerArenaMap.put(player, arena);
            Lang.ADD_MID_CHEST.sendMessage(player);
        }

        return null;
    }

    @Command(
            aliases = {"scan"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "SCAN_CHESTS",
                    langClass = Lang.class
            )
    )
    public List<String> scanArea(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        Selection selection = selectionManagerProvider.get().getSelection(player);
        CommandUtils.validateTrue(selection != null && selection.arePointsSet(), CoreLang.SELECT_AN_AREA);
        Cuboid cuboid = selection.toCubiod("temp", arena);

        int blocksToScan = (cuboid.getUpperSW().getBlockX() - cuboid.getLowerNE().getBlockX()) * (cuboid.getUpperSW().getBlockZ() - cuboid.getLowerNE().getBlockZ()) * (cuboid.getUpperSW().getBlockY() - cuboid.getLowerNE().getBlockY());
        int blocksScanned = 0;
        int chestsFound = 0;
        double interval = blocksToScan / 100.0;
        long lastSent = 0;

        Lang.SCANNING_AREA.sendMessage(player);

        for (int x = cuboid.getLowerNE().getBlockX(); x < cuboid.getUpperSW().getBlockX(); x++) {
            for (int z = cuboid.getLowerNE().getBlockZ(); z < cuboid.getUpperSW().getBlockZ(); z++) {
                for (int y = cuboid.getLowerNE().getBlockY(); y < cuboid.getUpperSW().getBlockY(); y++) {
                    if (arena.getWorld().getBlockAt(x, y, z).getType() == Material.CHEST.parseMaterial() || arena.getWorld().getBlockAt(x, y, z).getType() == Material.TRAPPED_CHEST.parseMaterial()) {
                        ArenaLocation arenaLocation = new ArenaLocation(arena, arena.getWorld(), x, y, z);
                        arena.getMidChestLocations().add(arenaLocation);
                        chestsFound++;
                    }

                    blocksScanned++;
                    if (blocksScanned % interval < 1) {
                        double progress = (double) blocksScanned / blocksToScan;

                        if(System.currentTimeMillis() - 50 >= lastSent) {
                            Utils.displayProgress(Lang.SCANNING_AREA.getMessage(player), progress, Math.ceil(progress * 100.0) + "%", player);
                            lastSent = System.currentTimeMillis();
                        }
                    }
                }
            }
        }

        Utils.displayProgress(Lang.SCANNING_AREA.getMessage(player), 1, 100.0 + "%", player);
        Lang.SCANNED_AREA.replaceAndSend(player, chestsFound);
        return null;
    }


    @Command(
            aliases = {"list", "ls"},
            usage = "<arena> [page]",
            min = 1,
            max = 2,
            langDescription = @LangDescription(
                    element = "LIST_MID_CHEST",
                    langClass = Lang.class
            )
    )
    public List<String> listMidChests(CommandContext args, CommandSender sender) throws CommandException {
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
                return "Mid-Chests";
            }

            @Override
            protected String entry(ArenaLocation entry, int index, CommandSender commandSender) {
                return ChatColor.GRAY + "* " + ChatColor.GOLD.toString() + (index+1) + ". " + ChatColor.BLUE + "Mid-Chest "
                        + ChatColor.YELLOW + " Location: " + Utils.locationToCoords(entry, sender);
            }
        }.display(sender, arena.getMidChestLocations(), page);
        return null;
    }

    @Command(
            aliases = {"clear", "cl"},
            usage = "<arena>",
            min = 1,
            langDescription = @LangDescription(
                    element = "CLEAR_MID_CHESTS",
                    langClass = Lang.class
            )
    )
    public List<String> clearSpawns(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        arena.getMidChestLocations().clear();
        Lang.CLEARED_MID_CHEST.sendMessage(player);
        return null;
    }
}
