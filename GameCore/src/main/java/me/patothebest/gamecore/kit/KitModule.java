package me.patothebest.gamecore.kit;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.commands.kit.KitCommand;
import me.patothebest.gamecore.commands.user.UserKitCommand;
import me.patothebest.gamecore.injector.InstanceProvider;
import me.patothebest.gamecore.kit.entities.KitFlatFileEntity;
import me.patothebest.gamecore.kit.entities.KitMySQLEntity;
import me.patothebest.gamecore.storage.StorageModule;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;
import me.patothebest.gamecore.treasure.reward.Reward;
import me.patothebest.gamecore.treasure.reward.RewardProvider;
import me.patothebest.gamecore.treasure.reward.rewards.KitReward;

public class KitModule extends StorageModule {

    public KitModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        super.configure();
        install(new FactoryModuleBuilder().build(KitFactory.class));

        registerModule(UserKitCommand.class);
        registerModule(KitCommand.class);
        registerModule(KitCommand.Parent.class);

        MapBinder<String, InstanceProvider<Reward>> mapBinder = MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {}, new TypeLiteral<InstanceProvider<Reward>>() {});
        mapBinder.addBinding("kit").toInstance(new RewardProvider(KitReward.class));
    }

    @Override
    protected Class<? extends FlatFileEntity> getFlatFileEntity() {
        return KitFlatFileEntity.class;
    }

    @Override
    protected Class<? extends MySQLEntity> getSQLEntity() {
        return KitMySQLEntity.class;
    }
}
