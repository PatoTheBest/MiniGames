package me.patothebest.gamecore.experience;

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
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.logger.InjectParentLogger;
import me.patothebest.gamecore.logger.Logger;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.storage.StorageManager;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import javax.inject.Inject;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class ExperienceCommand implements Module {

    @InjectParentLogger(parent = ExperienceManager.class) private Logger logger;
    private final StorageManager storageManager;
    private final CoreConfig config;
    private final PlayerManager playerManager;
    private final ExperienceManager experienceManager;

    @Inject
    private ExperienceCommand(StorageManager storageManager, CoreConfig config, PlayerManager playerManager, ExperienceManager experienceManager) {
        this.storageManager = storageManager;
        this.config = config;
        this.playerManager = playerManager;
        this.experienceManager = experienceManager;
    }

    @ChildOf(BaseCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = {"experience", "exp", "xp"},
                langDescription = @LangDescription(
                        langClass = CoreLang.class,
                        element = "EXPERIENCE_COMMAND_DESC"
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = ExperienceCommand.class)
        public void signs(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"populate"},
            max = 0,
            flags = "c",
            langDescription = @LangDescription(
                    element = "EXPERIENCE_POPULATE_DESC",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public void migrate(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.validateTrue(storageManager.arePlayersOnDatabase(), CoreLang.DATABASE_REQUIREMENT);
        ConfigurationSection statsSection = config.getConfigurationSection("game-experience.experience-stats");
        CommandUtils.validateNotNull(statsSection, "Invalid configuration! Missing game-experience.experience-stats section.");
        if (!args.hasFlag('c')) {
            CoreLang.EXPERIENCE_POPULATE_CONFIRM.sendMessage(sender);
            statsSection
                    .getValues(false)
                    .forEach((statName, experienceObj) -> CoreLang.EXPERIENCE_POPULATE_CONFIG.replaceAndSend(sender, statName, experienceObj));
            return;
        }

        storageManager.getMySQLStorage().getConnectionHandler().executeSQLQuery(connection -> {
            StringBuilder stb = new StringBuilder(PopulateQueries.HEADER);
            Map<String, Object> values = statsSection.getValues(false);
            values.forEach((statName, experienceObj) -> {
                int experience = (int) experienceObj;
                stb.append(PopulateQueries.makeMiddleQueryPart(statName, experience));
                stb.append("UNION\n");
            });
            stb.delete(stb.length() - 7, stb.length()); // remove last union
            stb.append(PopulateQueries.FOOTER);
            logger.fine("Executing the following query:");
            logger.fine(stb.toString());
            Statement statement = connection.createStatement();
            statement.execute(stb.toString());
            CoreLang.EXPERIENCE_POPULATE_DONE.sendMessage(sender);
        }, true);
    }

    @Command(
            aliases = {"add", "give"},
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "EXPERIENCE_ADD_DESC",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> addExp(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completePlayers(args.getString(0));
            }
            return null;
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        long amount = args.getInteger(1);
        player.addExperience(amount);
        int newLevel = experienceManager.getExperienceCalculator().expToLevelFloor(player.getExperience());
        CoreLang.EXPERIENCE_ADD.replaceAndSend(sender, player.getName(), amount, player.getExperience(), newLevel);
        return null;
    }

    @Command(
            aliases = {"set"},
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "EXPERIENCE_SET_DESC",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> setExp(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completePlayers(args.getString(0));
            }
            return null;
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        long amount = args.getInteger(1);
        player.setExperience(amount);
        int newLevel = experienceManager.getExperienceCalculator().expToLevelFloor(player.getExperience());
        CoreLang.EXPERIENCE_SET.replaceAndSend(sender, player.getName(), amount, newLevel);
        return null;
    }

    @Command(
            aliases = {"remove", "subtract"},
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "EXPERIENCE_REMOVE_DESC",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> removeExp(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completePlayers(args.getString(0));
            }
            return null;
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        long amount = args.getInteger(1);
        player.removeExperience(amount);
        int newLevel = experienceManager.getExperienceCalculator().expToLevelFloor(player.getExperience());
        CoreLang.EXPERIENCE_REMOVE.replaceAndSend(sender, player.getName(), amount, player.getExperience(), newLevel);
        return null;
    }
}
