// $Id$
/*
 * WorldEdit
 * Copyright (C) 2011 sk89q <http://www.sk89q.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package me.patothebest.gamecore.command.impl;

import me.patothebest.gamecore.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zml2008
 */
public class CommandRegistration {

    static {
      Bukkit.getServer().getHelpMap().registerHelpTopicFactory(DynamicPluginCommand.class, new DynamicPluginCommandHelpTopic.Factory());
    }

	protected final Plugin plugin;
    protected final CommandExecutor executor;
    protected final TabCompleter completer;
	private CommandMap fallbackCommands;

    public CommandRegistration(Plugin plugin) {
        this(plugin, plugin, plugin);
    }

    public CommandRegistration(Plugin plugin, CommandExecutor executor) {
        this(plugin, executor, null);
    }

    public CommandRegistration(Plugin plugin, CommandExecutor executor, @Nullable TabCompleter completer) {
        this.plugin = plugin;
        this.executor = executor;
        this.completer = completer;
    }

    public boolean register(List<CommandInfo> registered) {
        CommandMap commandMap = getCommandMap();
        if (registered == null || commandMap == null) {
            return false;
        }

        for (CommandInfo command : registered) {
            DynamicPluginCommand cmd = new DynamicPluginCommand(command.getAliases(),
                                                                command.getDesc(),
                                                                "/" + command.getAliases()[0] + " " + command.getUsage(),
                                                                executor,
                                                                completer,
                                                                command.getRegisteredWith(),
                                                                plugin);
            cmd.setPermissions(command.getPermissions());
            commandMap.register(plugin.getDescription().getName(), cmd);
        }

        return true;
    }

    public boolean registerAlias(String[] commandAlias, String destinationCommand) {
        return register(new CommandAlias(commandAlias, destinationCommand));
    }

    public boolean register(CommandAlias commandAlias) {
        CommandMap commandMap = getCommandMap();
        if (commandAlias == null || commandMap == null) {
            return false;
        }

        commandMap.register(plugin.getDescription().getName(), commandAlias);
        return true;
    }

    public CommandMap getCommandMap() {
        CommandMap commandMap = ReflectionUtil.getField(plugin.getServer().getPluginManager(), "commandMap");
        if (commandMap == null) {
            if (fallbackCommands != null) {
                commandMap = fallbackCommands;
            } else {
                Bukkit.getServer().getLogger().severe(plugin.getDescription().getName() +
                        ": Could not retrieve server CommandMap, using fallback instead! Please report to Pato");
                fallbackCommands = commandMap = new SimpleCommandMap(Bukkit.getServer());
                Bukkit.getServer().getPluginManager().registerEvents(new FallbackRegistrationListener(fallbackCommands), plugin);
            }
        }
        return commandMap;
    }

    public boolean unregisterCommands() {
        CommandMap commandMap = getCommandMap();
        List<String> toRemove = new ArrayList<>();
        Map<String, org.bukkit.command.Command> knownCommands = ReflectionUtil.getField(commandMap, "knownCommands");
        Set<String> aliases = ReflectionUtil.getField(commandMap, "aliases");
        if (knownCommands == null || aliases == null) {
            return false;
        }
        for (Iterator<org.bukkit.command.Command> i = knownCommands.values().iterator(); i.hasNext();) {
            org.bukkit.command.Command cmd = i.next();
            if ((cmd instanceof DynamicPluginCommand && ((DynamicPluginCommand) cmd).getExecutor().equals(executor)) || (cmd instanceof CommandAlias)) {
                i.remove();
                for (String alias : cmd.getAliases()) {
                    org.bukkit.command.Command aliasCmd = knownCommands.get(alias);
                    if (cmd.equals(aliasCmd)) {
                        aliases.remove(alias);
                        toRemove.add(alias);
                    }
                }
            }
        }
        for (String string : toRemove) {
            knownCommands.remove(string);
        }
        return true;
    }
}
