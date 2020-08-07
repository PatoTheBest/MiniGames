package me.patothebest.gamecore.gui.inventory.itemeditor;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ItemPage extends GUIMultiPage {

    private static final List<Material> materials = new ArrayList<>();
    private final String filter;
    private final ItemStackBuilder itemStack;
    private final UpdateAction updateAction;

    public ItemPage(Plugin plugin, Player player, String filter, ItemStackBuilder itemStack, UpdateAction updateAction) {
        super(plugin, player, "Choose an item");
        this.filter = filter;
        this.updateAction = updateAction;
        this.itemStack = itemStack;
        build();
    }


    public ItemPage(Plugin plugin, Player player, ItemStackBuilder itemStack, UpdateAction updateAction) {
        super(plugin, player, "Choose an item");
        this.updateAction = updateAction;
        this.filter = null;
        this.itemStack = itemStack;
        build();
    }

    @Override
    protected void buildContent() {
        final int[] i = {0};
        Stream<Material> stream = materials.stream();
        if (filter != null) {
            stream = stream.filter(material -> material.name().toLowerCase().contains(filter.toLowerCase()));
        }
        stream.skip(currentPage*pageSize).limit(pageSize).forEach(material -> {
            addButton(new SimpleButton(new ItemStackBuilder().material(material), () -> {
                updateAction.onUpdate(itemStack.material(material));
                new ItemMainPage(plugin, player, itemStack, updateAction);
            }), i[0]);
            i[0]++;
        });

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), () -> new ItemMainPage(plugin, player, itemStack, updateAction)), 47);
        addButton(new AnvilButton(new ItemStackBuilder().material(Material.OAK_SIGN).name(getPlayer(), CoreLang.GUI_EDIT_ITEM_FILTER)).action(new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                new ItemPage(plugin, player, output, itemStack, updateAction);
            }

            @Override
            public void onCancel() {
                new ItemPage(plugin, player, itemStack, updateAction);
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.OAK_SIGN).name("Dirt")), 51);
    }

    @Override
    protected int getListCount() {
        if (filter != null) {
            return (int) materials.stream().filter(material -> material.name().toLowerCase().contains(filter.toLowerCase())).count();
        }
        return materials.size();
    }

    static {
        Arrays.asList(Material.values()).forEach(material -> {
            if (material.isItem() && material.isSupported() && material.parseMaterial() != null) {
                materials.add(material);
            }
        });
    }
}
