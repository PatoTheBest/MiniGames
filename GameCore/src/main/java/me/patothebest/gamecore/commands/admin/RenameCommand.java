package me.patothebest.gamecore.commands.admin;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.command.HiddenCommand;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.gui.anvil.AnvilGUI;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ChildOf(BaseCommand.class)
public class RenameCommand implements RegisteredCommandModule {

    private final Map<String, ItemStack> items = new HashMap<>();
    private final CorePlugin plugin;

    @Inject private RenameCommand(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Command(
            aliases = {"rename"},
            max = 0,
            langDescription = @LangDescription(
                    element = "Rename an item in hand",
                    langClass = CoreLang.class
            )
    )
    @HiddenCommand
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> rename(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        AnvilGUI gui = new AnvilGUI(plugin, player, event -> {
            if(event.getSlot() == AnvilSlot.OUTPUT){
                event.setWillClose(true);
                ItemStack item = items.get(event.getPlayerName());
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(event.getName().replace("&", "§"));
                item.setItemMeta(itemMeta);
                Bukkit.getPlayer(event.getPlayerName()).setItemInHand(item);
                items.remove(event.getPlayerName());
            } else {
                event.setWillClose(true);
                Bukkit.getPlayer(event.getPlayerName()).setItemInHand(items.get(event.getPlayerName()));
                items.remove(event.getPlayerName());
            }
        });

        gui.setSlot(AnvilSlot.INPUT_LEFT, player.getItemInHand());
        items.put(player.getName(), player.getItemInHand());
        player.closeInventory();
        player.setItemInHand(null);
        gui.open();
        return null;
    }


}
