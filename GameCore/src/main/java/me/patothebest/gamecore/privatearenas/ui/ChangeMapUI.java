package me.patothebest.gamecore.privatearenas.ui;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.page.StaticPaginatedUI;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.privatearenas.PrivateArena;
import me.patothebest.gamecore.privatearenas.PrivateArenasManager;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import me.patothebest.gamecore.gui.inventory.button.ButtonAction;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ChangeMapUI extends StaticPaginatedUI<String> {

    private final PrivateArena privateArena;
    private final PrivateArenasManager privateArenasManager;
    private final ButtonAction backAction;

    protected ChangeMapUI(Plugin plugin, Player player, PrivateArena privateArena, PrivateArenasManager privateArenasManager, ButtonAction backAction) {
        super(plugin, player, CoreLang.CHANGE_ARENA, privateArenasManager::getEnabledArenas);
        this.privateArena = privateArena;
        this.privateArenasManager = privateArenasManager;
        this.backAction = backAction;
        build();
    }

    @Override
    protected GUIButton createButton(String item) {
        return new SimpleButton(new ItemStackBuilder()
                .material(Material.MAP)
                .name(ChatColor.GOLD + item))
                .action(() -> {
            privateArenasManager.changeArena(privateArena, item);
        });
    }

    @Override
    protected void buildFooter() {
        addButton(new BackButton(getPlayer(), backAction), 47);
    }
}
