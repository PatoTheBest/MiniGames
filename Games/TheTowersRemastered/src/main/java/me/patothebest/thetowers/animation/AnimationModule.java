package me.patothebest.thetowers.animation;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.thetowers.animation.animations.ForTeamAnimation;
import me.patothebest.thetowers.animation.animations.TeamInfoAnimation;
import me.patothebest.gamecore.animation.Animation;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.thetowers.TheTowersRemastered;

public class AnimationModule extends AbstractBukkitModule<TheTowersRemastered> {

    public AnimationModule(TheTowersRemastered plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<Animation> animations = Multibinder.newSetBinder(binder(), Animation.class);

        animations.addBinding().to(ForTeamAnimation.class);
        animations.addBinding().to(TeamInfoAnimation.class);
    }
}