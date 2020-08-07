package me.patothebest.gamecore.privatearenas.ui;

import me.patothebest.gamecore.gui.inventory.button.ConfirmButton;
import me.patothebest.gamecore.gui.inventory.page.StaticPaginatedUI;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.privatearenas.PrivateArena;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import me.patothebest.gamecore.gui.inventory.button.ButtonAction;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.stream.Collectors;

public class RemovePlayerFromWhitelistUI extends StaticPaginatedUI<String> {

    private final PrivateArena privateArena;
    private final ButtonAction backAction;
    private final ItemStack displayItem;

    RemovePlayerFromWhitelistUI(Plugin plugin, Player player, PrivateArena privateArena, ItemStack displayItem, ButtonAction backAction) {
        super(plugin, player,
                CoreLang.GUI_PRIVATE_ARENA_REMOVE_PLAYERS_FROM_WHITELIST,
                () -> privateArena.getWhitelistedPlayers().stream()
                        .filter(s -> !privateArena.getOwnerName().equalsIgnoreCase(s)).collect(Collectors.toList()));
        this.privateArena = privateArena;
        this.backAction = backAction;
        this.displayItem = displayItem;
        build();
    }

    @Override
    protected GUIButton createButton(String whitelistedPlayer) {
        ItemStackBuilder removeCoHostItem = new ItemStackBuilder().skullOwner(whitelistedPlayer).name(CoreLang.GUI_PRIVATE_ARENA_REMOVE_PLAYER_FROM_WHITELIST.replace(getPlayer(), whitelistedPlayer));
        return new ConfirmButton(removeCoHostItem, displayItem, removeCoHostItem, () -> {
            privateArena.getWhitelistedPlayers().remove(whitelistedPlayer);
            CoreLang.GUI_PRIVATE_ARENA_PLAYER_REMOVED_ROM_WHITELIST.replaceAndSend(getPlayer(), whitelistedPlayer);
            new RemovePlayerFromWhitelistUI(plugin, getPlayer(), privateArena, displayItem, backAction);
        });
    }

    @Override
    protected void buildFooter() {
        addButton(new BackButton(getPlayer(), backAction), 47);
    }
}
