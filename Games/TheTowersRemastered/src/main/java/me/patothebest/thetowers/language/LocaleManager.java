package me.patothebest.thetowers.language;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.thetowers.TheTowersRemastered;
import me.patothebest.gamecore.lang.CoreLocaleManager;
import me.patothebest.gamecore.modules.Module;

@Singleton
public class LocaleManager extends CoreLocaleManager implements Module {

    @Inject private LocaleManager(TheTowersRemastered plugin) {
        super(plugin, Lang.values());
    }
}