package me.patothebest.gamecore.treasure;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.treasure.chest.TreasureChestStructureFile;
import me.patothebest.gamecore.treasure.entities.TreasureFlatFileEntity;
import me.patothebest.gamecore.treasure.entities.TreasureMySQLEntity;
import me.patothebest.gamecore.treasure.reward.Reward;
import me.patothebest.gamecore.treasure.reward.RewardFile;
import me.patothebest.gamecore.treasure.reward.RewardProvider;
import me.patothebest.gamecore.treasure.reward.rewards.CommandReward;
import me.patothebest.gamecore.treasure.reward.rewards.ItemReward;
import me.patothebest.gamecore.treasure.reward.rewards.MoneyReward;
import me.patothebest.gamecore.treasure.reward.rewards.PermissionReward;
import me.patothebest.gamecore.treasure.reward.rewards.ShopReward;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.injector.InstanceProvider;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;

public class TreasureModule extends AbstractBukkitModule<CorePlugin> {

    public TreasureModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(TreasureFactory.class));

        registerModule(TreasureFile.class);
        registerModule(TreasureCommand.Parent.class);
        registerModule(TreasureManager.class);
        registerModule(RewardFile.class);
        registerModule(TreasureChestStructureFile.class);
        registerModule(TreasureConfigFile.class);

        MapBinder<String, InstanceProvider<Reward>> mapBinder = MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {}, new TypeLiteral<InstanceProvider<Reward>>() {});
        mapBinder.addBinding("command").toInstance(new RewardProvider(CommandReward.class));
        mapBinder.addBinding("item").toInstance(new RewardProvider(ItemReward.class));
        mapBinder.addBinding("money").toInstance(new RewardProvider(MoneyReward.class));
        mapBinder.addBinding("permission").toInstance(new RewardProvider(PermissionReward.class));
        mapBinder.addBinding("shop-item").toInstance(new RewardProvider(ShopReward.class));

        Multibinder<MySQLEntity> mySQLEntityMultibinder = Multibinder.newSetBinder(binder(), MySQLEntity.class);
        mySQLEntityMultibinder.addBinding().to(TreasureMySQLEntity.class);

        Multibinder<FlatFileEntity> flatFileEntityMultibinder = Multibinder.newSetBinder(binder(), FlatFileEntity.class);
        flatFileEntityMultibinder.addBinding().to(TreasureFlatFileEntity.class);
    }
}
