package me.patothebest.gamecore.privatearenas.ui;

import me.patothebest.gamecore.gui.inventory.button.ConfirmButton;
import me.patothebest.gamecore.gui.inventory.page.StaticPaginatedUI;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.privatearenas.PrivateArena;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import me.patothebest.gamecore.gui.inventory.button.ButtonAction;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.stream.Collectors;

public class AddPlayerToWhitelistUI extends StaticPaginatedUI<Player> {

    private final PrivateArena privateArena;
    private final ButtonAction backAction;
    private final ItemStack displayItem;

    AddPlayerToWhitelistUI(Plugin plugin, Player player, PrivateArena privateArena, ItemStack displayItem, ButtonAction backAction) {
        super(plugin, player,
                CoreLang.GUI_PRIVATE_ARENA_ADD_PLAYERS_TO_WHITELIST,
                () -> Bukkit.getOnlinePlayers().stream().filter(o -> !privateArena.getWhitelistedPlayers().contains(o.getName())).collect(Collectors.toList()));
        this.privateArena = privateArena;
        this.backAction = backAction;
        this.displayItem = displayItem;
        build();
    }

    @Override
    protected GUIButton createButton(Player whitelistPlayer) {
        ItemStackBuilder addToWhitelist = new ItemStackBuilder().skullOwner(whitelistPlayer.getName()).name(CoreLang.GUI_PRIVATE_ARENA_ADD_PLAYER_TO_WHITELIST.replace(getPlayer(), whitelistPlayer.getName()));
        return new ConfirmButton(addToWhitelist, displayItem, addToWhitelist, () -> {
            privateArena.getWhitelistedPlayers().add(whitelistPlayer.getName());
            CoreLang.GUI_PRIVATE_ARENA_PLAYER_ADDED_TO_WHITELIST.replaceAndSend(getPlayer(), whitelistPlayer.getName());
            new AddPlayerToWhitelistUI(plugin, player, privateArena, displayItem, backAction);
        });
    }

    @Override
    protected void buildFooter() {
        addButton(new BackButton(getPlayer(), backAction), 47);
        addButton(new AnvilButton(new ItemStackBuilder().material(Material.WRITABLE_BOOK).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_ADD_PLAYER_TO_WHITELIST_BY_NAME), new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                privateArena.getWhitelistedPlayers().add(output);
                CoreLang.GUI_PRIVATE_ARENA_PLAYER_ADDED_TO_WHITELIST.replaceAndSend(getPlayer(), output);
                new AddPlayerToWhitelistUI(plugin, player, privateArena, displayItem, backAction);
            }

            @Override
            public void onCancel() {
                new AddPlayerToWhitelistUI(plugin, player, privateArena, displayItem, backAction);
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder(Material.PAPER).name("player")), 51);
    }
}
