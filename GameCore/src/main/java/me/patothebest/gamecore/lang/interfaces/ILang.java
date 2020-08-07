package me.patothebest.gamecore.lang.interfaces;

import me.patothebest.gamecore.lang.CoreLocaleManager;
import me.patothebest.gamecore.lang.Locale;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.NameableObject;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface ILang {

    default String getMessage(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            return getMessage(PlayerManager.get().getPlayer((Player) commandSender));
        }

        return getMessage(CoreLocaleManager.DEFAULT_LOCALE);
    }

    default String getMessage(Player player) {
        IPlayer player1 = PlayerManager.get().getPlayer(player);

        if (player == null || player1 == null) {
            System.err.println(name() + " - Error while getting messages, player is null!");
            return getMessage(CoreLocaleManager.DEFAULT_LOCALE);
        }

        return getMessage(player1);
    }

    default String getPath() {
        return name().toLowerCase().replace("_", "-");
    }

    default String getMessage(IPlayer player) {
        return getMessage(player.getLocale());
    }

    default void sendMessage(CommandSender commandSender) {
        commandSender.sendMessage(getMessage(commandSender));
    }

    default void sendMessage(IPlayer corePlayer) {
        corePlayer.sendMessage(getMessage(corePlayer));
    }


    default void replaceAndSend(CommandSender commandSender, Object... args) {
        if (commandSender instanceof Player) {
            replaceAndSend(PlayerManager.get().getPlayer((Player) commandSender), args);
            return;
        }

        commandSender.sendMessage(replace(CoreLocaleManager.DEFAULT_LOCALE, args));
    }


    default void replaceAndSend(Player player, Object... args) {
        replaceAndSend(PlayerManager.get().getPlayer(player), args);
    }

    default void replaceAndSend(IPlayer player, Object... args) {
        player.sendMessage(replace(player.getLocale(), args));
    }

    default String replace(CommandSender commandSender, Object... args) {
        if (commandSender instanceof Player) {
            return replace(PlayerManager.get().getPlayer((Player) commandSender), args);
        }

        return replace(CoreLocaleManager.DEFAULT_LOCALE, args);
    }


    default String replace(Player player, Object... args) {
        return replace(PlayerManager.get().getPlayer(player), args);
    }

    default String replace(IPlayer player, Object... args) {
        return replace(player.getLocale(), args);
    }

    default String replace(Locale locale, Object... args) {
        if (args.length != getArguments().length) {
            throw new IllegalArgumentException("Wrong arguments for Lang " + name() + ": entered " + args.length + ", expected " + getArguments().length);
        }

        String message = getMessage(locale);

        for (int i = 0; i < args.length; i++) {
            String replacement;
            if (args[i] instanceof String) {
                replacement = (String) args[i];
            } else if(args[i] instanceof ILang) {
                replacement = ((ILang)args[i]).getMessage(locale);
            } else if(args[i] instanceof NameableObject) {
                replacement = ((NameableObject)args[i]).getName();
            } else {
                replacement = String.valueOf(args[i]);
            }

            message = message.replace("%" + getArguments()[i] + "%", replacement);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    String getMessage(Locale locale);

    String name();

    boolean isComment();

    IComment getComment();

    String getRawMessage(Locale locale);

    String getDefaultMessage();

    void setMessage(Locale locale, String defaultMessage);

    String[] getArguments();

}
