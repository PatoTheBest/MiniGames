package me.patothebest.gamecore.leaderboards.signs;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.patothebest.gamecore.event.other.LeaderboardUpdateEvent;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.file.ParserException;
import me.patothebest.gamecore.leaderboards.LeaderboardManager;
import me.patothebest.gamecore.logger.InjectParentLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.pluginhooks.PluginHookManager;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.stats.StatPeriod;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.StatsManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@ModuleName("Leader Signs Manager")
public class LeaderSignsManager implements ActivableModule, ReloadableModule, ListenerModule {

    private final LeaderboardManager leaderboardManager;
    private final CoreConfig coreConfig;
    private final StatsManager statsManager;
    private final PluginHookManager pluginHookManager;
    private final PluginScheduler pluginScheduler;
    private final Provider<Injector> injectorProvider;
    private final LeaderSignsFile leaderSignsFile;
    private final List<LeaderSign> signs = new ArrayList<>();
    private final Map<Player, Callback<Block>> blockInteractCallback = new HashMap<>();
    private final Map<Player, Callback<LeaderSign>> signInteractCallback = new HashMap<>();

    private String[] signTemplate;
    private String[] fallbackSignTemplate;

    @InjectParentLogger(parent = LeaderboardManager.class)
    private Logger logger;

    @Inject private LeaderSignsManager(LeaderboardManager leaderboardManager, CoreConfig coreConfig, StatsManager statsManager, PluginHookManager pluginHookManager, PluginScheduler pluginScheduler, Provider<Injector> injectorProvider, LeaderSignsFile leaderSignsFile) {
        this.leaderboardManager = leaderboardManager;
        this.coreConfig = coreConfig;
        this.statsManager = statsManager;
        this.pluginHookManager = pluginHookManager;
        this.pluginScheduler = pluginScheduler;
        this.injectorProvider = injectorProvider;
        this.leaderSignsFile = leaderSignsFile;
    }

    @Override
    public void onPreEnable() {
        signTemplate = coreConfig.getStringList("leaderboard-signs").toArray(new String[4]);
        fallbackSignTemplate = coreConfig.getStringList("leaderboard-signs-fallback").toArray(new String[4]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPostEnable() {
        if(!coreConfig.getBoolean("leaderboard.enabled")) {
            return;
        }

        AttachmentType.setup(pluginHookManager);

        List<Map<String, Object>> signsData = (List<Map<String, Object>>) leaderSignsFile.get("signs");

        if (signsData == null || signsData.isEmpty()) {
            return;
        }

        logger.info(ChatColor.YELLOW + "Loading leaderboard signs...");
        for (Map<String, Object> signData : signsData) {
            try {
                signs.add(new LeaderSign(this, statsManager, signData));
            } catch (ParserException e) {
                Utils.printError("Could not parse leaderboard sign", e.getMessage());
            } catch (Throwable t) {
                logger.log(Level.SEVERE, ChatColor.RED + "Could not load sign", t);
            }
        }
        logger.info("Loaded " + signs.size() + " sign(s)");
    }

    @Override
    public void onReload() {
        onDisable();
        onPreEnable();
        onPostEnable();
    }

    @Override
    public void onDisable() {
        signs.clear();
    }

    @Override
    public String getReloadName() {
        return "leadersigns";
    }

    public void saveData() {
        List<Map<String, Object>> signData = new ArrayList<>();
        for (LeaderSign sign : signs) {
            signData.add(sign.serialize());
        }
        leaderSignsFile.set("signs", signData);
        leaderSignsFile.save();
    }

    @EventHandler
    public void onLeaderboardUpdate(LeaderboardUpdateEvent event) {
        updateData();
    }

    public void updateData() {
        pluginScheduler.ensureSync(() -> {
            logger.fine("Updating leader signs");
            for (LeaderSign sign : signs) {
                try {
                    sign.update();
                } catch (Throwable t) {
                    logger.log(Level.SEVERE, "Could not update sign " + sign.toString(), t);
                }
            }
        });
    }

    public LeaderSign createSign(Location location, Statistic statistic, StatPeriod period, int place) {
        return new LeaderSign(this, location, statistic, period, place);
    }

    public String[] getSignTemplate() {
        return signTemplate;
    }

    public String[] getFallbackSignTemplate() {
        return fallbackSignTemplate;
    }

    public Attachment createAttachment(AttachmentType attachmentType) {
        return attachmentType.createNew(injectorProvider.get());
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public List<LeaderSign> getSigns() {
        return signs;
    }

    public Map<Player, Callback<Block>> getBlockInteractCallback() {
        return blockInteractCallback;
    }

    public Map<Player, Callback<LeaderSign>> getSignInteractCallback() {
        return signInteractCallback;
    }
}
