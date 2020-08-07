package me.patothebest.gamecore.leaderboards.signs;

import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.leaderboards.LeaderboardCommand;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.stats.StatsManager;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

public class AttachmentCommand implements RegisteredCommandModule {

    private final StatsManager statsManager;
    private final LeaderSignsManager leaderSignsManager;

    @Inject private AttachmentCommand(StatsManager statsManager, LeaderSignsManager leaderSignsManager) {
        this.statsManager = statsManager;
        this.leaderSignsManager = leaderSignsManager;
    }

    @ChildOf(LeaderboardCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = {"attachments", "attach"},
                langDescription = @LangDescription(
                        langClass = CoreLang.class,
                        element = "LEADERBOARD_ATTACHMENTS_DESC"
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = AttachmentCommand.class)
        public void attachments(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"add"},
            usage = "<type>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "LEADERBOARD_ATTACHMENTS_ADD",
                    langClass = CoreLang.class
            )
    )
    public List<String> add(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            return CommandUtils.complete(args.getString(0), AttachmentType.usableValues());
        }

        Player player = CommandUtils.getPlayer(sender);
        AttachmentType attachmentType = CommandUtils.getEnumValueFromString(AttachmentType.class, args.getString(0), CoreLang.LEADERBOARD_ATTACHMENTS_NOT_FOUND);
        if (!attachmentType.canBeUsed()) {
            throw new CommandException(CoreLang.LEADERBOARD_ATTACHMENTS_NOT_USABLE.replace(sender, attachmentType.getDependencyClass().getSimpleName()));
        }

        leaderSignsManager.getSignInteractCallback().put(player, leaderSign -> {
            for (Attachment attachment : leaderSign.getAttachmentSet()) {
                if (attachment.getType() == attachmentType) {
                    CoreLang.LEADERBOARD_ATTACHMENTS_ALREADY_ATTACHED.sendMessage(player);
                    return;
                }
            }

            Attachment attachment = leaderSignsManager.createAttachment(attachmentType);
            if (attachment.createNew(leaderSign)) {
                leaderSign.getAttachmentSet().add(attachment);
                leaderSign.update();
                CoreLang.LEADERBOARD_ATTACHMENTS_ADDED.sendMessage(player);
            } else {
                CoreLang.LEADERBOARD_ATTACHMENTS_COULD_NOT_ADD.sendMessage(player);
            }
        });
        CoreLang.LEADERBOARD_ATTACHMENTS_RIGHT_CLICK.sendMessage(sender);
        return null;
    }
}
