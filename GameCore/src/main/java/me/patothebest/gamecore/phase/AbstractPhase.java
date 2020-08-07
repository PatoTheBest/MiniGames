package me.patothebest.gamecore.phase;

import me.patothebest.gamecore.feature.Feature;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import org.apache.commons.lang.Validate;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPhase<PluginType extends CorePlugin, Arena extends AbstractArena> implements Phase<Arena> {

    private final Map<Class<? extends Feature>, Feature> features = new HashMap<>();

    protected final PluginType plugin;

    protected Arena arena;
    private boolean canJoin;
    private Phase previousPhase;
    private Phase nextPhase;

    private boolean previousPhaseFeatures = false;

    public AbstractPhase(PluginType plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        Validate.notNull(arena, "Arena is null! Please set it first using setArena(Arena)");

        for (Feature feature : features.values()) {
            if(!feature.hasBeenRegistered()) {
                feature.initializeFeature();
                plugin.registerListener(feature);
            }
        }
    }

    @Override
    public void stop() {
        for (Feature feature : features.values()) {
            HandlerList.unregisterAll(feature);
            feature.stopFeature();
        }
    }

    @Override
    public <T extends Feature> T registerFeature(Class<T> featureClass) {
        T feature = arena.createFeature(featureClass);
        features.put(featureClass, feature);
        return feature;
    }

    @Override
    public void destroy() {
        for (Feature feature : features.values()) {
            HandlerList.unregisterAll(feature);
            feature.destroy();
        }
        features.clear();
        setArena(null);
    }

    public void registerFeature(Feature feature) {
        features.put(feature.getClass(), feature);
    }

    @Override
    public boolean canJoin() {
        return canJoin;
    }

    @Override
    public void setCanJoin(boolean canJoin) {
        this.canJoin = canJoin;
    }

    @Override
    public boolean isPreviousPhaseFeatures() {
        return previousPhaseFeatures;
    }

    @Override
    public void setPreviousPhaseFeatures(boolean previousPhaseFeatures) {
        this.previousPhaseFeatures = previousPhaseFeatures;
    }

    @Override
    public void removeFeature(Feature feature) {
        features.remove(feature.getClass());
    }

    @Override
    public Phase getNextPhase() {
        return nextPhase;
    }

    @Override
    public Phase getPreviousPhase() {
        return previousPhase;
    }

    @Override
    public void setPreviousPhase(Phase previousPhase) {
        this.previousPhase = previousPhase;
    }

    @Override
    public Map<Class<? extends Feature>, Feature> getFeatures() {
        return features;
    }

    @Override
    public void setNextPhase(Phase nextPhase) {
        this.nextPhase = nextPhase;
    }

    @Override
    public void setArena(Arena arena) {
        this.arena = arena;
    }
}
