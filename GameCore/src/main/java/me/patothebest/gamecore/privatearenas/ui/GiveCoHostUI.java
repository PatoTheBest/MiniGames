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
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.stream.Collectors;

public class GiveCoHostUI extends StaticPaginatedUI<Player> {

    private final PlayerManager playerManager;
    private final PrivateArena privateArena;
    private final ButtonAction backAction;
    private final ItemStack giveCoHost;

    GiveCoHostUI(Plugin plugin, PlayerManager playerManager, Player player, PrivateArena privateArena, ItemStack giveCoHost, ButtonAction backAction) {
        super(plugin, player,
                CoreLang.GUI_PRIVATE_ARENA_ADD_CO_HOST,
                () -> privateArena.getArena().getPlayers().stream()
                        .filter(player1 -> !privateArena.getCoHosts().contains(player1.getName())).collect(Collectors.toList()));
        this.playerManager = playerManager;
        this.privateArena = privateArena;
        this.backAction = backAction;
        this.giveCoHost = giveCoHost;
        build();
    }

    @Override
    protected GUIButton createButton(Player coHost) {
        ItemStackBuilder makeCoHost = new ItemStackBuilder().skullOwner(coHost.getName()).name(CoreLang.GUI_PRIVATE_ARENA_GIVE_CO_HOST.replace(getPlayer(), coHost.getName()));
        return new ConfirmButton(makeCoHost, giveCoHost, makeCoHost, () -> {
            privateArena.getCoHosts().add(coHost.getName());
            IPlayer coHostIPlayer = playerManager.getPlayer(player);
            if (coHostIPlayer.isInArena() && coHostIPlayer.getCurrentArena() == privateArena.getArena() && !privateArena.getArena().isInGame()) {
                coHost.getInventory().setItem(7, new ItemStackBuilder().material(Material.COMPARATOR).name(player, CoreLang.GUI_PRIVATE_ARENA_LOBBY_MENU));
            }
            CoreLang.GUI_PRIVATE_ARENA_CO_HOST_MADE.replaceAndSend(getPlayer(), coHost.getName());
            new GiveCoHostUI(plugin, playerManager, player, privateArena, giveCoHost, backAction);
        });
    }

    @Override
    protected void buildFooter() {
        addButton(new BackButton(getPlayer(), backAction), 47);
    }
}
