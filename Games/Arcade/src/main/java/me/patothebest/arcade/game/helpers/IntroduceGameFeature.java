package me.patothebest.arcade.game.helpers;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import me.patothebest.gamecore.util.Sounds;

import javax.inject.Inject;

public class IntroduceGameFeature extends AbstractRunnableFeature {

    private final CorePlugin plugin;

    private int time = 5;

    @Inject public IntroduceGameFeature(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        time--;

        if(time == 0) {
            arena.nextPhase();
            cancel();
            return;
        }

        arena.getPlayers().forEach(player -> {
            player.setLevel(time);
            Sounds.UI_BUTTON_CLICK.play(player);
        });
    }

    @Override
    public void initializeFeature() {
        time = 5;
        runTaskTimer(plugin, 0L, 20L);
    }
}
