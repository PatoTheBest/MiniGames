package me.patothebest.gamecore.combat;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class CombatModule extends AbstractBukkitModule<CorePlugin> {

    public CombatModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        registerModule(CombatManager.class);
    }
}
