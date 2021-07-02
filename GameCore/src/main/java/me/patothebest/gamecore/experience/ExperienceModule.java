package me.patothebest.gamecore.experience;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.experience.entities.ExperienceFlatFileEntity;
import me.patothebest.gamecore.experience.entities.ExperienceMySQLEntity;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;

public class ExperienceModule extends AbstractBukkitModule<CorePlugin> {

    public ExperienceModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<MySQLEntity> mySQLEntityMultibinder = Multibinder.newSetBinder(binder(), MySQLEntity.class);
        mySQLEntityMultibinder.addBinding().to(ExperienceMySQLEntity.class);

        Multibinder<FlatFileEntity> flatFileEntityMultibinder = Multibinder.newSetBinder(binder(), FlatFileEntity.class);
        flatFileEntityMultibinder.addBinding().to(ExperienceFlatFileEntity.class);

        Multibinder<Statistic> statisticMultibinder = Multibinder.newSetBinder(binder(), Statistic.class);
        statisticMultibinder.addBinding().to(ExperienceStatistic.class);

        registerModule(ExperienceManager.class);
        registerModule(ExperienceCommand.Parent.class);
    }
}
