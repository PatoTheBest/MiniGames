package me.patothebest.hungergames.phase.phases;

import com.google.inject.Inject;
import me.patothebest.gamecore.feature.features.other.WorldBorderFeature;
import me.patothebest.hungergames.phase.AbstractHungerGamesPhase;
import me.patothebest.hungergames.phase.PhaseType;
import me.patothebest.hungergames.HungerGames;
import org.bukkit.configuration.ConfigurationSection;

public class BorderShrinkPhase extends AbstractHungerGamesPhase {

    private int startSize;
    private int endSize;
    private int shrinkTime;

    @Inject private BorderShrinkPhase(HungerGames plugin) {
        super(plugin);
    }

    @Override
    public void configure() {
        setPreviousPhaseFeatures(true);
    }

    @Override
    public void parseExtraData(ConfigurationSection phasesConfigurationSection) {
        startSize = phasesConfigurationSection.getInt("start-size");
        endSize = phasesConfigurationSection.getInt("end-size");
        shrinkTime = phasesConfigurationSection.getInt("shrink-time");
    }

    @Override
    public void start() {
        WorldBorderFeature worldBorderFeature = registerFeature(WorldBorderFeature.class);
        worldBorderFeature.setStartSize(startSize);
        worldBorderFeature.setEndSize(endSize);
        worldBorderFeature.setTimeToShrink(shrinkTime);
        worldBorderFeature.setShrink(true);
        super.start();
    }

    @Override
    public PhaseType getPhaseType() {
        return PhaseType.BORDER_SHRINK;
    }
}