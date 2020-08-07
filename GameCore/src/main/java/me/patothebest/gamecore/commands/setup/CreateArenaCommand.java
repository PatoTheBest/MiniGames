package me.patothebest.gamecore.commands.setup;

import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.io.File;

@ChildOf(SetupCommand.class)
public class CreateArenaCommand implements ParentCommandModule {

    private final ArenaManager arenaManager;

    @Inject private CreateArenaCommand(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @Command(
            aliases = {"createarena", "newarena"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "NEW_ARENA",
                    langClass = CoreLang.class
            )
    )
    public void createArena(CommandContext args, CommandSender sender) throws CommandException {
        String arenaName = args.getString(0);

        // requirements
        CommandUtils.validateTrue(arenaManager.getArena(arenaName) == null, CoreLang.ARENA_ALREADY_EXIST);

        // create the arena
        AbstractArena arena = arenaManager.createArena(arenaName);
        CoreLang.ARENA_CREATED.sendMessage(sender);

        if(sender instanceof Player) {
            Player player = CommandUtils.getPlayer(sender);
            player.teleport(new Location(arena.getWorld(), 0, 100, 0));
        }
    }

    @Command(
            aliases = {"import", "importarena"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "IMPORT_ARENA",
                    langClass = CoreLang.class
            )
    )
    public void importArena(CommandContext args, CommandSender sender) throws CommandException {
        String arenaName = args.getString(0);

        // requirements
        CommandUtils.validateTrue(arenaManager.getArena(arenaName) == null, CoreLang.ARENA_ALREADY_EXIST);
        File worldFolder = new File(arenaName + File.separatorChar);
        CommandUtils.validateTrue(worldFolder.exists(), CoreLang.FOLDER_DOESNT_EXIST);
        CommandUtils.validateTrue(worldFolder.isDirectory(), CoreLang.ARENA_IS_FILE);
        CommandUtils.validateTrue(worldFolder.renameTo(new File(PluginConfig.WORLD_PREFIX + arenaName + File.separatorChar)), CoreLang.SOMETHING_WENT_WRONG_IMPORTING);

        // create the arena
        AbstractArena arena = arenaManager.createArena(arenaName);
        CoreLang.ARENA_IMPORTED.sendMessage(sender);

        if(sender instanceof Player) {
            Player player = CommandUtils.getPlayer(sender);
            player.teleport(new Location(arena.getWorld(), 0, 100, 0));
        }
    }
}
