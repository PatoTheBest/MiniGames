package me.patothebest.gamecore.privatearenas.ui;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.privatearenas.PrivateArena;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import me.patothebest.gamecore.gui.inventory.button.ButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ArenaOptionsUI extends GUIPage {

    private final ButtonAction backAction;
    private final PrivateArena privateArena;

    protected ArenaOptionsUI(Plugin plugin, Player player, ButtonAction backAction, PrivateArena privateArena) {
        super(plugin, player, CoreLang.GUI_PRIVATE_ARENA_OPTIONS, 36);
        this.backAction = backAction;
        this.privateArena = privateArena;
        build();
    }

    @Override
    protected void buildPage() {
        addOption(CoreLang.GUI_PRIVATE_ARENA_WHITELIST, Material.PAPER, privateArena.getArena().isWhitelist(), () -> {
            privateArena.getArena().setWhitelist(!privateArena.getArena().isWhitelist());
            refresh();
        }, 9);

        addOption(CoreLang.GUI_PRIVATE_ARENA_TEAM_SELECTOR, Material.LIGHT_BLUE_WOOL, privateArena.getArena().isTeamSelector(), () -> {
            privateArena.getArena().setTeamSelector(!privateArena.getArena().isTeamSelector());
            refresh();
        }, 11);

        addOption(CoreLang.GUI_PRIVATE_ARENA_PUBLIC_JOIN, Material.OAK_FENCE_GATE, privateArena.getArena().isPublicJoinable(), () -> {
            privateArena.getArena().setPublicJoinable(!privateArena.getArena().isPublicJoinable());
            refresh();
        }, 13);

        addOption(CoreLang.GUI_PRIVATE_ARENA_PUBLIC_SPECTATE, Material.COMPASS, privateArena.getArena().isPublicSpectable(), () -> {
            privateArena.getArena().setPublicSpectable(!privateArena.getArena().isPublicSpectable());
            refresh();
        }, 15);

        addOption(CoreLang.GUI_PRIVATE_ARENA_STATS, Material.DIAMOND_SWORD, !privateArena.getArena().isDisableStats(), () -> {
            privateArena.getArena().setDisableStats(!privateArena.getArena().isDisableStats());
            refresh();
        }, 17);

        addButton(new BackButton(getPlayer(), backAction), 27);
    }

    private void addOption(CoreLang title, Material displayMaterial, boolean state, ButtonAction onClick, int slot) {
        addButton(new PlaceHolder(new ItemStackBuilder(displayMaterial).name(title.getMessage(getPlayer()))), slot);
        addButton(new SimpleButton(new ItemStackBuilder().createTogglableItem(getPlayer(), state)).action(onClick), slot + 9);
    }
}
