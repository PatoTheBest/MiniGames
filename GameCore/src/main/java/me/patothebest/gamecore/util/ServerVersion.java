package me.patothebest.gamecore.util;

import org.bukkit.Bukkit;

public class ServerVersion {

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static String getBukkitVersion() {
        return Bukkit.getVersion();
    }

}
