package me.patothebest.skywars.commands;

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
import me.patothebest.skywars.arena.Arena;
import me.patothebest.skywars.arena.ArenaType;
import me.patothebest.skywars.lang.Lang;
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
public class SpawnsCommand implements ListenerModule {

    private final Map<Player, Arena> playerArenaMap = new HashMap<>();
    private final ArenaManager arenaManager;

    @Inject
    private SpawnsCommand(ArenaManager arenaManager) {
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
                aliases = {"spawns"},
                langDescription = @LangDescription(
                        element = "SPAWNS_DESC",
                        langClass = Lang.class
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = SpawnsCommand.class)
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

        Arena arena = playerArenaMap.get(event.getPlayer());
        ArenaLocation spawnLocation = new ArenaLocation(arena, event.getClickedBlock().getLocation());

        if (!arena.getSpawns().contains(spawnLocation)) {
            arena.getSpawns().add(spawnLocation);
            ActionBar.sendActionBar(event.getPlayer(), Lang.SPAWN_ADDED.replace(event.getPlayer(), arena.getSpawns().size()-1));
            event.setCancelled(true);
        } else {
            int i = arena.getSpawns().indexOf(spawnLocation);
            arena.getSpawns().remove(spawnLocation);
            ActionBar.sendActionBar(event.getPlayer(), Lang.SPAWN_REMOVED.replace(event.getPlayer(), i));
            event.setCancelled(true);
        }
    }

    @Command(
            aliases = {"toggle"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "ADD_SPAWNS_DESC",
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
        CommandUtils.validateTrue(arena.getArenaType() == ArenaType.SOLO, Lang.ARENA_MUST_BE_SOLO);

        if (playerArenaMap.containsKey(player)) {
            playerArenaMap.remove(player);
            Lang.ADD_STOP_SPAWNS.sendMessage(player);
        } else {
            playerArenaMap.put(player, arena);
            Lang.ADD_SPAWNS.sendMessage(player);
        }

        return null;
    }

    @Command(
            aliases = {"add", "addspawn"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "ADD_SPAWN",
                    langClass = Lang.class
            )
    )
    public List<String> addSpawn(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        CommandUtils.validateTrue(arena.getArenaType() == ArenaType.SOLO, Lang.ARENA_MUST_BE_SOLO);

        arena.getSpawns().add(new ArenaLocation(arena, player.getLocation()));
        player.sendMessage(Lang.SPAWN_ADDED.replace(player, arena.getSpawns().size()));
        return null;
    }

    @Command(
            aliases = {"remove", "delete"},
            usage = "<arena> <id>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "REMOVE_SPAWN",
                    langClass = Lang.class
            )
    )
    public List<String> removeSpawn(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        CommandUtils.validateTrue(arena.getArenaType() == ArenaType.SOLO, Lang.ARENA_MUST_BE_SOLO);

        int index = args.getInteger(1);
        CommandUtils.validateTrue(index <= arena.getSpawns().size() && index > 0, CoreLang.OUT_OF_BOUNDS);

        arena.getSpawns().remove(index-1);
        Lang.SPAWN_REMOVED.replaceAndSend(player, index);
        return null;
    }

    @Command(
            aliases = {"list", "ls"},
            usage = "<arena> [page]",
            min = 1,
            max = 2,
            langDescription = @LangDescription(
                    element = "LIST_SPAWN",
                    langClass = Lang.class
            )
    )
    public List<String> listSpawns(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        CommandUtils.validateTrue(arena.getArenaType() == ArenaType.SOLO, Lang.ARENA_MUST_BE_SOLO);
        int page = args.getInteger(1, 1);

        new Pagination<ArenaLocation>() {
            @Override
            protected String title() {
                return "Spawns";
            }

            @Override
            protected String entry(ArenaLocation entry, int index, CommandSender commandSender) {
                return ChatColor.GRAY + "* " + ChatColor.GOLD.toString() + (index+1) + ". " + ChatColor.BLUE + "Spawn "
                        + ChatColor.YELLOW + " Location: " + Utils.locationToCoords(entry, sender);
            }
        }.display(sender, arena.getSpawns(), page);
        return null;
    }

    @Command(
            aliases = {"clear", "cl"},
            usage = "<arena>",
            min = 1,
            langDescription = @LangDescription(
                    element = "CLEAR_SPAWNS",
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
        CommandUtils.validateTrue(arena.getArenaType() == ArenaType.SOLO, Lang.ARENA_MUST_BE_SOLO);
        arena.getSpawns().clear();
        Lang.CLEARED_SPAWNS.sendMessage(player);
        return null;
    }

    @Command(
            aliases = {"spawnheight", "height", "setspawnheight", "setheight"},
            usage = "<arena> <height>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "SPAWN_HEIGHT",
                    langClass = Lang.class
            )
    )
    public List<String> spawnHeight(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        arena.setSpawnHeight(args.getInteger(1));
        Lang.SET_SPAWN_HEIGHT.sendMessage(player);
        return null;
    }
}
