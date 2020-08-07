package me.patothebest.arcade.game.games.snake;

import com.google.inject.Inject;
import me.patothebest.arcade.Arcade;
import me.patothebest.arcade.game.Game;
import me.patothebest.arcade.game.GameType;
import me.patothebest.arcade.game.commands.BasicGameSetupCommands;
import me.patothebest.arcade.game.commands.GameCommand;
import me.patothebest.arcade.game.commands.SpawnsCommand;
import me.patothebest.arcade.game.components.floor.FloorCommand;
import me.patothebest.arcade.game.components.floor.FloorComponent;
import me.patothebest.arcade.game.goal.Goal;
import me.patothebest.arcade.game.goal.LastAliveGoal;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.feature.features.other.DeathMessageFeature;
import me.patothebest.gamecore.feature.features.protection.NoBorderTrespassingFeature;
import me.patothebest.gamecore.feature.features.protection.PlayerProtectionFeature;
import me.patothebest.gamecore.feature.features.spectator.DeathSpectatorFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorProtectionFeature;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.permission.Permission;
import org.bukkit.command.CommandSender;

public class Snake extends Game {

    private final Goal goal = new LastAliveGoal();

    @Inject private Snake(Arcade plugin) {
        super(plugin);
        gameComponents.add(new FloorComponent());
    }

    @Override
    public void configure() {
        registerFeature(DeathMessageFeature.class);
        registerFeature(NoBorderTrespassingFeature.class);
        registerFeature(DeathSpectatorFeature.class);
        registerFeature(SpectatorProtectionFeature.class);
        registerFeature(SpectatorFeature.class);
        registerFeature(PlayerProtectionFeature.class);
        registerFeature(SnakeFeature.class);
    }

    @Override
    public String getName() {
        return "Snake";
    }

    @Override
    public Goal getGoal() {
        return goal;
    }

    @Override
    public GameType getGameType() {
        return GameType.SNAKE;
    }

    @ChildOf(GameCommand.class)
    public static class Command implements ParentCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject
        private Command(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @me.patothebest.gamecore.command.Command(
                aliases = "snake",
                langDescription = @LangDescription(
                        langClass = CoreLang.class,
                        element = "SETUP_COMMAND_DESCRIPTION"
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = {
                SpawnsCommand.class,
                BasicGameSetupCommands.class,
                FloorCommand.class
        },
                defaultToBody = true
        )
        public void splegg(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }
}
