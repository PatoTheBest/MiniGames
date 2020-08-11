package me.patothebest.gamecore.privatearenas;

import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.privatearenas.ui.PrivateArenaUIFactory;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@ChildOf(BaseCommand.class)
public class PrivateArenasCommand implements RegisteredCommandModule {

    private final PrivateArenasManager privateArenasManager;
    private final PrivateArenaUIFactory privateArenaUIFactory;

    @Inject private PrivateArenasCommand(PrivateArenasManager privateArenasManager, PrivateArenaUIFactory privateArenaUIFactory) {
        this.privateArenasManager = privateArenasManager;
        this.privateArenaUIFactory = privateArenaUIFactory;
    }

    @Command(
            aliases = {"private", "privatearena"},
            min = 0,
            max = 0,
            langDescription = @LangDescription(
                    element = "GUI_PRIVATE_ARENA_COMMAND",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public void create(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        privateArenasManager.createPrivateArena(player);
    }

    @Command(
            aliases = {"privatemenu", "pm"},
            usage = "",
            min = 0,
            max = -1,
            langDescription = @LangDescription(
                    element = "",
                    langClass = CoreLang.class
            )
    )
    public void testmenu(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        PrivateArena privateArena = privateArenasManager.getPrivateArenaMap().get(player.getName());
        if (privateArena == null) {
            return;
        }
        privateArenaUIFactory.createPrivateArenaMenu(player, privateArena);
    }
}