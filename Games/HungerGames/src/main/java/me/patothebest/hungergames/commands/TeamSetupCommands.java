package me.patothebest.hungergames.commands;

import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.commands.setup.SetupCommand;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.hungergames.arena.Arena;
import me.patothebest.hungergames.arena.ArenaType;
import me.patothebest.hungergames.lang.Lang;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

public class TeamSetupCommands {

    private final ArenaManager arenaManager;

    @Inject private TeamSetupCommands(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @ChildOf(SetupCommand.class)
    public static class Parent implements ParentCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject
        private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = "teams",
                langDescription = @LangDescription(
                        element = "TEAMS_COMMAND_DESCRIPTION",
                        langClass = CoreLang.class
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(
                value = TeamSetupCommands.class,
                defaultToBody = true
        )
        public void teams(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
        
    }

    @Command(
            aliases = {"create", "add", "new"},
            usage = "<arena> <team name> <team color>",
            min = 3,
            max = 3,
            langDescription = @LangDescription(
                    element = "NEW_TEAM",
                    langClass = CoreLang.class
            )
    )
    public List<String> addGameTeam(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            switch(args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.complete(args.getString(0), arenaManager);
                case 2:
                    return CommandUtils.complete(args.getString(2), DyeColor.values());
                default:
                    return null;
            }
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        CommandUtils.validateTrue(arena.getArenaType() == ArenaType.TEAM, Lang.ARENA_MUST_BE_TEAM);

        DyeColor dyeColor = Utils.getEnumValueFromString(DyeColor.class, args.getString(2));
        CommandUtils.validateNotNull(dyeColor, CoreLang.TEAM_COLOR_NOT_FOUND);
        CommandUtils.validateTrue(!arena.containsTeam(args.getString(0)), CoreLang.TEAM_COLOR_ALREADY_EXIST);

        // create and add the game team
        arena.createTeam(args.getString(1), dyeColor);
        player.sendMessage(CoreLang.TEAM_CREATED.getMessage(player));
        arena.save();

        return null;
    }

    @Command(
            aliases = {"setspawn", "setteamspawn"},
            usage = "<arena> <team name>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "NEW_TEAM_SPAWN",
                    langClass = CoreLang.class
            )
    )
    public List<String> setTeamSpawn(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            switch(args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.complete(args.getString(0), arenaManager);
                case 1:
                    return CommandUtils.completeNameable(args.getString(1), CommandUtils.getDisabledArena(args, 0, arenaManager).getTeams().values());
                default:
                    return null;
            }
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        CommandUtils.validateTrue(arena.getArenaType() == ArenaType.TEAM, Lang.ARENA_MUST_BE_TEAM);
        AbstractGameTeam gameTeam = CommandUtils.getTeam(arena, args, 1);

        gameTeam.setSpawn(player.getLocation());
        player.sendMessage(CoreLang.TEAM_SPAWN_SET.getMessage(player));
        arena.save();
        return null;
    }
}
