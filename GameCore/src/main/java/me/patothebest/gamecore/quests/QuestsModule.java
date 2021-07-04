package me.patothebest.gamecore.quests;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.quests.entities.QuestsFlatFileEntity;
import me.patothebest.gamecore.quests.entities.QuestsSQLEntity;
import me.patothebest.gamecore.quests.types.KillPlayersQuestType;
import me.patothebest.gamecore.quests.types.WinGameQuestType;
import me.patothebest.gamecore.quests.ui.QuestGUIFactory;
import me.patothebest.gamecore.storage.StorageModule;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;

public class QuestsModule extends StorageModule {

    public QuestsModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        super.configure();

        registerModule(QuestManager.class);
        registerModule(QuestsCommand.class);

        Multibinder<QuestType> questTypeMultibinder = Multibinder.newSetBinder(binder(), QuestType.class);
        questTypeMultibinder.addBinding().to(KillPlayersQuestType.class);
        questTypeMultibinder.addBinding().to(WinGameQuestType.class);

        install(new FactoryModuleBuilder().build(QuestGUIFactory.class));
    }

    @Override
    protected Class<? extends FlatFileEntity> getFlatFileEntity() {
        return QuestsFlatFileEntity.class;
    }

    @Override
    protected Class<? extends MySQLEntity> getSQLEntity() {
        return QuestsSQLEntity.class;
    }
}
