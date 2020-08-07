package me.patothebest.skywars.commands;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaGroup;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandOverride;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.event.player.RandomArenaJoinEvent;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.skywars.arena.Arena;
import me.patothebest.skywars.arena.ArenaType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class JoinArenaOverrideCommand {

    private final EventRegistry eventRegistry;
    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;
    private final UserGUIFactory userGUIFactory;
    private final static Comparator<AbstractArena> COMPARATOR = Comparator.comparingInt(arena2 -> -arena2.getPlayers().size());

    @Inject private JoinArenaOverrideCommand(EventRegistry eventRegistry, PlayerManager playerManager, ArenaManager arenaManager, UserGUIFactory userGUIFactory) {
        this.eventRegistry = eventRegistry;
        this.playerManager = playerManager;
        this.arenaManager = arenaManager;
        this.userGUIFactory = userGUIFactory;
    }

    @Command(
            aliases = {"join", "j"},
            usage = "[arena|solo|team] [-r(andom)]",
            flags = "r",
            max = 1,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "JOIN_ARENA"
            )
    )
    @CommandOverride
    public List<String> joinCommand(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            return CommandUtils.complete(args.getString(0, ""), Utils.toList(arenaManager.getArenas().values()));
        }

        ArenaGroup arenaGroup = null;
        if (args.isInBounds(0)) {
            arenaGroup = arenaManager.getGroup(args.getString(0));
        }

        if (args.hasFlag('r')) {
            Arena currentArena = (Arena) playerManager.getPlayer(player.getName()).getCurrentArena();
            List<AbstractArena> joinableArenas;
            if (currentArena != null) {
                arenaGroup = currentArena.getArenaGroup();
            }

            if (arenaGroup != null) {
                ArenaGroup finalArenaGroup = arenaGroup;
                joinableArenas = arenaManager.getJoinableArenas(player, arena -> arena.getArenaGroup() == finalArenaGroup && !arena.isFull());
            } else {
                joinableArenas = arenaManager.getJoinableArenas(player, arena -> !arena.isFull());
            }

            CommandUtils.validateTrue(!joinableArenas.isEmpty(), CoreLang.NO_ARENAS);
            RandomArenaJoinEvent arenaJoinEvent = eventRegistry.callEvent(new RandomArenaJoinEvent(player, joinableArenas.get(0)));
            CommandUtils.validateNotNull(arenaJoinEvent.getArena(), CoreLang.NO_ARENAS);
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

        if (arenaGroup != null) {
            ArenaGroup finalArenaGroup1 = arenaGroup;
            userGUIFactory.createJoinArenaUI(player, abstractArena -> ((Arena)abstractArena).getArenaType() == finalArenaGroup1);
            return null;
        }

        // requirements
        if (playerManager.getPlayer(player.getName()).getCurrentArena() != null) {
            player.sendMessage(CoreLang.ALREADY_IN_ARENA.getMessage(player));
            return null;
        }

        Arena arena = (Arena) arenaManager.getArena(args.getString(0));

        if (arena == null) {
            if (args.getString(0).equalsIgnoreCase("random-solo") || args.getString(0).equalsIgnoreCase("random-team")) {
                ArenaGroup arenaType = (args.getString(0).equalsIgnoreCase("random-solo")) ? ArenaType.SOLO : ArenaType.TEAM;
                Optional<AbstractArena> min = arenaManager.getArenas()
                        .values()
                        .stream()
                        .filter(o -> ((Arena)o).getArenaType() == arenaType && o.canJoinArena() && o.isEnabled())
                        .min(COMPARATOR);

                if (min.isPresent()) {
                    arena = (Arena) min.get();
                } else {
                    player.sendMessage(CoreLang.NO_ARENAS.getMessage(player));
                    return null;
                }
            } else {
                player.sendMessage(CoreLang.ARENA_DOES_NOT_EXIST.getMessage(player));
                return null;
            }
        }

        if (!arena.isEnabled()) {
            player.sendMessage(CoreLang.ARENA_IS_NOT_PLAYABLE.getMessage(player));
            return null;
        }

        if (arena.getPhase().canJoin()) {
            if (arena.isFull()) {
                player.sendMessage(CoreLang.ARENA_IS_FULL.getMessage(player));
                return null;
            }

            arena.addPlayer(player);
        } else if (arena.canJoinArena()) {
            player.sendMessage(CoreLang.ARENA_HAS_STARTED.getMessage(player));
        } else {
            player.sendMessage(CoreLang.ARENA_IS_RESTARTING.getMessage(player));
        }

        return null;
    }
}