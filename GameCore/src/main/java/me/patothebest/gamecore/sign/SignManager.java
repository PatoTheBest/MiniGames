package me.patothebest.gamecore.sign;

import com.google.inject.Provider;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadPriority;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.placeholder.PlaceHolderManager;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.util.Priority;
import me.patothebest.gamecore.arena.ArenaManager;
import org.bukkit.ChatColor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
@ReloadPriority(priority = Priority.HIGHEST)
@ModuleName("Sign Manager")
public class SignManager implements ListenerModule, ActivableModule, ReloadableModule {

    private final CorePlugin corePlugin;
    private final SignFile signFile;
    private final Provider<NMS> nms;
    private final PluginScheduler pluginScheduler;
    private final CoreConfig coreConfig;
    private final PlaceHolderManager placeHolderManager;
    private final ArenaManager arenaManager;
    private final List<ArenaSign> signs;
    private final Map<String, String> newSigns;
    @InjectLogger private Logger logger;

    @Inject private SignManager(CorePlugin corePlugin, SignFile signFile, Provider<NMS> nms, PluginScheduler pluginScheduler, CoreConfig coreConfig, PlaceHolderManager placeHolderManager, ArenaManager arenaManager) {
        this.corePlugin = corePlugin;
        this.signFile = signFile;
        this.nms = nms;
        this.pluginScheduler = pluginScheduler;
        this.coreConfig = coreConfig;
        this.placeHolderManager = placeHolderManager;
        this.arenaManager = arenaManager;
        this.signs = new ArrayList<>();
        this.newSigns = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPostEnable() {
        logger.info(ChatColor.YELLOW + "Loading signs...");

        if(signFile.get("signs") == null) {
            return;
        }

        try {
            // load the signs as a List<Map<String, Object>>
            // then for each Map<String, Object> convert it into
            // a sign by invoking the constructor
            // (TheTowersRemastered, Map<String, Object>)
            signs.addAll(((List<Map<String, Object>>) signFile.get("signs")).stream().map(stringObjectMap -> new ArenaSign(pluginScheduler, coreConfig, placeHolderManager, this, arenaManager, nms, stringObjectMap)).collect(Collectors.toList()));
            logger.info("Loaded " + signs.size() + " sign(s)");
        } catch(Throwable t) {
            logger.info(ChatColor.RED + "Could not load signs");
            t.printStackTrace();
        }

        updateSigns();
    }

    public void saveSigns() {
        signFile.set("signs", signs.stream().map(ArenaSign::serialize).collect(Collectors.toList()));
        signFile.save();
    }

    @Override
    public void onDisable() {
        signs.clear();
    }

    @Override
    public void onReload() {
        onDisable();
        signFile.load();
        onPostEnable();
    }

    @Override
    public String getReloadName() {
        return "signs";
    }

    public void updateSigns() {
        pluginScheduler.ensureSync(() -> {
            for (ArenaSign sign : signs) {
                try {
                    sign.update();
                } catch (Throwable t) {
                    System.err.println("Could not update sign " + sign.getLocation().toString());
                    System.err.println("Arena: " + sign.getCurrentArena());
                    t.printStackTrace();
                }
            }
        });
    }

    public List<ArenaSign> getSigns() {
        return signs;
    }

    public Map<String, String> getNewSigns() {
        return newSigns;
    }
}
