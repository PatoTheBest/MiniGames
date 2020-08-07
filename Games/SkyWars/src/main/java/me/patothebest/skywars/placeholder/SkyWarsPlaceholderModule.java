package me.patothebest.skywars.placeholder;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.skywars.placeholder.placeholders.all.ModePlaceholder;
import me.patothebest.skywars.placeholder.placeholders.all.NextEventPlaceholder;
import me.patothebest.skywars.placeholder.placeholders.all.NextEventTimePlaceholder;
import me.patothebest.skywars.placeholder.placeholders.all.TypePlaceholder;
import me.patothebest.skywars.SkyWars;

public class SkyWarsPlaceholderModule extends AbstractBukkitModule<SkyWars> {

    public SkyWarsPlaceholderModule(SkyWars plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<PlaceHolder> placeholders = Multibinder.newSetBinder(binder(), PlaceHolder.class);

        // Placeholders for players only
        placeholders.addBinding().to(ModePlaceholder.class);
        placeholders.addBinding().to(NextEventPlaceholder.class);
        placeholders.addBinding().to(NextEventTimePlaceholder.class);
        placeholders.addBinding().to(TypePlaceholder.class);
    }
}
