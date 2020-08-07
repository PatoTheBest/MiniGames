package me.patothebest.skywars.phase;

import me.patothebest.skywars.phase.phases.BorderShrinkSkyWarsPhase;
import me.patothebest.skywars.phase.phases.DoomSkyWarsPhase;
import me.patothebest.skywars.phase.phases.EndSkyWarsPhase;
import me.patothebest.skywars.phase.phases.RefillSkyWarsPhase;

public enum PhaseType {

    NONE("null", null),
    REFILL("Refill", RefillSkyWarsPhase.class),
    DOOM("Doom", DoomSkyWarsPhase.class),
    BORDER_SHRINK("Border Shrink", BorderShrinkSkyWarsPhase.class),
    END("End", EndSkyWarsPhase.class),

    ;

    private final String configName;
    private final Class<? extends SkyWarsPhase> phaseClass;

    PhaseType(String configName, Class<? extends SkyWarsPhase> phaseClass) {
        this.configName = configName;
        this.phaseClass = phaseClass;
    }

    public static PhaseType getPhaseType(String name) {
        PhaseType phaseType = null;

        for (PhaseType phaseType2 : PhaseType.values()) {
            if(phaseType2.getConfigName().equalsIgnoreCase(name)) {
                phaseType = phaseType2;
            }
        }

        return phaseType;
    }

    /**
     * Gets the configName
     *
     * @return the configName
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * Gets the phase class
     *
     * @return the phase class
     */
    public Class<? extends SkyWarsPhase> getPhaseClass() {
        return phaseClass;
    }
}
