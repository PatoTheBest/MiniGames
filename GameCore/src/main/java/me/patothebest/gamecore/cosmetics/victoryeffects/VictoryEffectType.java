package me.patothebest.gamecore.cosmetics.victoryeffects;

import com.google.inject.Injector;
import me.patothebest.gamecore.cosmetics.victoryeffects.types.DayCycleVictoryEffect;
import me.patothebest.gamecore.cosmetics.victoryeffects.types.FireworkVictoryEffect;
import me.patothebest.gamecore.cosmetics.victoryeffects.types.LavaPopVictoryEffect;
import me.patothebest.gamecore.cosmetics.victoryeffects.types.RainDiscoVictoryEffect;
import me.patothebest.gamecore.cosmetics.victoryeffects.types.RainWoolVictoryEffect;

public enum VictoryEffectType {

    FIREWORK(FireworkVictoryEffect.class),
    LAVA_POP(LavaPopVictoryEffect.class),
    RAIN_DISCO(RainDiscoVictoryEffect.class),
    RAIN_WOOL(RainWoolVictoryEffect.class),
    DAY_CYCLE(DayCycleVictoryEffect.class);

    private final Class<? extends IVictoryEffect> iVictoryEffectClass;
    private IVictoryEffect iVictoryEffect;

    VictoryEffectType(Class<? extends IVictoryEffect> iVictoryEffectClass) {
        this.iVictoryEffectClass = iVictoryEffectClass;
    }

    public IVictoryEffect getVictoryEffect() {
        return iVictoryEffect;
    }

    public static void init(Injector injector) {
        for (VictoryEffectType value : values()) {
            value.iVictoryEffect = injector.getInstance(value.iVictoryEffectClass);
        }
    }
}
