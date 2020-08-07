package me.patothebest.hungergames.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.hungergames.HungerGames;

@Singleton
public class Config extends CoreConfig implements ActivableModule {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private Config(HungerGames plugin) {
        super(plugin, "config");
        this.header = "SkyWars Configuration";

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