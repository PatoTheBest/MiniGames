package me.patothebest.hungergames.lang;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.lang.CoreLocaleManager;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.hungergames.HungerGames;

@Singleton
public class LocaleManager extends CoreLocaleManager implements Module {

    @Inject private LocaleManager(HungerGames plugin) {
        super(plugin, Lang.values());
    }
}