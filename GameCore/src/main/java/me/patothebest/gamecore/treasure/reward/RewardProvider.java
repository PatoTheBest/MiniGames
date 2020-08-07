package me.patothebest.gamecore.treasure.reward;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import me.patothebest.gamecore.injector.InstanceProvider;

public class RewardProvider implements InstanceProvider<Reward> {

    private final Class<? extends Reward> rewardClass;
    @Inject private Provider<Injector> injectorProvider;

    public RewardProvider(Class<? extends Reward> rewardClass) {
        this.rewardClass = rewardClass;
    }

    /**
     * Provides an instance of {@link Reward}. Must never return {@code null}.
     */
    @Override
    public Reward newInstance() {
        return injectorProvider.get().getInstance(rewardClass);
    }
}
