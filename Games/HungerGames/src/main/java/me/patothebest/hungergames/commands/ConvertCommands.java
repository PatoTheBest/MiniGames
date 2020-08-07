package me.patothebest.hungergames.commands;

import me.patothebest.gamecore.PluginConfig;
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
import me.patothebest.gamecore.vector.ArenaLocation;
import me.patothebest.gamecore.world.ArenaWorld;
import me.patothebest.hungergames.arena.Arena;
import me.patothebest.hungergames.arena.ArenaType;
import me.patothebest.hungergames.arena.GameTeam;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

public class ConvertCommands {

    private final ArenaManager arenaManager;

    @Inject private ConvertCommands(ArenaManager arenaManager) {
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
                aliases = "convert",
                desc = "Command for converting",
                langDescription = @LangDescription(
                        langClass = CoreLang.class,
                        element = ""
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(
                value = ConvertCommands.class,
                defaultToBody = true
        )
        public void convert(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"toteam"},
            usage = "<arena>",
            min = 1,
            max = 1,
            desc = "convert an arena to team",
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = ""
            )
    )
    public List<String> convertArena(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getEnabledArena(args, 0, arenaManager);
        Utils.unZip(new File(ArenaWorld.WORLD_DIRECTORY, arena.getName() + ".zip").getPath(), new File(PluginConfig.WORLD_PREFIX + arena.getName() + "-Team" + File.separatorChar).getPath());
        new File(PluginConfig.WORLD_PREFIX + arena.getName() + "-Team" + File.separatorChar + "uid.dat").delete();
        Arena newArena = (Arena) arenaManager.createArena(arena.getName() + "-Team");
        newArena.setArenaGroup(ArenaType.TEAM);
        newArena.setArea(arena.getArea().clone().setArena(newArena));
        newArena.setLobbyLocation(new ArenaLocation(newArena, arena.getLobbyLocation()));
        newArena.setMaxPlayers(arena.getMaxPlayers()*2);
        newArena.setMinPlayers(arena.getMinPlayers());
        newArena.setPermissionGroup(arena.getPermissionGroup());
        newArena.setSpectatorLocation(new ArenaLocation(newArena, arena.getSpectatorLocation()));
        newArena.save();
        newArena.initializePhase();

        int i = 0;
        int i2 = 1;
        boolean appendTeam = arena.getSpawns().size() > DyeColor.values().length;
        for (ArenaLocation arenaLocation : arena.getSpawns()) {
            if(i == DyeColor.values().length) {
                i = 0;
                i2++;
            }

            DyeColor color = DyeColor.values()[i];

            GameTeam team = newArena.createTeam(Utils.capitalizeFirstLetter(color.name()) + (appendTeam ?  "#" + i2 : ""), color);
            team.setSpawn(new ArenaLocation(newArena, arenaLocation));
            i++;
        }

        newArena.enableArena();
        return null;
    }


}
