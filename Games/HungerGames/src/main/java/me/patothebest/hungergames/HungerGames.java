package me.patothebest.hungergames;

import com.google.inject.Binder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.PluginInfo;

@PluginInfo(
        pluginName = "HungerGames",
        worldPrefix = "HG_",
        pluginTitle = "HG",
        langPrefix = "HG",
        gameTitle = "HG",
        placeholderPrefix = "hg",
        permissionPrefix = "hg",
        sqlPrefix = "hungergames",
        baseCommand = "hg",
        loggerPrefix = "HG",
        // TODO: Change this when possible
        resourceId = "",
        header = " _   _                             _____                           \n" +
                "| | | |                           |  __ \\                          \n" +
                "| |_| |_   _ _ __   __ _  ___ _ __| |  \\/ __ _ _ __ ___   ___  ___ \n" +
                "|  _  | | | | '_ \\ / _` |/ _ \\ '__| | __ / _` | '_ ` _ \\ / _ \\/ __|\n" +
                "| | | | |_| | | | | (_| |  __/ |  | |_\\ \\ (_| | | | | | |  __/\\__ \\\n" +
                "\\_| |_/\\__,_|_| |_|\\__, |\\___|_|   \\____/\\__,_|_| |_| |_|\\___||___/\n" +
                "                    __/ |                                          \n" +
                "                   |___/                                           "
)
public class HungerGames extends CorePlugin {

    @Override
    public void configure(Binder binder) {
        binder.install(new HungerGamesModule(this));
    }
}