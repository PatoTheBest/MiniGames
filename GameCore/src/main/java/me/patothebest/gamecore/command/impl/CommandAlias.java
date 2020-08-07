package me.patothebest.gamecore.command.impl;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Hacky class to have command aliases for commands such
 * as /night to be redirected to /time night and so on
 *
 * @author PatoTheBest
 */
public class CommandAlias extends Command {

    private final String destCommand;

    public CommandAlias(String[] commandAliases, String destCommand) {
        super(commandAliases[0], "Alias para " + destCommand, "/" + commandAliases[0], Arrays.asList(commandAliases));
        this.destCommand = destCommand;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        Bukkit.dispatchCommand(sender, destCommand + " " +  StringUtils.join(args, " "));
        return true;
    }
}
