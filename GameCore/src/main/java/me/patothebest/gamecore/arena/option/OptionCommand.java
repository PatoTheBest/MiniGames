package me.patothebest.gamecore.arena.option;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.arena.option.options.EnableableOption;
import me.patothebest.gamecore.arena.option.options.EnvironmentOption;
import me.patothebest.gamecore.arena.option.options.TimeOfDayOption;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.commands.setup.SetupCommand;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class OptionCommand {

    private final ArenaManager arenaManager;

    @Inject private OptionCommand(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @ChildOf(SetupCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject
        private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = {"option", "options"},
                langDescription = @LangDescription(
                        element = "OPTION_COMMAND_DESC",
                        langClass = CoreLang.class
                )
        )
        @NestedCommand(value = OptionCommand.class)
        public void shop(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"list"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "OPTION_LIST_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public List<String> list(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }
            return null;
        }

        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        for (ArenaOption arenaOption : arena.getArenaOptions()) {
            if (!(arenaOption instanceof EnableableOption)) {
                sender.sendMessage("");
                arenaOption.sendDescription(sender);
            }
        }

        for (ArenaOption arenaOption : arena.getArenaOptions()) {
            if (arenaOption instanceof EnableableOption) {
                sender.sendMessage("");
                arenaOption.sendDescription(sender);
            }
        }
        return null;
    }

    @Command(
            aliases = {"enable"},
            usage = "<arena> <option>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "OPTION_ENABLE_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public List<String> enable(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            } else if (args.getSuggestionContext().getIndex() == 1) {
                AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
                return CommandUtils.complete(args.getString(1),
                        arena.getArenaOptions()
                                .stream()
                                .filter(arenaOption -> arenaOption instanceof EnableableOption)
                                .map(ArenaOption::getName)
                                .collect(Collectors.toList()));
            }
            return null;
        }

        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        String optionName = args.getString(1);
        ArenaOption arenaOption = arena.getArenaOptions()
                .stream()
                .filter(arenaOptionL -> arenaOptionL.getName().equalsIgnoreCase(optionName))
                .findFirst().orElse(null);
        CommandUtils.validateNotNull(arenaOption, CoreLang.OPTION_NOT_FOUND);
        CommandUtils.validateTrue(arenaOption instanceof EnableableOption, CoreLang.OPTION_CANT_BE_ENABLED);
        ((EnableableOption)arenaOption).setEnabled(true);
        CoreLang.OPTION_ENABLED.replaceAndSend(sender, optionName, arena.getName());
        return null;
    }

    @Command(
            aliases = {"disable"},
            usage = "<arena> <option>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "OPTION_DISABLE_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public List<String> disable(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            } else if (args.getSuggestionContext().getIndex() == 1) {
                AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
                return CommandUtils.complete(args.getString(1),
                        arena.getArenaOptions()
                                .stream()
                                .filter(arenaOption -> arenaOption instanceof EnableableOption)
                                .map(ArenaOption::getName)
                                .collect(Collectors.toList()));
            }
            return null;
        }

        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        String optionName = args.getString(1);
        ArenaOption arenaOption = arena.getArenaOptions()
                .stream()
                .filter(arenaOptionL -> arenaOptionL.getName().equalsIgnoreCase(optionName))
                .findFirst().orElse(null);
        CommandUtils.validateNotNull(arenaOption, CoreLang.OPTION_NOT_FOUND);
        CommandUtils.validateTrue(arenaOption instanceof EnableableOption, CoreLang.OPTION_CANT_BE_ENABLED);
        ((EnableableOption)arenaOption).setEnabled(false);
        CoreLang.OPTION_DISABLED.replaceAndSend(sender, optionName, arena.getName());
        return null;
    }

    @Command(
            aliases = {"settime"},
            usage = "<arena> <time>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "OPTION_SETTIME_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public List<String> settime(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            } else if (args.getSuggestionContext().getIndex() == 1) {
                return CommandUtils.complete(args.getString(1), TimeOfDayOption.Time.values());
            }
            return null;
        }

        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        TimeOfDayOption.Time time = CommandUtils.getEnumValueFromString(TimeOfDayOption.Time.class, args.getString(1), CoreLang.OPTION_TIME_INVALID);
        arena.getOption(TimeOfDayOption.class).setTime(time);
        arena.getWorld().setTime(time.getTick());
        CoreLang.OPTION_TIME_CHANGED.replaceAndSend(sender, Utils.capitalizeFirstLetter(time.name()));
        return null;
    }


    @Command(
            aliases = {"setenvironment"},
            usage = "<arena> <envirnonment>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "OPTION_SETENVIRONMENT_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public List<String> setenvironment(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            } else if (args.getSuggestionContext().getIndex() == 1) {
                return CommandUtils.complete(args.getString(1), World.Environment.values());
            }
            return null;
        }

        AbstractArena arena = CommandUtils.getArena(args, 0, arenaManager);
        World.Environment environment = CommandUtils.getEnumValueFromString(World.Environment.class, args.getString(1), CoreLang.OPTION_TIME_INVALID);
        arena.getOption(EnvironmentOption.class).setEnvironment(environment);
        CoreLang.OPTION_ENVIRONMENT_CHANGING.sendMessage(sender);
        arenaManager.reloadArena(arena);
        CoreLang.OPTION_ENVIRONMENT_CHANGED.replaceAndSend(sender, Utils.capitalizeFirstLetter(environment.name()));
        return null;
    }
}
