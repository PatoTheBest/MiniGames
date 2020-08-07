package me.patothebest.thetowers.placeholder;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.thetowers.placeholder.placeholders.all.TimeElapsed;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.thetowers.TheTowersRemastered;
import me.patothebest.thetowers.placeholder.placeholders.player.GamePointsPlaceholder;
import me.patothebest.thetowers.placeholder.placeholders.player.GameTotalKillsPlaceholder;

public class TheTowersPlaceholderModule extends AbstractBukkitModule<TheTowersRemastered> {

    public TheTowersPlaceholderModule(TheTowersRemastered plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<PlaceHolder> placeholders = Multibinder.newSetBinder(binder(), PlaceHolder.class);

        // Placeholders for players only
        placeholders.addBinding().to(TimeElapsed.class);
        placeholders.addBinding().to(GamePointsPlaceholder.class);
        placeholders.addBinding().to(GameTotalKillsPlaceholder.class);
    }
}
