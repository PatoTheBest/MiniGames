package me.patothebest.gamecore.logger;

import com.google.inject.matcher.Matchers;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class LoggerModule extends AbstractBukkitModule<CorePlugin> {

    public LoggerModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new LoggerTypeListener(plugin));
    }
}
