package me.patothebest.gamecore.leaderboards;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.leaderboards.signs.AttachmentCommand;
import me.patothebest.gamecore.leaderboards.holograms.HolographicCommand;
import me.patothebest.gamecore.leaderboards.holograms.HolographicFactory;
import me.patothebest.gamecore.leaderboards.holograms.LeaderHologramManager;
import me.patothebest.gamecore.leaderboards.signs.LeaderSignListener;
import me.patothebest.gamecore.leaderboards.signs.LeaderSignsCommand;
import me.patothebest.gamecore.leaderboards.signs.LeaderSignsManager;

public class LeaderboardModule extends AbstractBukkitModule<CorePlugin> {

    public LeaderboardModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        registerModule(LeaderboardManager.class);
        registerModule(LeaderboardCommand.class);

        registerModule(LeaderSignsManager.class);
        registerModule(LeaderSignListener.class);
        registerModule(LeaderSignsCommand.Parent.class);
        registerModule(LeaderSignsCommand.class);
        registerModule(AttachmentCommand.Parent.class);

        registerModule(HolographicCommand.Parent.class);
        registerModule(LeaderHologramManager.class);
        install(new FactoryModuleBuilder().build(HolographicFactory.class));
    }
}
