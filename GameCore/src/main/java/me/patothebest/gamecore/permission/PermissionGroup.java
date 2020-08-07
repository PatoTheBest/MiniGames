package me.patothebest.gamecore.permission;

import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionGroup {

    private final String name;
    private final org.bukkit.permissions.Permission bukkitPermission;

    PermissionGroup(String name) {
        this.name = name;
        String perm = PluginConfig.PERMISSION_PREFIX + ".group." + name;

        if(Bukkit.getServer().getPluginManager().getPermission(perm) == null) {
            Bukkit.getServer().getPluginManager().addPermission(new org.bukkit.permissions.Permission(perm));
        }

        bukkitPermission = Bukkit.getServer().getPluginManager().getPermission(perm);
    }

    public boolean hasPermission(IPlayer player) {
        return hasPermission(player.getPlayer());
    }

    public boolean hasPermission(Player player) {
        return name.equalsIgnoreCase("default") || player.hasPermission(bukkitPermission);
    }

    public String getName() {
        return name;
    }

    public org.bukkit.permissions.Permission getBukkitPermission() {
        return bukkitPermission;
    }
}
