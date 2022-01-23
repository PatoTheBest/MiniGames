package me.patothebest.gamecore.commands.admin;

import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.guis.AdminGUIFactory;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

@ChildOf(AdminCommand.class)
public class AdminArenaCommands implements RegisteredCommandModule {

    private final AdminGUIFactory adminGUIFactory;

    @Inject private AdminArenaCommands(AdminGUIFactory adminGUIFactory) {
        this.adminGUIFactory = adminGUIFactory;
    }

    @Command(
            aliases = {"join"},
            langDescription = @LangDescription(
                    element = "JOIN_ARENA",
                    langClass = CoreLang.class
            )
    )
    public List<String> joinArena(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        adminGUIFactory.createMenu(player);
        return null;
    }
}
