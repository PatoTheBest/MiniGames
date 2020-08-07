package me.patothebest.gamecore.commands.setup;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.commands.ConfirmCommand;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class ArenaSetupCommands {

    private final ArenaManager arenaManager;
    private final Provider<SelectionManager> selectionManagerProvider;
    private final ConfirmCommand confirmCommand;

    @Inject private ArenaSetupCommands(ArenaManager arenaManager, Provider<SelectionManager> selectionManagerProvider, ConfirmCommand confirmCommand) {
        this.arenaManager = arenaManager;
        this.selectionManagerProvider = selectionManagerProvider;
        this.confirmCommand = confirmCommand;
    }

    @Command(
            aliases = {"enablearena", "enable"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "ENABLE_ARENA",
                    langClass = CoreLang.class
            )
    )
    public List<String> enableArena(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);

        if(!arena.canArenaBeEnabled(player)) {
            return null;
        }

        // enable the arena
        arena.enableArena();
        player.sendMessage(CoreLang.ARENA_ENABLED.getMessage(player));
        arena.save();

        return null;
    }


    @Command(
            aliases = {"disablearena", "disable"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "",
                    langClass = CoreLang.class
            )
    )
    public List<String> disableArena(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getEnabledArena(args, 0, arenaManager);

        // disable the arena
        arena.disableArena();
        player.sendMessage(CoreLang.ARENA_DISABLED.getMessage(player));
        arena.save();
        return null;
    }

    @Command(
            aliases = {"setmaxplayers", "maxplayers"},
            usage = "<arena> <amount>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "SET_MAX_PLAYERS",
                    langClass = CoreLang.class
            )
    )
    public List<String> setMaxPlayers(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);

        // change the max player amount
        arena.setMaxPlayers(args.getInteger(1));
        player.sendMessage(CoreLang.MAX_PLAYERS_SET.getMessage(player));
        arena.save();
        return null;
    }

    @Command(
            aliases = {"setminplayers", "minplayers"},
            usage = "<arena> <amount>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "SET_MIN_PLAYERS",
                    langClass = CoreLang.class
            )
    )
    public List<String> setMinPlayers(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);

        // change the max player amount
        arena.setMinPlayers(args.getInteger(1));
        player.sendMessage(CoreLang.MAX_PLAYERS_SET.getMessage(player));
        arena.save();
        return null;
    }

    @Command(
            aliases = {"setarenaarea", "setarea"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "SET_ARENA_AREA",
                    langClass = CoreLang.class
            )
    )
    public List<String> setArenaArea(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);

        Selection selection = selectionManagerProvider.get().getSelection(player);
        CommandUtils.validateTrue(selection != null && selection.arePointsSet(), CoreLang.SELECT_AN_AREA);

        // set the arena area
        arena.setArea(selection.toCubiod(arena.getName(), arena));
        player.sendMessage(CoreLang.ARENA_AREA_SET.getMessage(player));
        arena.save();
        return null;
    }

    @Command(
            aliases = {"setlobbyarea"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "SET_LOBBY_AREA",
                    langClass = CoreLang.class
            )
    )
    public List<String> setLobbyArea(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);

        Selection selection = selectionManagerProvider.get().getSelection(player);
        CommandUtils.validateTrue(selection != null && selection.arePointsSet(), CoreLang.SELECT_AN_AREA);

        // set the arena area
        arena.setLobbyArea(selection.toCubiod(arena.getName(), arena));
        player.sendMessage(CoreLang.ARENA_LOBBY_AREA_SET.getMessage(player));
        arena.save();
        return null;
    }

    @Command(
            aliases = {"showarena", "showarenaarea"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "SHOW_ARENA_AREA",
                    langClass = CoreLang.class
            )
    )
    public List<String> showArenaArea(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        if(arena.getArea() == null) {
            player.sendMessage(CoreLang.NO_AREA_SET.getMessage(player));
        } else {
            arena.getArea().show(player);
            player.sendMessage(CoreLang.AREA_SHOWN.getMessage(player));
        }
        return null;
    }

    @Command(
            aliases = {"hidearena", "hidearenaarea"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "HIDE_ARENA_AREA",
                    langClass = CoreLang.class
            )
    )
    public List<String> hideArenaArea(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        arena.getArea().hide(player);
        player.sendMessage(CoreLang.AREA_HIDE.getMessage(player));
        return null;
    }

    @Command(
            aliases = {"setlobby", "setlobbylocation"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "SET_LOBBY_LOCATION",
                    langClass = CoreLang.class
            )
    )
    public List<String> setLobbyLocation(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);

        // set the lobby location of the arena
        arena.setLobbyLocation(player.getLocation());
        player.sendMessage(CoreLang.LOBBY_LOCATION_SET.getMessage(player));
        arena.save();
        return null;
    }


    @Command(
            aliases = {"setspec", "setspectator"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "SET_SPECTATOR_LOCATION",
                    langClass = CoreLang.class
            )
    )
    public List<String> setSpectatorLocation(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);

        // set the lobby location of the arena
        arena.setSpectatorLocation(player.getLocation());
        player.sendMessage(CoreLang.SPECTATOR_LOCATION_SET.getMessage(player));
        arena.save();
        return null;
    }

    @Command(
            aliases = {"teleport", "tp"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "TELEPORT_TO_ARENA",
                    langClass = CoreLang.class
            )
    )
    public List<String> teleport(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Player player = CommandUtils.getPlayer(sender);
        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);

        player.teleport(arena.getWorld().getSpawnLocation());
        player.sendMessage(CoreLang.TELEPORTED_TO_ARENA.getMessage(player));
        return null;
    }

    @Command(
            aliases = {"check", "revise", "isready", "ready"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "TELEPORT_TO_ARENA",
                    langClass = CoreLang.class
            )
    )
    public List<String> check(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        if (arena.canArenaBeEnabled(sender)) {
            CoreLang.READY_ARENA.sendMessage(sender);
        }
        return null;
    }

    @Command(
            aliases = {"delete", "remove"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "DELETE_ARENA",
                    langClass = CoreLang.class
            )
    )
    public List<String> delete(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        confirmCommand.addConfiration(sender, (commandSender) -> {
            arena.delete();
            arenaManager.getArenas().remove(arena.getName());
            CoreLang.ARENA_DELETED.sendMessage(commandSender);
        });

        return null;
    }

    @Command(
            aliases = {"clear"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "CLEAR_ARENA",
                    langClass = CoreLang.class
            )
    )
    public List<String> clear(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        confirmCommand.addConfiration(sender, (commandSender) -> {
            arena.getArenaFile().delete();
            arena.destroy();
            arenaManager.createArena(arena.getName());
            CoreLang.ARENA_CLEARED.sendMessage(commandSender);
        });

        return null;
    }
}