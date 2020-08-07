package me.patothebest.arcade.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.arcade.Arcade;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.modules.ActivableModule;

@Singleton
public class Config extends CoreConfig implements ActivableModule {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private Config(Arcade plugin) {
        super(plugin, "config");
        this.header = "Arcade Configuration";

        load();
    }

    // -------------------------------------------- //
    // ON ENABLE
    // -------------------------------------------- //

    @Override
    public void onEnable() {
        readConfig();
    }


    // -------------------------------------------- //
    // CACHE
    // -------------------------------------------- //

    @Override
    public void readConfig() {
        super.readConfig();
    }
}