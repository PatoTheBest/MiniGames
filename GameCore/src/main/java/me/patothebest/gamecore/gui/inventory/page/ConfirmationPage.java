package me.patothebest.gamecore.gui.inventory.page;

import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.ConfirmationPageButton;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class ConfirmationPage extends GUIPage {

    private final static ItemStack CONFIRM = new ItemStackBuilder().material(Material.EMERALD_BLOCK).name(ChatColor.GREEN + "CONFIRM");
    private final static ItemStack CANCEL = new ItemStackBuilder().material(Material.REDSTONE_BLOCK).name(ChatColor.RED + "CANCEL");

    private ItemStack infoTop;
    private ItemStack infoMiddle;

    public ConfirmationPage(Plugin plugin, Player player, ItemStack infoTop, ItemStack infoMiddle) {
        super(plugin, player, "Confirm?", 54);
        this.infoTop = infoTop;
        this.infoMiddle = infoMiddle;
        build();
    }

    public void buildPage() {
        addButton(new PlaceHolder(infoTop), 4);
        addButton(new PlaceHolder(infoMiddle), 22);

        addButton(new ConfirmationPageButton(true, CONFIRM), 27);
        addButton(new ConfirmationPageButton(true, CONFIRM), 28);
        addButton(new ConfirmationPageButton(true, CONFIRM), 29);

        addButton(new ConfirmationPageButton(true, CONFIRM), 36);
        addButton(new ConfirmationPageButton(true, CONFIRM), 37);
        addButton(new ConfirmationPageButton(true, CONFIRM), 38);

        addButton(new ConfirmationPageButton(true, CONFIRM), 45);
        addButton(new ConfirmationPageButton(true, CONFIRM), 46);
        addButton(new ConfirmationPageButton(true, CONFIRM), 47);

        addButton(new ConfirmationPageButton(false, CANCEL), 33);
        addButton(new ConfirmationPageButton(false, CANCEL), 34);
        addButton(new ConfirmationPageButton(false, CANCEL), 35);

        addButton(new ConfirmationPageButton(false, CANCEL), 42);
        addButton(new ConfirmationPageButton(false, CANCEL), 43);
        addButton(new ConfirmationPageButton(false, CANCEL), 44);

        addButton(new ConfirmationPageButton(false, CANCEL), 51);
        addButton(new ConfirmationPageButton(false, CANCEL), 52);
        addButton(new ConfirmationPageButton(false, CANCEL), 53);
    }

    public void destroy() {
        infoMiddle = null;
        infoTop = null;
    }

    public abstract void onConfirm();

    public abstract void onCancel();

}
