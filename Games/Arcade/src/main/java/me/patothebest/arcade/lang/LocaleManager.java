package me.patothebest.arcade.lang;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.arcade.Arcade;
import me.patothebest.gamecore.lang.CoreLocaleManager;
import me.patothebest.gamecore.modules.Module;

@Singleton
public class LocaleManager extends CoreLocaleManager implements Module {

    @Inject private LocaleManager(Arcade plugin) {
        super(plugin, Lang.values());
    }
}