package me.patothebest.gamecore.experience;

import com.google.inject.Inject;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.logger.Logger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.stats.StatsUpdateEvent;
import me.patothebest.gamecore.storage.StorageManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

@ModuleName("Experience Manager")
public class ExperienceManager implements ActivableModule, ListenerModule, ReloadableModule {

    private final CoreConfig config;
    @InjectLogger private Logger logger;

    private final Map<String, Integer> experienceStats = new HashMap<>();
    private ExperienceCalculator experienceCalculator = new ExperienceCalculator(1, 1);

    private String symbol;
    private String[] colors;
    private int[] indices;

    @Inject
    private ExperienceManager(StorageManager storageManager, CoreConfig config) {
        this.config = config;
    }

    @Override
    public void onPostEnable() {
        experienceStats.clear();
        if (!config.getBoolean("game-experience.enabled")) {
            logger.config("Not enabling experience-manager because it's disabled in config.");
            return;
        }

        ConfigurationSection statsSection = config.getConfigurationSection("game-experience.experience-stats");
        if (statsSection == null) {
            logger.severe("Game experience is enabled but experience-stats section is not found in config!");
            return;
        }

        experienceStats.clear();
        Map<String, Object> values = statsSection.getValues(false);
        values.forEach((statName, experienceObj) -> {
            experienceStats.put(statName, (Integer) experienceObj);
        });

        int base = config.getInt("game-experience.experience-base");
        int factor = config.getInt("game-experience.experience-factor");
        logger.config("Using {0} as base", base);
        logger.config("Using {0} as factor", factor);
        experienceCalculator = new ExperienceCalculator(base, factor);

        ConfigurationSection displaySection = config.getConfigurationSection("game-experience.display-colors");
        symbol = config.getString("game-experience.display-symbol");
        if (displaySection == null) {
            logger.severe("Game experience is enabled but display section is not found in config!");
            return;
        }

        Map<String, Object> formatValues = displaySection.getValues(false);
        colors = new String[formatValues.size()];
        indices = new int[formatValues.size()];
        int i = 0;
        for (Map.Entry<String, Object> stringObjectEntry : formatValues.entrySet()) {
            int minLevel = Integer.parseInt(stringObjectEntry.getKey());
            indices[i] = minLevel;
            colors[i] = ChatColor.translateAlternateColorCodes('&', (String) stringObjectEntry.getValue());
            i++;
        }
    }

    @EventHandler
    public void onStatsUpdate(StatsUpdateEvent event) {
        if (experienceStats.isEmpty()) {
            return;
        }

        int expToAdd = experienceStats.getOrDefault(event.getStatistic().getStatName(), -1);
        if (expToAdd == -1) {
            return;
        }

        event.getPlayer().addExperience(expToAdd);
    }

    public String getColor(int level) {
        int low = 0;
        int high = indices.length - 1;

        int mid = -1;
        int midVal = -1;
        while (low <= high) {
            mid = (low + high) >>> 1;
            midVal = indices[mid];
            if (midVal < level) {
                low = mid + 1;
            } else if (midVal > level) {
                high = mid - 1;
            } else {
                break;
            }
        }

        if (midVal > level) {
            mid--;
        }

        return colors[mid];
    }

    public String getSymbol() {
        return symbol;
    }

    public ExperienceCalculator getExperienceCalculator() {
        return experienceCalculator;
    }

    @Override
    public void onReload() {
        onPostEnable();
    }

    @Override
    public String getReloadName() {
        return "experience-manager";
    }
}
