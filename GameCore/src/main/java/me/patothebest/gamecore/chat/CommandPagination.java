package me.patothebest.gamecore.chat;

import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.command.ChatColor;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.util.Levenshtein;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandPagination extends Pagination<Map.Entry<String, Command>> {

    private final CommandsManager<CommandSender> commandsManager;
    private final CommandContext args;
    private final String wholeCommand;
    private final int page;
    private final Map<String, Command> helpMap;

    private boolean showAdminInFooter = false;

    public CommandPagination(CommandsManager<CommandSender> commandManager, CommandContext args) throws CommandException {
        this.commandsManager = commandManager;
        StringBuilder cmd = new StringBuilder();
        for (int i = 0; i <= args.getLevel(); i++) {
            cmd.append(args.getWholeArgs()[i]).append(" ");
        }
        this.wholeCommand = cmd.toString();
        this.args = args;
        this.perPage(10);

        if(args.isInBounds(0)) {
            if(args.isInteger(0)) {
                page = args.getInteger(0);
            } else if(args.isInBounds(1) && args.isInteger(1)) {
                page = args.getInteger(1);
            } else {
                page = 1;
            }
        } else {
            page = 1;
        }

        helpMap = commandManager.getSubcommandsHelp().get(args.getWholeArgs()[args.getLevel()]);
    }

    @Override
    public String header(int page, int pages, CommandSender sender) {
        return "§6§l" + PluginConfig.PLUGIN_TITLE + "§f | §7/" + wholeCommand + "help " + CoreLang.PAGE.replace(sender, page, pages);
    }

    @Override
    public String subHeader(int page, int pages) {
        return ChatColor.GOLD + "Oo-----------------------oOo-----------------------oO";
    }

    @Override
    protected String entry(Map.Entry<String, Command> entry, int index, CommandSender commandSender) {
        Command command = entry.getValue();
        String description = getDescriptionFromCommand(command, commandSender);
        CommandPermissions commandPermissions = commandsManager.getCommandPermissions().get(command.aliases()[0]);

        if(commandPermissions != null) {
            if(showAdminInFooter) {
                return null;
            } else {
                if(!commandSender.hasPermission(commandPermissions.permission().getBukkitPermission())) {
                    return null;
                }
            }
        }

        return "§2/" + wholeCommand + command.aliases()[0] + " " + (!command.usage().isEmpty() ? command.usage() + " " : "")
                + "§a - " + description;
    }

    public CommandPagination showAdminInFooter(boolean showAdminCommands) {
        this.showAdminInFooter = showAdminCommands;
        this.perPage(DEFAULT_PER_PAGE);
        return this;
    }

    @Override
    public void footer(int page, int pages, CommandSender sender) {
        if (showAdminInFooter) {
            for (Command command : helpMap.values()) {
                CommandPermissions commandPermissions = commandsManager.getCommandPermissions().get(command.aliases()[0]);
                if (commandPermissions == null) {
                    continue;
                }

                Permission permission = commandPermissions.permission();

                if(permission.getDisplayName() != null) {
                    if(sender.hasPermission(permission.getBukkitPermission())) {
                        String description = getDescriptionFromCommand(command, sender);
                        sender.sendMessage("§4" + permission.getDisplayName() + ": §2/" + wholeCommand + command.aliases()[0] + " " + (!command.usage().isEmpty() ? command.usage() + " " : "")
                                + "§a - " + description);
                    }
                }
            }
        }

        sender.sendMessage(ChatColor.GOLD + "Oo-----------------------oOo-----------------------oO");
    }

    public void display(CommandSender sender) {
        super.display(sender, helpMap.entrySet(), page);
        if(args.isInBounds(0) && !args.getString(0).equalsIgnoreCase("help") && !args.isInteger(0)) {
            sendCorrection(sender, args.getString(0), new ArrayList<>(helpMap.keySet()).stream().map(s -> {
                if(s.contains(" ")) {
                    return s.split(" ")[0];
                }

                return s;
            }).collect(Collectors.toList()));
        }
    }

    private void sendCorrection(CommandSender sender, String input, List<String> options) {
        // get the closest command with the invalid command
        // using the Levenshtein distance algorithm
        String closest = Levenshtein.getClosestString(input, options.toArray(new String[0]));
        if(closest.equals("")) return;

        // send the correction to the player
        sender.sendMessage(CoreLang.COMMAND_CORRECTION.replace(sender, "/" + wholeCommand + closest));
    }

    private String getDescriptionFromCommand(Command command, CommandSender commandSender) {
        LangDescription langDescription = command.langDescription();

        if(!langDescription.element().isEmpty()) {
            Class<? extends Enum<? extends ILang>> aClass = langDescription.langClass();

            Enum[] enumValArr = aClass.getEnumConstants();

            for (Enum anEnum : enumValArr) {
                if(anEnum.name().equals(langDescription.element())) {
                    ILang iLang = (ILang) anEnum;
                    return iLang.getMessage(commandSender);
                }
            }
        } else {
            return command.desc();
        }

        return null;
    }
}
