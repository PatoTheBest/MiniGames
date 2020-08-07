package me.patothebest.gamecore.treasure.reward;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.file.FlatFile;
import me.patothebest.gamecore.injector.InstanceProvider;
import me.patothebest.gamecore.logger.InjectParentLogger;
import me.patothebest.gamecore.logger.Logger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModulePriority;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.treasure.TreasureManager;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.util.Priority;
import org.bukkit.configuration.ConfigurationSection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@ModulePriority(priority =  Priority.HIGHEST)
public class RewardFile extends FlatFile implements ActivableModule, ReloadableModule {

    private final CorePlugin plugin;
    private final Map<TreasureType, List<Reward>> rewards = new HashMap<>();
    private final Map<String, InstanceProvider<Reward>> rewardProviders;
    @InjectParentLogger(parent = TreasureManager.class) private Logger logger;

    @Inject private RewardFile(CorePlugin plugin, Map<String, InstanceProvider<Reward>> rewardProviders) {
        super("rewards");
        this.rewardProviders = rewardProviders;
        this.header = "Rewards";
        this.plugin = plugin;

        load();
    }

    @Override
    public void onReload() {
        load();
        onPostEnable();
    }

    @Override
    public String getReloadName() {
        return "rewards";
    }

    @Override
    public void onPostEnable() {
        rewards.clear();

        for (TreasureType treasureType : TreasureType.values()) {
            ConfigurationSection treasureConfigSection = getConfigurationSection(treasureType.name().toLowerCase());
            rewards.put(treasureType, new ArrayList<>());

            if(treasureConfigSection == null) {
                continue;
            }

            for (String rewardName : treasureConfigSection.getKeys(false)) {
                ConfigurationSection rewardConfigSection = treasureConfigSection.getConfigurationSection(rewardName);
                InstanceProvider<Reward> rewardProvider = rewardProviders.get(rewardConfigSection.getString("type"));

                if(rewardProvider == null) {
                    System.err.println("Reward " + rewardName + " has unknown reward type!");
                    continue;
                }

                Reward reward = rewardProvider.newInstance();

                if(!reward.parse(rewardConfigSection)) {
                    logger.severe("Could not parse reward {0}!", rewardName);
                    continue;
                }

                List<Reward> rewardList = rewards.get(treasureType);
                rewardList.add(reward);
            }
        }
    }

    /**
     * Gets rewards
     *
     * @return value of rewards
     */
    public Map<TreasureType, List<Reward>> getRewards() {
        return rewards;
    }

    @Override
    protected void writeFile(BufferedWriter writer) throws IOException {
        super.writeFile(writer);
        Utils.writeFileToWriter(writer, plugin.getResource("rewards.yml"));
    }
}
