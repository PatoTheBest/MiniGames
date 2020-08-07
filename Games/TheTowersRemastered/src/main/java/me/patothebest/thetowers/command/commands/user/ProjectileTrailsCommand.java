package me.patothebest.thetowers.command.commands.user;

import com.google.inject.Inject;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.cosmetics.projectiletrails.ProjectileManager;
import me.patothebest.gamecore.cosmetics.shop.ShopFactory;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ParentCommandModule;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@ChildOf(BaseCommand.class)
public class ProjectileTrailsCommand implements ParentCommandModule {

    private final ShopFactory shopFactory;
    private final ProjectileManager arrowManager;

    @Inject
    private ProjectileTrailsCommand(ShopFactory shopFactory, ProjectileManager arrowManager) {
        this.shopFactory = shopFactory;
        this.arrowManager = arrowManager;
    }

    @Command(
            aliases = {"trails"},
            langDescription = @LangDescription(
                    element = "SHOP_PROJECTILE_TRAIL_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public void trails(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        shopFactory.createShopMenu(player, arrowManager);
    }
}
