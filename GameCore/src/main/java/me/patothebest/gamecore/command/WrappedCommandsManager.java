package me.patothebest.gamecore.command;

import me.patothebest.gamecore.lang.interfaces.ILang;

public class WrappedCommandsManager extends CommandsManager<WrappedCommandSender> {
    @Override
    public boolean hasPermission(WrappedCommandSender player, String perm) {
        return player.hasPermission(perm);
    }

    @Override
    public String getLocalizedMessage(WrappedCommandSender player, ILang message) {
        return null;
    }
}
