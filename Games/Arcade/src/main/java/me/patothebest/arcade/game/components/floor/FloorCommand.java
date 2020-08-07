package me.patothebest.arcade.game.components.floor;

import com.google.inject.Inject;
import me.patothebest.arcade.game.Game;
import me.patothebest.arcade.game.commands.AbstractGameCommand;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.lang.Lang;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Provider;
import java.util.List;

public class FloorCommand extends AbstractGameCommand {

    private final Provider<SelectionManager> selectionManagerProvider;

    @Inject private FloorCommand(ArenaManager arenaManager, Provider<SelectionManager> selectionManagerProvider) {
        super(arenaManager);
        this.selectionManagerProvider = selectionManagerProvider;
    }

    @Command(
            aliases = "setfloor",
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "SET_FLOOR"
            )
    )
    public List<String> setFloor(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        Game game = getGame(args, arena);

        Selection selection = selectionManagerProvider.get().getSelection(player);
        CommandUtils.validateTrue(selection != null && selection.arePointsSet(), CoreLang.SELECT_AN_AREA);

        // set the arena area
        game.getComponent(FloorComponent.class).setFloor(selection.toCubiod(arena.getName(), arena));
        player.sendMessage(Lang.FLOOR_SET.getMessage(player));
        arena.save();
        return null;
    }
}
