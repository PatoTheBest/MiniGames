package me.patothebest.skywars.phase;

import me.patothebest.skywars.lang.Lang;
import me.patothebest.skywars.phase.phases.BorderShrinkSkyWarsPhase;
import me.patothebest.skywars.phase.phases.DoomSkyWarsPhase;
import me.patothebest.skywars.phase.phases.EndSkyWarsPhase;
import me.patothebest.skywars.phase.phases.RefillSkyWarsPhase;

public enum PhaseType {

    NONE("null", null, Lang.PHASE_NONE),
    REFILL("Refill", RefillSkyWarsPhase.class, Lang.PHASE_REFILL),
    DOOM("Doom", DoomSkyWarsPhase.class, Lang.PHASE_DOOM),
    BORDER_SHRINK("Border Shrink", BorderShrinkSkyWarsPhase.class, Lang.PHASE_BORDER_SHRINK),
    END("End", EndSkyWarsPhase.class, Lang.PHASE_END),

    ;

    private final String configName;
    private final Class<? extends SkyWarsPhase> phaseClass;
    private final Lang message;

    PhaseType(String configName, Class<? extends SkyWarsPhase> phaseClass, Lang message) {
        this.configName = configName;
        this.phaseClass = phaseClass;
        this.message = message;
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

    /**
     * Gets the translatable message
     *
     * @return the translatable message
     */
    public Lang getMessage() {
        return message;
    }
}
