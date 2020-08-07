package me.patothebest.thetowers.file;

import com.google.inject.Singleton;
import me.patothebest.gamecore.file.PluginHookFile;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.scoreboard.ScoreboardFile;
import me.patothebest.gamecore.sign.SignFile;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.thetowers.TheTowersRemastered;

import javax.inject.Inject;

@Singleton
@ModuleName("File Manager")
public class FileManager implements ActivableModule {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final TheTowersRemastered plugin;

    // All files are not final so they can be
    // re-defined on a command reload
    private Config config;
    private SignFile signFile;
    private PluginHookFile pluginHookFile;
    private PermissionGroupsFile permissionGroupsFile;
    private ScoreboardFile scoreboardFile;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private FileManager(TheTowersRemastered plugin) {
        // If there is no plugin directory...
        if(!Utils.PLUGIN_DIR.exists()) {
            // ...create it
            Utils.PLUGIN_DIR.mkdirs();
        }

        this.plugin = plugin;

        // load the files
        loadFiles();
    }

    // -------------------------------------------- //
    // CLASS METHODS
    // -------------------------------------------- //

    private void loadFiles() {
        this.config = plugin.getInjector().getInstance(Config.class);
        this.signFile = plugin.getInjector().getInstance(SignFile.class);
        this.pluginHookFile = plugin.getInjector().getInstance(PluginHookFile.class);
        this.permissionGroupsFile = new PermissionGroupsFile(plugin);
        this.scoreboardFile = plugin.getInjector().getInstance(ScoreboardFile.class);
    }

    @Override
    public void onPreEnable() {
        // pre load is called before loading arenas and kits
        this.permissionGroupsFile.load();
        this.permissionGroupsFile.loadGroups();
        this.config.load();
        this.config.readConfig();
        this.scoreboardFile.load();
        this.scoreboardFile.onPreEnable();
    }

    @Override
    public void onDisable() {
        // only save the permission groups no need
        // to save other files
        permissionGroupsFile.save();
    }

    // -------------------------------------------- //
    // GETTERS
    // -------------------------------------------- //

    public Config getConfig() {
        return config;
    }

    public SignFile getSignFile() {
        return signFile;
    }

    public PluginHookFile getPluginHookFile() {
        return pluginHookFile;
    }

    public PermissionGroupsFile getPermissionGroupsFile() {
        return permissionGroupsFile;
    }

    public ScoreboardFile getScoreboardFile() {
        return scoreboardFile;
    }
}
