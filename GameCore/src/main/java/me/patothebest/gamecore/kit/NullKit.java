package me.patothebest.gamecore.kit;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.permission.PermissionGroupManager;
import org.bukkit.inventory.ItemStack;

public class NullKit extends Kit {

    @Inject private NullKit(CorePlugin plugin, PermissionGroupManager permissionGroupManager) {
        super(plugin, permissionGroupManager, "Default", new ItemStack[1], new ItemStack[1]);
    }
}
