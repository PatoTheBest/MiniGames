package me.patothebest.gamecore.stats;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

@ChildOf(BaseCommand.class)
public class StatsCommand implements RegisteredCommandModule {

    private final PlayerManager playerManager;
    private final StatsManager statsManager;

    @Inject private StatsCommand(PlayerManager playerManager, StatsManager statsManager) {
        this.playerManager = playerManager;
        this.statsManager = statsManager;
    }

    @Command(
            aliases = {"stats"},
            usage = "[player]",
            min = 0,
            max = 1,
            langDescription = @LangDescription(
                    element = "",
                    langClass = CoreLang.class
            )
    )
    public List<String> stats(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completePlayers(args.getString(0));
            }

            return null;
        }

        Player player;
        boolean self = false;
        if (!args.isInBounds(0)) {
            player = CommandUtils.getPlayer(sender);
            self = true;
        } else {
            player = CommandUtils.getPlayer(args, 0);
        }

        IPlayer corePlayer = playerManager.getPlayer(player);
        if (self) {
            CoreLang.STATS_WEEK.sendMessage(sender);
        } else {
            CoreLang.STATS_WEEK_PLAYER.replaceAndSend(sender, player.getName());
        }
        corePlayer.getStatistics().forEach((statClass, trackedStatistic) -> {
            Statistic statistic = statsManager.getStatisticByClass(statClass);
            CoreLang.STATS_WEEK_DISPLAY.replaceAndSend(sender, statistic.getStatName(), trackedStatistic.getWeekly());
        });
        sender.sendMessage("");

        if (self) {
            CoreLang.STATS_ALL.sendMessage(sender);
        } else {
            CoreLang.STATS_ALL_PLAYER.replaceAndSend(sender, player.getName());
        }
        corePlayer.getStatistics().forEach((statClass, trackedStatistic) -> {
            Statistic statistic = statsManager.getStatisticByClass(statClass);
            CoreLang.STATS_ALL_DISPLAY.replaceAndSend(sender, statistic.getStatName(), trackedStatistic.getAllTime());
        });
        return null;
    }


}
