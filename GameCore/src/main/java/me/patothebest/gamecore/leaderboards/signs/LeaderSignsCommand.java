package me.patothebest.gamecore.leaderboards.signs;

import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.chat.Pagination;
import me.patothebest.gamecore.command.ChatColor;
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
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.stats.StatPeriod;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.StatsManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

public class LeaderSignsCommand implements Module {

    private final StatsManager statsManager;
    private final LeaderSignsManager leaderSignsManager;

    @Inject private LeaderSignsCommand(StatsManager statsManager, LeaderSignsManager leaderSignsManager) {
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
                aliases = "signs",
                langDescription = @LangDescription(
                        langClass = CoreLang.class,
                        element = "LEADERBOARD_SIGNS_DESC"
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = LeaderSignsCommand.class)
        public void signs(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"add"},
            usage = "<stat> <periodicity> <place>",
            min = 3,
            max = 3,
            langDescription = @LangDescription(
                    element = "LEADERBOARD_SIGNS_ADD",
                    langClass = CoreLang.class
            )
    )
    public List<String> add(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completeNameable(args.getString(0), statsManager.getStatistics());
            } else if (args.getSuggestionContext().getIndex() == 1) {
                return CommandUtils.complete(args.getString(1), StatPeriod.values());
            }

            return null;
        }

        Player player = CommandUtils.getPlayer(sender);
        Statistic statistic = statsManager.getStatisticByName(args.getString(0));
        CommandUtils.validateNotNull(statistic, CoreLang.LEADERBOARD_STATISTIC_NOT_FOUND);
        StatPeriod statPeriod = CommandUtils.getEnumValueFromString(StatPeriod.class, args.getString(1), CoreLang.LEADERBOARD_PERIOD_NOT_FOUND);
        int place = args.getInteger(2);
        CommandUtils.validateTrue(place <= 10, CoreLang.LEADERBOARD_MAX_PLACE);

        leaderSignsManager.getBlockInteractCallback().put(player, block -> {
            LeaderSign leaderSign = leaderSignsManager.createSign(block.getLocation(), statistic, statPeriod, place);
            leaderSignsManager.getSigns().add(leaderSign);

            try {
                for (AttachmentType attachmentType : AttachmentType.usableValues()) {
                    Attachment attachment = leaderSignsManager.createAttachment(attachmentType);
                    if (attachment.createNew(leaderSign)) {
                        leaderSign.getAttachmentSet().add(attachment);
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

            leaderSignsManager.saveData();
            leaderSignsManager.updateData();
            CoreLang.SIGN_CREATED.sendMessage(player);
        });
        CoreLang.LEADERBOARD_SIGNS_CLICK_TO_ADD.sendMessage(sender);
        return null;
    }


    @Command(
            aliases = {"list", "listsigns", "signs", "l"},
            usage = "[page]",
            max = 1,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "LEADERBOARD_SIGNS_LIST"
            )
    )
    @CommandPermissions(permission = Permission.SETUP)
    public void list(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.getPlayer(sender);

        List<LeaderSign> signs = leaderSignsManager.getSigns();
        int page = 1;

        if(args.isInBounds(0) && args.isInteger(0)) {
            page = args.getInteger(0);
        }

        new Pagination<LeaderSign>() {
            @Override
            protected String title() {
                return "Signs";
            }

            @Override
            protected String entry(LeaderSign entry, int index, CommandSender commandSender) {
                return ChatColor.GRAY + "* " + ChatColor.GOLD.toString() + (index+1) + ". " + ChatColor.BLUE + entry.getStatistic().getStatName()
                        + " " + ChatColor.YELLOW + " Location: " + Utils.locationToString(entry.getLocation(), sender);
            }
        }.display(sender, signs, page);
    }
}
