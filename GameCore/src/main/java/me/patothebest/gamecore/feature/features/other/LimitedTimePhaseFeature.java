package me.patothebest.gamecore.feature.features.other;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;

import javax.inject.Inject;

public class LimitedTimePhaseFeature extends AbstractRunnableFeature {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final CorePlugin plugin;
    private final CoreConfig coreConfig;

    private int timeUntilNextStage = 120;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject public LimitedTimePhaseFeature(CorePlugin plugin, CoreConfig coreConfig) {
        this.plugin = plugin;
        this.coreConfig = coreConfig;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void run() {
        cancel();
        arena.nextPhase();
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    public int getTimeUntilNextStage() {
        return timeUntilNextStage;
    }

    /**
     * Set the time to the next stage
     *
     * @param timeUntilNextStage time in seconds
     */
    public void setTimeUntilNextStage(int timeUntilNextStage) {
        this.timeUntilNextStage = timeUntilNextStage;
    }

    @Override
    public String toString() {
        return "Countdown{" +
                "arena=" + arena.getName() +
                ", timeUntilNextStage=" + timeUntilNextStage +
                '}';
    }

    @Override
    public void initializeFeature() {
        runTaskLater(plugin, timeUntilNextStage*20L);
    }

    public int getRemainingTime() {
        return (int) (timeUntilNextStage - (System.currentTimeMillis()/1000L - arena.getPhaseTime()/1000L));
    }
}
