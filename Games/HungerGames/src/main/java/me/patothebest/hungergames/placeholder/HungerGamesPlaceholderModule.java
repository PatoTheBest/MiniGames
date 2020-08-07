package me.patothebest.hungergames.placeholder;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.hungergames.HungerGames;
import me.patothebest.hungergames.placeholder.placeholders.all.NextEventPlaceholder;
import me.patothebest.hungergames.placeholder.placeholders.all.TypePlaceholder;
import me.patothebest.hungergames.placeholder.placeholders.all.NextEventTimePlaceholder;

public class HungerGamesPlaceholderModule extends AbstractBukkitModule<HungerGames> {

    public HungerGamesPlaceholderModule(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<PlaceHolder> placeholders = Multibinder.newSetBinder(binder(), PlaceHolder.class);

        // Placeholders for players only
        placeholders.addBinding().to(NextEventPlaceholder.class);
        placeholders.addBinding().to(NextEventTimePlaceholder.class);
        placeholders.addBinding().to(TypePlaceholder.class);
    }
}
