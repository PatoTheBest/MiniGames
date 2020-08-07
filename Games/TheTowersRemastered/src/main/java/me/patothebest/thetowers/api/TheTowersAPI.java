package me.patothebest.thetowers.api;

import me.patothebest.thetowers.TheTowersRemastered;
import net.megaplanet.thetowers.api.IArena;

public class TheTowersAPI {

    // -------------------------------------------- //
    // INSTANCE
    // -------------------------------------------- //

    private static TheTowersRemastered plugin;

    public static void setPlugin(TheTowersRemastered plugin){
        TheTowersAPI.plugin = plugin;
    }

    // -------------------------------------------- //
    // API METHODS
    // -------------------------------------------- //

    /**
     * Checks whether or not an arena with the specified name exists
     *
     * @param arenaName the arena name
     * @return whether or not the arena exists
     */
    public static boolean arenaExists(String arenaName) {
        return getArena(arenaName) != null;
    }

    /**
     * Get's the arena by the given name
     *
     * @param arenaName the arena name
     * @return the arena object or null if the arena doesn't exist
     */
    public static IArena getArena(String arenaName) {
        return (IArena) plugin.getArenaManager().getArenas().get(arenaName);
    }
}