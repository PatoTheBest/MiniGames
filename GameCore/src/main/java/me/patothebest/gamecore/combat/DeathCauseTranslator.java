package me.patothebest.gamecore.combat;

import org.bukkit.event.entity.EntityDamageEvent;

public abstract class DeathCauseTranslator<T extends EntityDamageEvent> {

    private final Class<T> eventClass;

    public DeathCauseTranslator(Class<T> eventClass) {
        this.eventClass = eventClass;
    }

    public abstract DeathCause getDeathCause(T event);

    public abstract DamageOption[] getDamageOptions(DeathCause deathCause);

    public Class<T> getEventClass() {
        return eventClass;
    }
}
