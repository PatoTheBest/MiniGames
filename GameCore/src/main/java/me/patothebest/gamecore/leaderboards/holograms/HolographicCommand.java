package me.patothebest.gamecore.leaderboards.holograms;

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

public class HolographicCommand implements RegisteredCommandModule {

    private final StatsManager statsManager;
    private final LeaderHologramManager leaderHologramManager;

    @Inject private HolographicCommand(StatsManager statsManager, LeaderHologramManager leaderHologramManager) {
        this.statsManager = statsManager;
        this.leaderHologramManager = leaderHologramManager;
    }

    @ChildOf(LeaderboardCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = {"holo", "holograms"},
                langDescription = @LangDescription(
                        langClass = CoreLang.class,
                        element = "LEADERBOARD_HOLOGRAM_DESC"
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = HolographicCommand.class)
        public void holograms(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"create"},
            usage = "<name>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "LEADERBOARD_HOLOGRAM_CREATE",
                    langClass = CoreLang.class
            )
    )
    public void create(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        String name = args.getString(0);
        CommandUtils.validateTrue(leaderHologramManager.getHologram(name) == null, CoreLang.LEADERBOARD_HOLOGRAM_ALREADY_EXISTS);

        leaderHologramManager.createHologram(name, player.getLocation());
        leaderHologramManager.saveData();
        CoreLang.LEADERBOARD_HOLOGRAM_CREATED.sendMessage(player);
    }

    @Command(
            aliases = {"add"},
            usage = "<hologram> <stat> <periodicity> <title>",
            min = 4,
            max = -1,
            langDescription = @LangDescription(
                    element = "LEADERBOARD_HOLOGRAM_ADD",
                    langClass = CoreLang.class
            )
    )
    public List<String> add(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completeNameable(args.getString(0), leaderHologramManager.getHolograms());
            } else if (args.getSuggestionContext().getIndex() == 1) {
                return CommandUtils.completeNameable(args.getString(1), statsManager.getStatistics());
            } else if (args.getSuggestionContext().getIndex() == 2) {
                return CommandUtils.complete(args.getString(2), StatPeriod.values());
            }

            return null;
        }

        Player player = CommandUtils.getPlayer(sender);
        LeaderHologram hologram = leaderHologramManager.getHologram(args.getString(0));

        if (hologram == null) {
            throw new CommandException(CoreLang.LEADERBOARD_HOLOGRAM_NOT_FOUND.replace(player, args.getString(0)));
        }

        Statistic statistic = statsManager.getStatisticByName(args.getString(1));
        CommandUtils.validateNotNull(statistic, CoreLang.LEADERBOARD_STATISTIC_NOT_FOUND);
        StatPeriod statPeriod = CommandUtils.getEnumValueFromString(StatPeriod.class, args.getString(2), CoreLang.LEADERBOARD_PERIOD_NOT_FOUND);
        String title = args.getJoinedStrings(3);

        hologram.addPage(statistic, statPeriod, title);
        leaderHologramManager.saveData();
        CoreLang.LEADERBOARD_HOLOGRAM_ADDED.replaceAndSend(player, statistic.getName(), hologram.getName());
        return null;
    }

    @Command(
            aliases = {"list"},
            usage = "[hologram] [page]",
            min = 0,
            max = 2,
            langDescription = @LangDescription(
                    element = "LEADERBOARD_HOLOGRAM_LIST",
                    langClass = CoreLang.class
            )
    )
    public List<String> list(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            return CommandUtils.completeNameable(args.getString(0), leaderHologramManager.getHolograms());
        }

        if (args.isInBounds(0) && !args.isInteger(0)) {
            LeaderHologram hologram = leaderHologramManager.getHologram(args.getString(0));

            if (hologram == null) {
                throw new CommandException(CoreLang.LEADERBOARD_HOLOGRAM_NOT_FOUND.replace(sender, args.getString(0)));
            }

            int page = 1;

            if(args.isInBounds(1)) {
                page = args.getInteger(1);
            }

            new Pagination<HolographicStat>() {
                @Override
                protected String title() {
                    return CoreLang.LEADERBOARD_HOLOGRAM_LIST_TITLE.getMessage(sender);
                }

                @Override
                protected String entry(HolographicStat entry, int index, CommandSender commandSender) {
                    return ChatColor.GRAY + "* " + ChatColor.GOLD.toString() + (index+1) + ". " + ChatColor.BLUE + entry.getTitle()
                            + " " + ChatColor.YELLOW + " Stat: " + entry.getStatistic().getStatName()
                            + " Period: " + Utils.capitalizeFirstLetter(entry.getPeriod().name());
                }
            }.display(sender, hologram.getPages(), page);
        } else {
            int page = 1;

            if(args.isInBounds(0)) {
                page = args.getInteger(0);
            }

            new Pagination<LeaderHologram>() {
                @Override
                protected String title() {
                    return CoreLang.LEADERBOARD_HOLOGRAM_LIST_TITLE.getMessage(sender);
                }

                @Override
                protected String entry(LeaderHologram entry, int index, CommandSender commandSender) {
                    return ChatColor.GRAY + "* " + ChatColor.GOLD.toString() + (index+1) + ". " + ChatColor.BLUE + entry.getName()
                            + " " + ChatColor.YELLOW + " Location: " + Utils.locationToCoords(entry.getHologramLocation(), commandSender)
                            + " Entries: " + entry.getAmountToDisplay();
                }
            }.display(sender, leaderHologramManager.getHolograms(), page);
        }
        return null;
    }

    @Command(
            aliases = {"setsize"},
            usage = "<hologram> <size>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "LEADERBOARD_HOLOGRAM_SETSIZE",
                    langClass = CoreLang.class
            )
    )
    public List<String> setsize(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completeNameable(args.getString(0), leaderHologramManager.getHolograms());
            }
            return null;
        }

        Player player = CommandUtils.getPlayer(sender);
        LeaderHologram hologram = leaderHologramManager.getHologram(args.getString(0));

        if (hologram == null) {
            throw new CommandException(CoreLang.LEADERBOARD_HOLOGRAM_NOT_FOUND.replace(player, args.getString(0)));
        }

        int integer = args.getInteger(1);
        CommandUtils.validateTrue(integer <= 10, CoreLang.LEADERBOARD_MAX_PLACE);
        hologram.setAmountToDisplay(integer);
        leaderHologramManager.saveData();
        CoreLang.LEADERBOARD_HOLOGRAM_SETSIZE_SUCCESS.replaceAndSend(sender, integer);
        return null;
    }

    @Command(
            aliases = {"move", "tp"},
            usage = "<hologram>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "LEADERBOARD_HOLOGRAM_MOVE",
                    langClass = CoreLang.class
            )
    )
    public List<String> move(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completeNameable(args.getString(0), leaderHologramManager.getHolograms());
            }
            return null;
        }

        Player player = CommandUtils.getPlayer(sender);
        LeaderHologram hologram = leaderHologramManager.getHologram(args.getString(0));

        if (hologram == null) {
            throw new CommandException(CoreLang.LEADERBOARD_HOLOGRAM_NOT_FOUND.replace(player, args.getString(0)));
        }

        hologram.setLocation(player.getLocation());
        leaderHologramManager.saveData();
        CoreLang.LEADERBOARD_HOLOGRAM_MOVED.sendMessage(sender);
        return null;
    }


    @Command(
            aliases = {"remove"},
            usage = "<hologram> <index>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "LEADERBOARD_HOLOGRAM_REMOVE",
                    langClass = CoreLang.class
            )
    )
    public List<String> remove(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completeNameable(args.getString(0), leaderHologramManager.getHolograms());
            }
            return null;
        }

        Player player = CommandUtils.getPlayer(sender);
        LeaderHologram hologram = leaderHologramManager.getHologram(args.getString(0));

        if (hologram == null) {
            throw new CommandException(CoreLang.LEADERBOARD_HOLOGRAM_NOT_FOUND.replace(player, args.getString(0)));
        }
        int integer = args.getInteger(1);
        CommandUtils.validateTrue(hologram.getPages().size() >= integer && integer > 0, CoreLang.LEADERBOARD_HOLOGRAM_REMOVE_INDEX);

        HolographicStat holographicStat = hologram.getPages().get(integer - 1);
        hologram.removePage(integer - 1);
        leaderHologramManager.saveData();
        CoreLang.LEADERBOARD_HOLOGRAM_REMOVED.replaceAndSend(player, holographicStat.getStatistic().getName(), Utils.capitalizeFirstLetter(holographicStat.getPeriod().name()));
        return null;
    }

    @Command(
            aliases = {"delete"},
            usage = "<hologram>",
            flags = "c",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "LEADERBOARD_HOLOGRAM_DELETE",
                    langClass = CoreLang.class
            )
    )
    public List<String> delete(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completeNameable(args.getString(0), leaderHologramManager.getHolograms());
            }
            return null;
        }

        Player player = CommandUtils.getPlayer(sender);
        LeaderHologram hologram = leaderHologramManager.getHologram(args.getString(0));

        if (hologram == null) {
            throw new CommandException(CoreLang.LEADERBOARD_HOLOGRAM_NOT_FOUND.replace(player, args.getString(0)));
        }

        if (!args.hasFlag('c')) {
            CoreLang.LEADERBOARD_HOLOGRAM_DELETE_CONFIRM.replaceAndSend(sender, hologram.getName());
        } else {
            hologram.destroy();
            leaderHologramManager.getHolograms().remove(hologram);
            leaderHologramManager.saveData();
            CoreLang.LEADERBOARD_HOLOGRAM_DELETED.sendMessage(sender);
        }
        return null;
    }


}
