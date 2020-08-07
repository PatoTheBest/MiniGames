package me.patothebest.gamecore.commands.user;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

@ChildOf(BaseCommand.class)
public class UserKitCommand implements RegisteredCommandModule {

    private final CorePlugin corePlugin;
    private final KitManager kitManager;
    private final PlayerManager playerManager;
    private final UserGUIFactory userGUIFactory;

    @Inject private UserKitCommand(CorePlugin corePlugin, KitManager kitManager, PlayerManager playerManager, UserGUIFactory userGUIFactory) {
        this.corePlugin = corePlugin;
        this.kitManager = kitManager;
        this.playerManager = playerManager;
        this.userGUIFactory = userGUIFactory;
    }

    @Command(
            aliases = {"kit"},
            max = 2,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "KIT_COMMAND"
            )
    )
    public void kitCommand(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
//        new ArenaKitShopUI(corePlugin, kitManager, playerManager, player);
        userGUIFactory.openKitShop(player);
    }

    @Command(
            aliases = {"layout"},
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "KIT_COMMAND"
            )
    )
    public List<String> kitLayoutCommand(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), Utils.toList(kitManager.getEnabledKits()));
            }

            return null;
        }
        Player player = CommandUtils.getPlayer(sender);
        Kit kit = kitManager.getKits().get(args.getString(0));
        CommandUtils.validateNotNull(kit, CoreLang.SHOP_ITEM_NOT_FOUND);
        IPlayer player1 = playerManager.getPlayer(player);
        userGUIFactory.openKitLayoutEditor(player1, kit);
        return null;
    }
}
