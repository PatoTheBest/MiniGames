package me.patothebest.gamecore.privatearenas.ui;

import me.patothebest.gamecore.gui.inventory.button.ConfirmButton;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
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

public class KickPlayerUI extends StaticPaginatedUI<Player> {

    private final PrivateArena privateArena;
    private final ButtonAction backAction;
    private final ItemStack displayItem;

    KickPlayerUI(Plugin plugin, Player player, PrivateArena privateArena, ItemStack displayItem, ButtonAction backAction) {
        super(plugin, player,
                CoreLang.GUI_PRIVATE_ARENA_KICK_PLAYERS, () -> privateArena.getArena().getPlayers());
        this.privateArena = privateArena;
        this.displayItem = displayItem;
        this.backAction = backAction;
        build();
    }

    @Override
    protected GUIButton createButton(Player kickedPlayer) {
        ItemStackBuilder kickPlayerItem = new ItemStackBuilder().skullOwner(kickedPlayer.getName()).name(CoreLang.GUI_PRIVATE_ARENA_KICK.replace(getPlayer(), kickedPlayer.getName()));

        if (privateArena.getCoHosts().contains(kickedPlayer.getName()) || privateArena.getOwner() == kickedPlayer) {
            kickPlayerItem.lore(CoreLang.GUI_PRIVATE_ARENA_CANT_KICK.getMessage(getPlayer()));
            return new SimpleButton(kickPlayerItem);
        } else {
            return new ConfirmButton(kickPlayerItem, displayItem, kickPlayerItem, () -> {
                privateArena.getArena().removePlayer(kickedPlayer);
                CoreLang.GUI_PRIVATE_ARENA_KICKED.replaceAndSend(getPlayer(), kickedPlayer.getName());
                new KickPlayerUI(plugin, getPlayer(), privateArena, displayItem, backAction);
            });
        }
    }

    @Override
    protected void buildFooter() {
        addButton(new BackButton(getPlayer(), backAction), 47);
    }
}
