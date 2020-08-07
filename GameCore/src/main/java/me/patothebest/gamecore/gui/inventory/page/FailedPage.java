package me.patothebest.gamecore.gui.inventory.page;

import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class FailedPage extends GUIPage {

    private String[] reason;

    public FailedPage(Plugin plugin, Player player, String... reason) {
        super(plugin, player, "Error!", 54);
        this.reason = reason;
        build();
    }

    public void buildPage() {
        ItemStack confirm = new ItemStackBuilder().material(Material.REDSTONE_BLOCK).name(ChatColor.RED + "ERROR:").lore(reason);
        for (int i = 0; i < 54; i++) {
            addButton(new PlaceHolder(confirm), i);
        }

    }

    public void destroy() {
        this.reason = null;
    }

}
