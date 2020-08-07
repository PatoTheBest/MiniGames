package me.patothebest.thetowers.command.commands.setup;

import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.commands.setup.SetupCommand;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.thetowers.TheTowersRemastered;
import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.GameTeam;
import me.patothebest.thetowers.arena.ItemDropper;
import me.patothebest.thetowers.guis.setup.MainMenu;
import me.patothebest.thetowers.guis.setup.edit.EditArenaUI;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

@ChildOf(SetupCommand.class)
public class TheTowersSetupCommand implements ParentCommandModule {

    private final TheTowersRemastered plugin;
    private final ArenaManager arenaManager;
    private final Provider<SelectionManager> selectionManagerProvider;

    @Inject private TheTowersSetupCommand(TheTowersRemastered plugin, ArenaManager arenaManager, Provider<SelectionManager> selectionManagerProvider) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.selectionManagerProvider = selectionManagerProvider;
    }

    @Command(
            aliases = {"adddropper", "createdropper", "newdropper", "addspawner"},
            usage = "<arena> <name> <item> <interval>",
            min = 4,
            max = 4,
            langDescription = @LangDescription(
                    element = "NEW_DROPPER",
                    langClass = Lang.class
            )
    )
    public List<String> addDropper(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        ItemStack itemStack = Utils.itemStackFromString(args.getString(2));
        CommandUtils.validateNotNull(itemStack, Lang.WRONG_ITEMSTACK_FORMAT);

        // create and add the item spawner
        arena.addDropper(new ItemDropper(arena, args.getString(1), player.getLocation().getBlock().getLocation(), itemStack, args.getInteger(3)));
        player.sendMessage(CoreLang.DROPPER_ADDED.getMessage(player));
        arena.save();
        return null;
    }

    @Command(
            aliases = {"addpointarea", "addpoolarea", "addpool"},
            usage = "<arena> <team name> <area name>",
            min = 3,
            max = 3,
            langDescription = @LangDescription(
                    element = "ADD_POINT_AREA",
                    langClass = Lang.class
            )
    )
    public List<String> addPointArea(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.complete(args.getString(0), arenaManager);
                case 1:
                    return CommandUtils.completeNameable(args.getString(1), CommandUtils.getDisabledArena(args, 0, arenaManager).getTeams().values());
                default:
                    return null;
            }
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        GameTeam gameTeam = (GameTeam) CommandUtils.getTeam(arena, args, 1);

        Selection selection = selectionManagerProvider.get().getSelection(player);
        CommandUtils.validateTrue(selection != null && selection.arePointsSet(), CoreLang.SELECT_AN_AREA);

        // add the point area
        gameTeam.addArea(selection.toCubiod(args.getString(2), arena));
        player.sendMessage(CoreLang.TEAM_POINT_AREA_SET.getMessage(player));
        arena.save();
        return null;
    }

    @Command(
            aliases = {"menu", "gui"},
            langDescription = @LangDescription(
                    element = "OPEN_GUI",
                    langClass = Lang.class
            ),
            usage = "[arena]",
            max = 1
    )
    public void menuCommand(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        if (args.isInBounds(0)) {
            new EditArenaUI(plugin, player, CommandUtils.getArena(args, 0, arenaManager));
            return;
        }
        // open the main menu
        new MainMenu(plugin, player);
    }
}