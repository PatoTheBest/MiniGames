package me.patothebest.gamecore.guis.grouppermissible;

import me.patothebest.gamecore.gui.inventory.page.GenericGUI;
import me.patothebest.gamecore.permission.GroupPermissible;
import org.bukkit.entity.Player;

public interface PermissionGroupUIFactory {

    ChoosePermissionGroup create(Player player, GroupPermissible groupPermissible, GenericGUI genericGUI);

}
