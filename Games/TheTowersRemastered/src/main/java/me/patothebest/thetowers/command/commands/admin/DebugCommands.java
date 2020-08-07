package me.patothebest.thetowers.command.commands.admin;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.commands.admin.AdminCommand;
import me.patothebest.gamecore.feature.features.chests.regen.ChestRegenFeature;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.thetowers.arena.GameTeam;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@ChildOf(AdminCommand.class)
public class DebugCommands implements RegisteredCommandModule {

    private final PlayerManager playerManager;

    @Inject private DebugCommands(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Command(
            aliases = {"score"},
            min = 0,
            usage = "[amount]",
            max = 1,
            langDescription = @LangDescription(
                    element = "SCORE",
                    langClass = Lang.class
            )
    )
    public void score(CommandContext args, CommandSender sender) throws CommandException {
        Player bukkitPlayer = CommandUtils.getPlayer(sender);
        IPlayer player = playerManager.getPlayer(bukkitPlayer);
        GameTeam gameTeam = (GameTeam) player.getGameTeam();
        CommandUtils.validateTrue(player.isInArena(), CoreLang.NOT_IN_AN_ARENA);
        CommandUtils.validateNotNull(gameTeam, CoreLang.ARENA_NOT_STARTED);
        int times = args.getInteger(0, 1);
        times = Math.max(times, 10 - gameTeam.getPoints());
        for (int i = times; i > 0; i--) {
            gameTeam.scorePoint(bukkitPlayer);
        }
    }

    @Command(
            aliases = {"refill"},
            min = 0,
            max = 0,
            langDescription = @LangDescription(
                    element = "REFILL",
                    langClass = Lang.class
            )
    )
    public void refill(CommandContext args, CommandSender sender) throws CommandException {
        Player bukkitPlayer = CommandUtils.getPlayer(sender);
        IPlayer player = playerManager.getPlayer(bukkitPlayer);
        Arena arena = (Arena) player.getCurrentArena();
        CommandUtils.validateNotNull(arena, CoreLang.NOT_IN_AN_ARENA);
        CommandUtils.validateTrue(arena.isInGame(), CoreLang.ARENA_NOT_STARTED);
        if(arena.getFeature(ChestRegenFeature.class) != null) {
            arena.getFeature(ChestRegenFeature.class).run();
        }
        Lang.CHESTS_REFILLED.sendMessage(player);
    }
}
