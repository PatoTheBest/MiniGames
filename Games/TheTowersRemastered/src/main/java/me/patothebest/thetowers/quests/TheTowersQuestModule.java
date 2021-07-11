package me.patothebest.thetowers.quests;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.quests.QuestType;
import me.patothebest.thetowers.TheTowersRemastered;

public class TheTowersQuestModule extends AbstractBukkitModule<TheTowersRemastered> {

    public TheTowersQuestModule(TheTowersRemastered plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<QuestType> questTypeMultibinder = Multibinder.newSetBinder(binder(), QuestType.class);
        questTypeMultibinder.addBinding().to(ScoredPointQuestType.class);
    }
}
