package me.patothebest.arcade;

import com.google.inject.Binder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.PluginInfo;

@PluginInfo(
        pluginName = "Arcade",
        worldPrefix = "Arcade_",
        pluginTitle = "ARCADE",
        langPrefix = "Arcade",
        gameTitle = "Arcade",
        placeholderPrefix = "arcade",
        permissionPrefix = "arcade",
        sqlPrefix = "arcade",
        baseCommand = "arcade",
        loggerPrefix = "ARCADE",
        resourceId = "",
        header = "                                                      \n" +
                " _______  _______  _______  _______  ______   _______ \n" +
                "(  ___  )(  ____ )(  ____ \\(  ___  )(  __  \\ (  ____ \\\n" +
                "| (   ) || (    )|| (    \\/| (   ) || (  \\  )| (    \\/\n" +
                "| (___) || (____)|| |      | (___) || |   ) || (__    \n" +
                "|  ___  ||     __)| |      |  ___  || |   | ||  __)   \n" +
                "| (   ) || (\\ (   | |      | (   ) || |   ) || (      \n" +
                "| )   ( || ) \\ \\__| (____/\\| )   ( || (__/  )| (____/\\\n" +
                "|/     \\||/   \\__/(_______/|/     \\|(______/ (_______/\n" +
                "                                                      "
)
public class Arcade extends CorePlugin {

    @Override
    public void configure(Binder binder) {
        binder.install(new ArcadeModule(this));
    }
}