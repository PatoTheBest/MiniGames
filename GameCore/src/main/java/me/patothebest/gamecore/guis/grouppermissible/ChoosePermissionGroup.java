package me.patothebest.gamecore.guis.grouppermissible;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import me.patothebest.gamecore.gui.inventory.page.GenericGUI;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.permission.GroupPermissible;
import me.patothebest.gamecore.permission.PermissionGroupManager;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ChoosePermissionGroup extends GUIMultiPage {

    private final GroupPermissible groupPermissible;
    private final GenericGUI genericGUI;
    private final PermissionGroupManager permissionGroupManager;

    @Inject public ChoosePermissionGroup(Plugin plugin, @Assisted Player player, @Assisted GroupPermissible groupPermissible, @Assisted GenericGUI genericGUI, PermissionGroupManager permissionGroupManager) {
        super(plugin, player, "Choose permission group");
        this.groupPermissible = groupPermissible;
        this.genericGUI = genericGUI;
        this.permissionGroupManager = permissionGroupManager;
        build();
    }

    @Override
    protected void buildContent() {
        final int[] i = new int[]{0};

        permissionGroupManager.getPermissionGroups().values().stream().skip(currentPage * pageSize).limit(pageSize).forEach(permissionGroup -> {
            ItemStackBuilder itemStack = new ItemStackBuilder().material(Material.PAPER).name(ChatColor.GREEN + permissionGroup.getName());
            if (groupPermissible.getPermissionGroup().getName().equalsIgnoreCase(permissionGroup.getName())) {
                itemStack.enchant(Enchantment.WATER_WORKER, 1);
            }
            addButton(new SimpleButton(itemStack).action(() -> {
                groupPermissible.setPermissionGroup(permissionGroup);
                refresh();
            }), i[0]);
            i[0]++;
        });

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), genericGUI::onBack), 47);
        addButton(new AnvilButton(new ItemStackBuilder().material(Material.EMERALD).name(ChatColor.GREEN + "Add permission group")).action(new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                permissionGroupManager.createGroup(output);
                new ChoosePermissionGroup(plugin, player, groupPermissible, genericGUI, permissionGroupManager);
            }

            @Override
            public void onCancel() {
                new ChoosePermissionGroup(plugin, player, groupPermissible, genericGUI, permissionGroupManager);
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.PAPER).name("permission")), 51);
    }

    @Override
    protected int getListCount() {
        return permissionGroupManager.getPermissionGroups().values().size();
    }

}
