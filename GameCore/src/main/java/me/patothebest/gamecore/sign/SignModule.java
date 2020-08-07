package me.patothebest.gamecore.sign;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class SignModule extends AbstractBukkitModule<CorePlugin> {

    public SignModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        registerModule(SignManager.class);
        registerModule(SignFile.class);
        registerModule(SignListener.class);
        registerModule(SignCommand.class);
        registerModule(SignCommand.Parent.class);
    }
}
