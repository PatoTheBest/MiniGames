package me.patothebest.skywars.phase.phases;

import com.google.inject.Inject;
import me.patothebest.gamecore.feature.features.other.WorldBorderFeature;
import me.patothebest.skywars.SkyWars;
import me.patothebest.skywars.phase.AbstractSkyWarsPhase;
import me.patothebest.skywars.phase.PhaseType;
import org.bukkit.configuration.ConfigurationSection;

public class BorderShrinkSkyWarsPhase extends AbstractSkyWarsPhase {

    private int startSize;
    private int endSize;
    private int shrinkTime;

    @Inject private BorderShrinkSkyWarsPhase(SkyWars plugin) {
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