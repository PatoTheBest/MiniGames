package me.patothebest.gamecore.util;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.arena.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandUtils {

    public static void validateNotNull(Object object, String message) throws CommandException {
        if(object == null) {
            throw new CommandException(message);
        }
    }

    public static void validateTrue(boolean isTrue, String message) throws CommandException {
        if(!isTrue) {
            throw new CommandException(message);
        }
    }

    public static void validateNotNull(Object object, ILang message) throws CommandException {
        if(object == null) {
            throw new CommandException(message);
        }
    }

    public static void validateTrue(boolean isTrue, ILang message) throws CommandException {
        if(!isTrue) {
            throw new CommandException(message);
        }
    }

    public static List<String> complete(String string, Enum[] values) {
        return complete(string, Stream.of(values).map(Enum::name).collect(Collectors.toList()));
    }

    public static List<String> complete(String startingString, ArenaManager arenaManager) {
        return completeNameable(startingString, arenaManager.getArenas().values());
    }

    public static List<String> completeNameable(String startingString, Collection<? extends NameableObject> listOfObjects) {
        return complete(startingString, Utils.toList(listOfObjects));
    }

    public static List<String> completeNameable(String startingString, List<? extends NameableObject> listOfObjects) {
        return complete(startingString, Utils.toList(listOfObjects));
    }

    public static List<String> completePlayers(String startingString) {
        return StringUtil.complete(startingString, Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
    }

    public static List<String> complete(String startingString, Iterable<String> completableStrings) {
        return StringUtil.complete(startingString, completableStrings);
    }

    public static Player getPlayer(CommandSender sender) throws CommandException {
        if(sender instanceof Player) {
            return (Player) sender;
        }

        throw new CommandException("This command can only be executed by players!");
    }

    public static Player getPlayer(CommandContext args, int arg) throws CommandException {
        Player player = Bukkit.getPlayer(args.getString(arg));

        if(player == null) {
            throw new CommandException("El jugador '" + args.getString(arg) + "' no esta en linea!");
        }

        return player;
    }

    public static <Arena extends AbstractArena> Arena getArena(CommandContext args, int arg, ArenaManager arenaManager) throws CommandException {
        Arena arena = (Arena) arenaManager.getArena(args.getString(arg));

        if(arena == null) {
            throw new CommandException(CoreLang.ARENA_DOES_NOT_EXIST);
        }

        return arena;
    }

    public static <Arena extends AbstractArena> Arena getDisabledArena(CommandContext args, int arg, ArenaManager arenaManager) throws CommandException {
        Arena arena = getArena(args, arg, arenaManager);

        if(arena.isEnabled()) {
            throw new CommandException(CoreLang.DISABLE_ARENA_FIRST);
        }

        return arena;
    }
    public static <Arena extends AbstractArena> Arena getEnabledArena(CommandContext args, int arg, ArenaManager arenaManager) throws CommandException {
        Arena arena = getArena(args, arg, arenaManager);

        if(!arena.isEnabled()) {
            throw new CommandException(CoreLang.ENABLE_ARENA_FIRST);
        }

        return arena;
    }


    public static AbstractGameTeam getTeam(AbstractArena arena, CommandContext args, int arg) throws CommandException {
        AbstractGameTeam gameTeam = arena.getTeam(args.getString(arg));

        if(gameTeam == null) {
            throw new CommandException(CoreLang.TEAM_DOES_NOT_EXIST);
        }

        return gameTeam;
    }

    public static Player getPlayer(CommandContext args, int arg, Player fallback) throws CommandException {
        if(!args.isInBounds(arg)) {
            return fallback;
        }

        Player player = Bukkit.getPlayer(args.getString(arg));

        if(player == null) {
            throw new CommandException("The player '" + args.getString(arg) + "' is not online!");
        }

        return player;
    }

    public static IPlayer getPlayer(CommandContext args, PlayerManager playerManager, int arg) throws CommandException {
        IPlayer player = playerManager.getPlayer(args.getString(arg));

        if(player == null) {
            throw new CommandException("The player '" + args.getString(arg) + "' is not online!");
        }

        return player;
    }

    public static <E extends Enum<E>> E getEnumValueFromString(Class<E> enumClass, String enumString, ILang errorMessage) throws CommandException {
        for (E enumElement : EnumSet.allOf(enumClass)) {
            if (enumElement.name().equalsIgnoreCase(enumString)) {
                return enumElement;
            }
        }

        throw new CommandException(errorMessage);
    }
}
