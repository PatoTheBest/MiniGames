package me.patothebest.gamecore.commands.user;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.guis.user.ChooseLocale;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class LocaleCommand {

    private final CorePlugin plugin;
    private final PlayerManager playerManager;

    @Inject private LocaleCommand(CorePlugin plugin, PlayerManager playerManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;
    }

    @Command(
            aliases = {"lang", "language", "locale"},
            max = 0,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "LOCALE_COMMAND"
            )
    )
    public void openLocale(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        new ChooseLocale(plugin, playerManager, player);
    }
}
