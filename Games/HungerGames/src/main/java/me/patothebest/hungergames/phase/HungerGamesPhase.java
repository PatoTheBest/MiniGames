package me.patothebest.hungergames.phase;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.phase.Phase;
import org.bukkit.configuration.ConfigurationSection;

public interface HungerGamesPhase<T extends AbstractArena> extends Phase<T> {

    PhaseType getPhaseType();

    void setTimeTilNextPhase(int time);

    default void parseExtraData(ConfigurationSection phasesConfigurationSection) {}

}
