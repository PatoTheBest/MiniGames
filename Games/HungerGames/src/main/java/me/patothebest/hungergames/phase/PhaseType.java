package me.patothebest.hungergames.phase;

import me.patothebest.hungergames.phase.phases.EndHGPhase;
import me.patothebest.hungergames.phase.phases.RefillPhase;
import me.patothebest.hungergames.phase.phases.SupplyDropPhase;
import me.patothebest.hungergames.phase.phases.BorderShrinkPhase;
import me.patothebest.hungergames.phase.phases.DeathmatchPhase;

public enum PhaseType {

    NONE("null", null),
    REFILL("Refill", RefillPhase.class),
    BORDER_SHRINK("Border Shrink", BorderShrinkPhase.class),
    SUPPLY_DROP("Supply Drop", SupplyDropPhase.class),
    DEATHMATCH("Deathmatch", DeathmatchPhase.class),
    END("End", EndHGPhase.class),

    ;

    private final String configName;
    private final Class<? extends HungerGamesPhase> phaseClass;

    PhaseType(String configName, Class<? extends HungerGamesPhase> phaseClass) {
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
    public Class<? extends HungerGamesPhase> getPhaseClass() {
        return phaseClass;
    }
}
