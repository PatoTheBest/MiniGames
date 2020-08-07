package me.patothebest.gamecore.kit.defaults;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.permission.PermissionGroupManager;
import me.patothebest.gamecore.kit.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitDefault extends Kit {

    @Inject private KitDefault(CorePlugin plugin, PermissionGroupManager permissionGroupManager) {
        super(plugin, permissionGroupManager, "Default", getDefaultArmor(), getDefaultItems());
    }

    private static ItemStack[] getDefaultArmor() {
        final ItemStack helmet = new ItemStackBuilder(Material.LEATHER_HELMET);
        final ItemStack chestplate = new ItemStackBuilder(Material.LEATHER_CHESTPLATE);
        final ItemStack legs = new ItemStackBuilder(Material.LEATHER_LEGGINGS);
        final ItemStack boots = new ItemStackBuilder(Material.LEATHER_BOOTS);
        return new ItemStack[] { boots, legs, chestplate, helmet };
    }

    private static ItemStack[] getDefaultItems() {
        return new ItemStack[] { new ItemStackBuilder(Material.BAKED_POTATO).amount(8) };
    }

    public void applyKit(Player player) {
        player.getInventory().setContents(getInventoryItems());
        player.getInventory().setArmorContents(getArmorItems());
    }
}
