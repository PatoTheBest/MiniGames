package me.patothebest.gamecore.gui.inventory.page;

import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class SuccessPage extends GUIPage {

    public SuccessPage(Plugin plugin, Player player) {
        super(plugin, player, "Success!", 54);
        build();
    }

    public SuccessPage(Plugin plugin, Player player, boolean b) {
        super(plugin, player, "Success!", 54, true);
        build();
    }

    public void buildPage() {
        ItemStack confirm = new ItemStackBuilder().material(Material.EMERALD_BLOCK).name(ChatColor.GREEN + "Success!");
        for (int i = 0; i < 54; i++) {
            addButton(new PlaceHolder(confirm), i);
        }

    }
}
