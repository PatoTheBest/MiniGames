package me.patothebest.skywars.lang;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.lang.CoreLocaleManager;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.skywars.SkyWars;

@Singleton
public class LocaleManager extends CoreLocaleManager implements Module {

    @Inject private LocaleManager(SkyWars plugin) {
        super(plugin, Lang.values());
    }
}