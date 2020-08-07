package me.patothebest.gamecore.gui.inventory.page;

import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class GUIMultiPage extends GUIPage {

    protected int currentPage;
    protected int pageSize = 45;

    protected GUIMultiPage(Plugin plugin, Player player, String rawName) {
        this(plugin, player, rawName, 54);
    }

    protected GUIMultiPage(Plugin plugin, Player player, String rawName, int size) {
        super(plugin, player, rawName, size);
    }

    protected GUIMultiPage(Plugin plugin, Player player, ILang title) {
        this(plugin, player, title, 54);
    }

    protected GUIMultiPage(Plugin plugin, Player player, ILang title, int size) {
        super(plugin, player, title, size);
    }

    public void buildPage() {
        ItemStack nextPage = new ItemStackBuilder().material(Material.PAPER).amount(currentPage + 2).name(CoreLang.GUI_NEXT_PAGE.replace(getPlayer(), (currentPage + 2)));
        ItemStack previousPage = new ItemStackBuilder().material(Material.PAPER).amount(currentPage).name(CoreLang.GUI_PREVIOUS_PAGE.replace(getPlayer(), currentPage));
        ItemStack currentPageItem = new ItemStackBuilder().material(Material.PAPER).amount(currentPage + 1).name(CoreLang.GUI_YOU_ARE_ON.replace(getPlayer(), (currentPage + 1)));

        buildContent();
        if ((currentPage + 1) * pageSize < getListCount()) {
            addButton(new SimpleButton(nextPage).action(() -> {currentPage++;refresh();}), 53);
        }

        if (currentPage != 0) {
            addButton(new SimpleButton(previousPage).action(() -> {currentPage--;refresh();}), 45);
        }

        if(getListCount() != -1) {
            addButton(new PlaceHolder(currentPageItem), 49);
        }
    }

    protected abstract void buildContent();

    protected abstract int getListCount();

}
