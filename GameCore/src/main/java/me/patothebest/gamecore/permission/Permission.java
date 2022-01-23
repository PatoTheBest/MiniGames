package me.patothebest.gamecore.permission;

import me.patothebest.gamecore.PluginConfig;
import org.bukkit.Bukkit;

public enum Permission {

    USER("user"),
    CHOOSE_TEAM("choose.team"),
    CHOOSE_TEAM_OVERRIDE("choose.team.override"),
    CHOOSE_WEATHER("choose.weather"),
    CHOOSE_TIME("choose.time"),
    CHOOSE_ARENA("choose.arena"),
    CHAT_COLORS("chat.colors"),
    FORCE_START("forcestart"),
    SETUP("setup", "Setup"),
    KIT("kitadmin", "KitAdmin"),
    ADMIN("admin", "Admin");

    private final org.bukkit.permissions.Permission bukkitPermission;
    private final String permissionRaw;
    private final String displayName;

    Permission(String permissionString) {
        this(permissionString, null);
    }

    Permission(String permissionString, String displayName) {
        String perm = PluginConfig.PERMISSION_PREFIX + "." + permissionString;
        this.permissionRaw = perm;

        if(Bukkit.getServer().getPluginManager().getPermission(perm) == null) {
            Bukkit.getServer().getPluginManager().addPermission(new org.bukkit.permissions.Permission(perm));
        }

        bukkitPermission = Bukkit.getServer().getPluginManager().getPermission(perm);
        this.displayName = displayName;
    }

    public org.bukkit.permissions.Permission getBukkitPermission() {
        return bukkitPermission;
    }

    public String getPermissionRaw() {
        return permissionRaw;
    }

    public String getDisplayName() {
        return displayName;
    }
}
