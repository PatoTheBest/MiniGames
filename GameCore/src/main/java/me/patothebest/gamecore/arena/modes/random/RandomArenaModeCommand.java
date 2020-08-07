package me.patothebest.gamecore.arena.modes.random;

import com.google.inject.Inject;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RandomArenaModeCommand implements ParentCommandModule {

    private final RandomArenaMode randomArenaMode;
    private final RandomArenaUIFactory randomArenaUIFactory;

    @Inject private RandomArenaModeCommand(RandomArenaMode randomArenaMode, RandomArenaUIFactory randomArenaUIFactory) {
        this.randomArenaMode = randomArenaMode;
        this.randomArenaUIFactory = randomArenaUIFactory;
    }

    @Command(
            aliases = {"ui"},
            max = 0,
            langDescription = @LangDescription(
                    element = "RANDOM_ARENA_MODE_CHOOSE_GROUP_COMMAND",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.CHOOSE_ARENA)
    public List<String> openChooseGroup(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        randomArenaUIFactory.createChooseGroupUI(player);
        return null;
    }

    @Command(
            aliases = {"map"},
            usage = "<group>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "RANDOM_ARENA_MODE_CHOOSE_GROUP_COMMAND",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.CHOOSE_ARENA)
    public List<String> openChooseMap(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), Utils.toList(randomArenaMode.getGroups()));
            }
            return null;
        }

        String mode = args.getString(0);
        RandomArenaGroup randomArenaGroup = null;
        for (RandomArenaGroup group : randomArenaMode.getGroups()) {
            if (group.getName().equalsIgnoreCase(mode)) {
                randomArenaGroup = group;
            }
        }
        CommandUtils.validateNotNull(randomArenaGroup, CoreLang.RANDOM_ARENA_MODE_NOT_FOUND);
        randomArenaUIFactory.createChooseGroupUI(player);
        return null;
    }

    @ChildOf(BaseCommand.class)
    public static class Parent implements ParentCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject
        private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = "random-arena",
                langDescription = @LangDescription(
                        langClass = CoreLang.class,
                        element = "RANDOM_ARENA_MODE_COMMAND"
                )
        )
        @NestedCommand(value = {
                RandomArenaModeCommand.class,
        },
                defaultToBody = true
        )
        public void oitc(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }
}
