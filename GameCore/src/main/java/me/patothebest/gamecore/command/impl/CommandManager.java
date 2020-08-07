package me.patothebest.gamecore.command.impl;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.command.CommandsManager;
import org.bukkit.command.CommandSender;

public class CommandManager extends CommandsManagerRegistration<CommandSender> {

    public CommandManager(CorePlugin abstractJavaPlugin) {
        super(abstractJavaPlugin, new BukkitCommandsManager());
    }

    public CommandsManager<CommandSender> getCommandManager() {
        return commands;
    }
}
