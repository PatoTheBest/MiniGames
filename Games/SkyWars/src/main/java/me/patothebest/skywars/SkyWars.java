package me.patothebest.skywars;

import com.google.inject.Binder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.PluginInfo;

@PluginInfo(
        pluginName = "SkyWars",
        worldPrefix = "SkyWars_",
        pluginTitle = "SKYWARS",
        langPrefix = "SkyWars",
        gameTitle = "SkyWars",
        placeholderPrefix = "skywars",
        permissionPrefix = "skywars",
        sqlPrefix = "skywars",
        baseCommand = "sw",
        loggerPrefix = "SW",
        // TODO: Change this when possible
        resourceId = "",
        header = " _____ _          _    _                \n" +
                "/  ___| |        | |  | |               \n" +
                "\\ `--.| | ___   _| |  | | __ _ _ __ ___ \n" +
                " `--. \\ |/ / | | | |/\\| |/ _` | '__/ __|\n" +
                "/\\__/ /   <| |_| \\  /\\  / (_| | |  \\__ \\\n" +
                "\\____/|_|\\_\\\\__, |\\/  \\/ \\__,_|_|  |___/\n" +
                "             __/ |                      \n" +
                "            |___/                       "
)
public class SkyWars extends CorePlugin {

    @Override
    public void configure(Binder binder) {
        binder.install(new SkyWarsModule(this));
    }
}