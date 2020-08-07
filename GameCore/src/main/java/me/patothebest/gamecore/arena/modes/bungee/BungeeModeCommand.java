package me.patothebest.gamecore.arena.modes.bungee;

import com.google.inject.Inject;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.commands.admin.AdminCommand;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

@ChildOf(value = AdminCommand.class)
public class BungeeModeCommand implements ParentCommandModule {

    private final BungeeMode bungeeMode;

    @Inject private BungeeModeCommand(BungeeMode bungeeMode) {
        this.bungeeMode = bungeeMode;
    }

    @Command(
            aliases = {"changearena"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "CHANGE_ARENA",
                    langClass = CoreLang.class
            )
    )
    public List<String> changeArena(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.validateTrue(bungeeMode.isEnabled(), CoreLang.NOT_IN_BUNGEE_MODE);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), bungeeMode.getEnabledArenas());
            }

            return null;
        }

        String arena = args.getString(0);
        CommandUtils.validateTrue(bungeeMode.getEnabledArenas().contains(arena), CoreLang.INVALID_ARENA);
        bungeeMode.changeArena(arena);
        return null;
    }

    @Command(
            aliases = {"restart"},
            langDescription = @LangDescription(
                    element = "RESTART_SERVER",
                    langClass = CoreLang.class
            )
    )
    public void restartServer(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.validateTrue(bungeeMode.isEnabled(), CoreLang.NOT_IN_BUNGEE_MODE);

        if(Bukkit.getOnlinePlayers().size() == 0) {
            Bukkit.shutdown();
            return;
        }

        bungeeMode.restartASAP();
    }
}