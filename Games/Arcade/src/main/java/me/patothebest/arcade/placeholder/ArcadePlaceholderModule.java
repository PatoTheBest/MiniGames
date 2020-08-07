package me.patothebest.arcade.placeholder;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.arcade.Arcade;
import me.patothebest.arcade.placeholder.placeholders.all.GameCountPlaceholder;
import me.patothebest.arcade.placeholder.placeholders.all.GameNamePlaceholder;
import me.patothebest.arcade.placeholder.placeholders.all.TotalGamesPlaceholder;
import me.patothebest.arcade.placeholder.placeholders.all.TotalPlayersPlaceholder;
import me.patothebest.arcade.placeholder.placeholders.all.TypePlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.points.GamePointsCountPlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.points.GamePointsNamePlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.points.GamePointsPlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.points.IsTopPointsPlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.points.PlayerPlacePointsPlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.points.PointGoalNamePlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.points.PointGoalPlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.stars.GameStarsCountPlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.stars.GameStarsNamePlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.stars.IsTopStarsPlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.stars.PlayerPlacePlaceholder;
import me.patothebest.arcade.placeholder.placeholders.player.stars.StarsPlaceholder;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.placeholder.PlaceHolder;

public class ArcadePlaceholderModule extends AbstractBukkitModule<Arcade> {

    public ArcadePlaceholderModule(Arcade plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<PlaceHolder> placeholders = Multibinder.newSetBinder(binder(), PlaceHolder.class);

        // Placeholders for signs and players
        placeholders.addBinding().to(GameCountPlaceholder.class);
        placeholders.addBinding().to(GameNamePlaceholder.class);
        placeholders.addBinding().to(TotalGamesPlaceholder.class);
        placeholders.addBinding().to(TotalPlayersPlaceholder.class);
        placeholders.addBinding().to(TypePlaceholder.class);

        // Placeholders for players only
        placeholders.addBinding().to(GameStarsCountPlaceholder.class);
        placeholders.addBinding().to(GameStarsNamePlaceholder.class);
        placeholders.addBinding().to(IsTopStarsPlaceholder.class);
        placeholders.addBinding().to(PlayerPlacePlaceholder.class);
        placeholders.addBinding().to(StarsPlaceholder.class);

        placeholders.addBinding().to(GamePointsCountPlaceholder.class);
        placeholders.addBinding().to(GamePointsNamePlaceholder.class);
        placeholders.addBinding().to(GamePointsPlaceholder.class);
        placeholders.addBinding().to(IsTopPointsPlaceholder.class);
        placeholders.addBinding().to(PlayerPlacePointsPlaceholder.class);
        placeholders.addBinding().to(PointGoalNamePlaceholder.class);
        placeholders.addBinding().to(PointGoalPlaceholder.class);
    }
}
