package me.patothebest.arcade.arena;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.patothebest.arcade.game.GameType;
import me.patothebest.arcade.game.commands.GameCommand;
import me.patothebest.arcade.game.scoreboard.ArcadeScoreboardManager;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaFactory;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class ArenaModule extends AbstractBukkitModule<CorePlugin> {

    public ArenaModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(AbstractArena.class, Arena.class).build(ArenaFactory.class));
        registerModule(ArenaManager.class);

        registerModule(GameCommand.class);

        for (GameType value : GameType.values()) {
            registerModule(value.getCommandClass());
        }

        registerModule(ArcadeScoreboardManager.class);
    }
}
