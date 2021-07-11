package me.patothebest.gamecore.quests;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.file.ParserException;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.logger.Logger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadableModule;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Singleton
@ModuleName("Quest Manager")
public class QuestManager implements ActivableModule, ReloadableModule {

    private final Map<String, Quest> questMap = new HashMap<>();
    private final QuestFile questFile;
    private final Set<QuestType> questTypes;
    private final EventRegistry eventRegistry;
    @InjectLogger private Logger logger;

    @Inject private QuestManager(QuestFile questFile, Set<QuestType> questTypes, EventRegistry eventRegistry) {
        this.questFile = questFile;
        this.questTypes = questTypes;
        this.eventRegistry = eventRegistry;
    }

    @Override
    public void onPostEnable() {
        questMap.clear();
        ConfigurationSection questSection = questFile.getConfigurationSection("quests");

        if (questSection == null) {
            logger.severe("Quest configurations section is null!");
            return;
        }

        Set<String> questKeys = questSection.getKeys(false);
        for (String questConfigName : questKeys) {
            try {
                Map<String, Object> data = Objects.requireNonNull(questSection.getConfigurationSection(questConfigName)).getValues(true);
                Quest quest = new Quest(this, questConfigName, data);
                this.questMap.put(questConfigName, quest);
                logger.config("Loaded quest {0} ({1})", quest.getDisplayName(), questConfigName);
            } catch (ParserException e) {
                logger.severe("Could not parse quest " + questConfigName);
                logger.severe(e.getMessage());
            }
        }

        for (QuestType questType : questTypes) {
            logger.config("Registering quest type {0}", questType.getName());
            eventRegistry.registerListener(questType);
        }
    }

    @Override
    public void onDisable() {
        for (QuestType questType : questTypes) {
            eventRegistry.unRegisterListener(questType);
        }
    }

    @Override
    public void onReload() {
        onDisable();
        onPostEnable();
    }

    @Override
    public String getReloadName() {
        return "quest-manager";
    }

    public @Nullable Quest getQuest(String name) {
        return questMap.get(name);
    }

    public @Nullable QuestType getQuestType(String name) {
        for (QuestType questType : questTypes) {
            if (questType.getName().equalsIgnoreCase(name)) {
                return questType;
            }
        }

        return null;
    }

    public Map<String, Quest> getQuestMap() {
        return questMap;
    }
}
