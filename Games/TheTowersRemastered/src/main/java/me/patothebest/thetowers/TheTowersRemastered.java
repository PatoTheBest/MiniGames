package me.patothebest.thetowers;

import com.google.inject.Binder;
import me.patothebest.thetowers.api.TheTowersAPI;
import me.patothebest.thetowers.file.Config;
import me.patothebest.thetowers.file.FileManager;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.PluginInfo;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.permission.PermissionGroupManager;

import javax.inject.Inject;

@PluginInfo(
        pluginName = "TheTowersRemastered",
        worldPrefix = "TheTowersRemastered_",
        pluginTitle = "THE TOWERS",
        langPrefix = "The Towers",
        gameTitle = "TheTowers",
        placeholderPrefix = "thetowers",
        permissionPrefix = "thetowers",
        sqlPrefix = "thetowers",
        baseCommand = "tt",
        loggerPrefix = "TT",
        resourceId = "/R8Ka27Ggvk=",
        header = " _____ _        _____                          \n" +
                "|_   _| |      |_   _|                         \n" +
                "  | | | |__   ___| | _____      _____ _ __ ___ \n" +
                "  | | | '_ \\ / _ \\ |/ _ \\ \\ /\\ / / _ \\ '__/ __|\n" +
                "  | | | | | |  __/ | (_) \\ V  V /  __/ |  \\__ \\\n" +
                "  \\_/ |_| |_|\\___\\_/\\___/ \\_/\\_/ \\___|_|  |___/\n" +
                "                                               \n" +
                "                                               "
)
public class TheTowersRemastered extends CorePlugin {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    // Guice
    @Inject private ArenaManager arenaManager;
    @Inject private PermissionGroupManager permissionGroupManager;
    @Inject private FileManager fileManager;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public TheTowersRemastered() {
        TheTowersAPI.setPlugin(this);
    }

    // -------------------------------------------- //
    // GUICE
    // -------------------------------------------- //

    @Override
    public void configure(Binder binder) {
        binder.install(new TheTowersModule(this));
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    public FileManager getFileManager() {
        return fileManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PermissionGroupManager getPermissionGroupManager() {
        return permissionGroupManager;
    }

    @Override
    public Config getConfig() {
        return fileManager.getConfig();
    }

    @Override
    public String toString() {
        return "TheTowersRemastered";
    }
}