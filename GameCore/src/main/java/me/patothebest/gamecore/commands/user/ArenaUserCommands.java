package me.patothebest.gamecore.commands.user;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.event.player.RandomArenaJoinEvent;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

public class ArenaUserCommands {

    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;
    private final UserGUIFactory userGUIFactory;
    private final EventRegistry eventRegistry;

    @Inject private ArenaUserCommands(PlayerManager playerManager, ArenaManager arenaManager, UserGUIFactory userGUIFactory, CorePlugin corePlugin, EventRegistry eventRegistry) {
        this.playerManager = playerManager;
        this.arenaManager = arenaManager;
        this.userGUIFactory = userGUIFactory;
        this.eventRegistry = eventRegistry;
    }

    @Command(
            aliases = {"join", "j"},
            usage = "[arena] [-r(andom)]",
            flags = "r",
            max = 1,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "JOIN_ARENA"
            )
    )
    public List<String> joinCommand(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            return CommandUtils.complete(args.getString(0, ""), Utils.toList(arenaManager.getArenas().values()));
        }

        if (args.hasFlag('r')) {
            List<AbstractArena> joinableArenas = arenaManager.getJoinableArenas(player, arena -> !arena.isFull());
            CommandUtils.validateTrue(!joinableArenas.isEmpty(), CoreLang.NO_ARENAS);
            RandomArenaJoinEvent arenaJoinEvent = eventRegistry.callEvent(new RandomArenaJoinEvent(player, joinableArenas.get(0)));
            CommandUtils.validateNotNull(arenaJoinEvent.getArena(), CoreLang.NO_ARENAS);
            AbstractArena currentArena = playerManager.getPlayer(player.getName()).getCurrentArena();
            if (currentArena == arenaJoinEvent.getArena()) {
                return null;
            }
            if (currentArena != null) {
                currentArena.removePlayer(player);
            }
            arenaJoinEvent.getArena().addPlayer(player);
            return null;
        }

        // check args
        if (!args.isInBounds(0)) {
            userGUIFactory.createJoinArenaUI(player);
            return null;
        }

        CommandUtils.validateTrue(playerManager.getPlayer(player.getName()).getCurrentArena() == null, CoreLang.ALREADY_IN_ARENA);
        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        CommandUtils.validateTrue(arena.isEnabled(), CoreLang.ARENA_IS_NOT_PLAYABLE);
        CommandUtils.validateTrue(arena.canJoinArena(), CoreLang.ARENA_IS_RESTARTING);

        if (arena.getPhase().canJoin()) {
            if (arena.isFull()) {
                player.sendMessage(CoreLang.ARENA_IS_FULL.getMessage(player));
                return null;
            }

            arena.addPlayer(player);
        } else if(arena.canJoinArena()){
            player.sendMessage(CoreLang.ARENA_HAS_STARTED.getMessage(player));
        } else {
            player.sendMessage(CoreLang.ARENA_IS_RESTARTING.getMessage(player));
        }

        return null;
    }

    @Command(
            aliases = {"spec", "spectate", "s"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "SPEC_ARENA"
            )
    )
    public List<String> spectateCommand(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            return CommandUtils.complete(args.getString(0), Utils.toList(arenaManager.getArenas().values()));
        }

        CommandUtils.validateTrue(playerManager.getPlayer(player.getName()).getCurrentArena() == null, CoreLang.ALREADY_IN_ARENA);
        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        CommandUtils.validateTrue(arena.isEnabled(), CoreLang.ARENA_IS_NOT_PLAYABLE);
        CommandUtils.validateTrue(arena.canJoinArena(), CoreLang.ARENA_IS_RESTARTING);
        arena.addSpectator(player);

        return null;
    }

    @Command(
            aliases = {"leave", "l"},
            max = 0,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "LEAVE_ARENA"
            )
    )
    public List<String> leaveCommand(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        AbstractArena arena = playerManager.getPlayer(player.getName()).getCurrentArena();
        CommandUtils.validateNotNull(arena, CoreLang.NOT_IN_AN_ARENA);
        arena.removePlayer(player);
        return null;
    }

    @Command(
            aliases = {"list", "arenas"},
            max = 0,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "LIST_ARENAS"
            )
    )
    public List<String> listArenas(CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage("§6Oo-----------------------oOo-----------------------oO");
        for (AbstractArena arena : arenaManager.getArenas().values()) {
            sender.sendMessage(arena.getWorldName() + " " + (arena.isEnabled() ? ChatColor.GREEN + CoreLang.ARENA_ENABLED_SHORT.getMessage(sender) : ChatColor.RED + CoreLang.ARENA_DISABLED_SHORT.getMessage(sender)));
        }
        sender.sendMessage("§6Oo-----------------------oOo-----------------------oO");
        return null;
    }
}
