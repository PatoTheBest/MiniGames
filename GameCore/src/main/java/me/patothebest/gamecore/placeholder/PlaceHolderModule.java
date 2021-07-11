package me.patothebest.gamecore.placeholder;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.placeholder.placeholders.all.ArenaPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.all.ArenaStatusPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.all.CountdownPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.all.DatePlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.all.IsCountingPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.all.MapPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.all.MaxPlayersPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.all.MinPlayersPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.all.PlayersPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.all.PlayingPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.all.WorldNamePlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.CustomNamePlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.GameDeathsPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.GameKillsPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.KDPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.LevelColorPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.LevelFormattedPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.LevelPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.LevelProgressBarPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.LevelProgressLabelPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.MoneyPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.NamePlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.PointsColorPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.PointsFormattedPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.PointsPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.StatsPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.top.TopNamePlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.top.TopPlaceholder;
import me.patothebest.gamecore.placeholder.placeholders.player.top.TopValuePlaceholder;

public class PlaceHolderModule extends AbstractBukkitModule<CorePlugin> {

    public PlaceHolderModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<PlaceHolder> placeholders = Multibinder.newSetBinder(binder(), PlaceHolder.class);

        // Placeholders for signs and players
        placeholders.addBinding().to(ArenaPlaceholder.class);
        placeholders.addBinding().to(ArenaStatusPlaceholder.class);
        placeholders.addBinding().to(CountdownPlaceholder.class);
        placeholders.addBinding().to(DatePlaceholder.class);
        placeholders.addBinding().to(IsCountingPlaceholder.class);
        placeholders.addBinding().to(MapPlaceholder.class);
        placeholders.addBinding().to(MaxPlayersPlaceholder.class);
        placeholders.addBinding().to(MinPlayersPlaceholder.class);
        placeholders.addBinding().to(PlayersPlaceholder.class);
        placeholders.addBinding().to(PlayingPlaceholder.class);
        placeholders.addBinding().to(WorldNamePlaceholder.class);

        // Placeholders for players only
        placeholders.addBinding().to(CustomNamePlaceholder.class);
        placeholders.addBinding().to(MoneyPlaceholder.class);
        placeholders.addBinding().to(NamePlaceholder.class);
        placeholders.addBinding().to(GameDeathsPlaceholder.class);
        placeholders.addBinding().to(GameKillsPlaceholder.class);
        placeholders.addBinding().to(KDPlaceholder.class);
        placeholders.addBinding().to(LevelColorPlaceholder.class);
        placeholders.addBinding().to(LevelFormattedPlaceholder.class);
        placeholders.addBinding().to(LevelPlaceholder.class);
        placeholders.addBinding().to(LevelProgressBarPlaceholder.class);
        placeholders.addBinding().to(LevelProgressLabelPlaceholder.class);
        placeholders.addBinding().to(PointsColorPlaceholder.class);
        placeholders.addBinding().to(PointsFormattedPlaceholder.class);
        placeholders.addBinding().to(PointsPlaceholder.class);
        placeholders.addBinding().to(StatsPlaceholder.class);
        placeholders.addBinding().to(TopNamePlaceholder.class);
        placeholders.addBinding().to(TopPlaceholder.class);
        placeholders.addBinding().to(TopValuePlaceholder.class);

        registerModule(PlaceHolderManager.class);
    }
}
