package me.patothebest.gamecore.commands.setup;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class GeneralSetupCommands {

    private final CoreConfig coreConfig;

    @Inject private GeneralSetupCommands(CoreConfig coreConfig) {
        this.coreConfig = coreConfig;
    }

    @Command(
            aliases = {"setmainlobby"},
            max = 0,
            langDescription = @LangDescription(
                    element = "SET_MAIN_LOBBY",
                    langClass = CoreLang.class
            )
    )
    public void setMainLobby(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        // set the main lobby in the config cache and file
        coreConfig.setUseMainLobby(true);
        coreConfig.setMainLobby(player.getLocation());
        coreConfig.set("MainLobby", coreConfig.getMainLobby().serialize());
        coreConfig.set("teleport.location", "MainLobby");

        // save the file
        coreConfig.save();
        player.sendMessage(CoreLang.MAIN_LOBBY_SET.getMessage(player));
    }


    @Command(
            aliases = {"spawn", "lobby", "mainlobby"},
            max = 0,
            langDescription = @LangDescription(
                    element = "TP_MAIN_LOBBY",
                    langClass = CoreLang.class
            )
    )
    public void spawn(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (coreConfig.isUseMainLobby() && coreConfig.getMainLobby() != null) {
            player.teleport(coreConfig.getMainLobby());
            player.sendMessage(CoreLang.MAIN_LOBBY_TP.getMessage(player));
        } else {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
    }
}