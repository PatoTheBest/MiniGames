package me.patothebest.gamecore.privatearenas.ui;

import me.patothebest.gamecore.gui.inventory.button.ConfirmButton;
import me.patothebest.gamecore.gui.inventory.page.StaticPaginatedUI;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.privatearenas.PrivateArena;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import me.patothebest.gamecore.gui.inventory.button.ButtonAction;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.stream.Collectors;

public class RemoveCoHostUI extends StaticPaginatedUI<String> {

    private final PlayerManager playerManager;
    private final PrivateArena privateArena;
    private final ButtonAction backAction;
    private final ItemStack displayItem;

    RemoveCoHostUI(Plugin plugin, PlayerManager playerManager, Player player, PrivateArena privateArena, ItemStack displayItem, ButtonAction backAction) {
        super(plugin, player,
                CoreLang.GUI_PRIVATE_ARENA_REMOVE_CO_HOST,
                () -> privateArena.getCoHosts().stream().filter(s -> !privateArena.getOwnerName().equalsIgnoreCase(s)).collect(Collectors.toList()));
        this.playerManager = playerManager;
        this.privateArena = privateArena;
        this.backAction = backAction;
        this.displayItem = displayItem;
        build();
    }

    @Override
    protected GUIButton createButton(String coHost) {
        ItemStackBuilder removeCoHostItem = new ItemStackBuilder().skullOwner(coHost).name(CoreLang.GUI_PRIVATE_ARENA_DEMOTE_CO_HOST.replace(getPlayer(), coHost));
        return new ConfirmButton(removeCoHostItem, displayItem, removeCoHostItem, () -> {
            privateArena.getCoHosts().remove(coHost);
            IPlayer coHostRemoved = playerManager.getPlayer(coHost);

            if (coHostRemoved != null) {
                if (coHostRemoved.getCurrentArena() == privateArena.getArena()) {
                    if (!privateArena.getArena().isInGame() && !coHostRemoved.getPlayer().hasPermission(Permission.ADMIN.getBukkitPermission())) {
                        coHostRemoved.getPlayer().getInventory().setItem(7, new ItemStackBuilder(Material.AIR));
                        coHostRemoved.getPlayer().closeInventory();
                    }
                }
            }

            CoreLang.GUI_PRIVATE_ARENA_CO_HOST_REMOVED.replaceAndSend(getPlayer(), coHost);
            new RemoveCoHostUI(plugin, playerManager, getPlayer(), privateArena, displayItem, backAction);
        });
    }

    @Override
    protected void buildFooter() {
        addButton(new BackButton(getPlayer(), backAction), 47);
    }
}
