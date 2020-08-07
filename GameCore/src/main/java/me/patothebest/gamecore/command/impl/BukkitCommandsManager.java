package me.patothebest.gamecore.command.impl;

import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.command.CommandsManager;
import org.bukkit.command.CommandSender;

public class BukkitCommandsManager extends CommandsManager<CommandSender> {

    @Override
    public boolean hasPermission(CommandSender player, String perm) {
        return player.hasPermission(perm);
    }

    @Override
    public String getLocalizedMessage(CommandSender player, ILang message) {
        return message.getMessage(player);
    }
}
