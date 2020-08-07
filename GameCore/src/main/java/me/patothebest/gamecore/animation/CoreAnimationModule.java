package me.patothebest.gamecore.animation;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.animation.animations.BlinkAnimation;
import me.patothebest.gamecore.animation.animations.DecisionAnimation;
import me.patothebest.gamecore.animation.animations.DelayAnimation;
import me.patothebest.gamecore.animation.animations.GlowAnimation;
import me.patothebest.gamecore.animation.animations.RainbowAnimation;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class CoreAnimationModule extends AbstractBukkitModule<CorePlugin> {

    public CoreAnimationModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<Animation> animations = Multibinder.newSetBinder(binder(), Animation.class);

        animations.addBinding().to(BlinkAnimation.class);
        animations.addBinding().to(DecisionAnimation.class);
        animations.addBinding().to(DelayAnimation.class);
        animations.addBinding().to(GlowAnimation.class);
        animations.addBinding().to(RainbowAnimation.class);

        registerModule(AnimationManager.class);
    }
}