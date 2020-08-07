package me.patothebest.gamecore.file;

import me.patothebest.gamecore.lang.CoreLocaleManager;
import me.patothebest.gamecore.lang.Locale;
import me.patothebest.gamecore.modules.ReloadPriority;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.util.Priority;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.IOException;

@ReloadPriority(priority = Priority.LOW)
public abstract class CoreConfig extends VersionFile implements ReloadableModule {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private Locale defaultLocale;
    private int countdownTime;
    private Location mainLobby;
    private int levelsToGive;
    private boolean useMainLobby;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public CoreConfig(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
        this.versionFileName = "core-config.yml";
    }

    // -------------------------------------------- //
    // METHODS
    // -------------------------------------------- //

    public void readConfig() {
        // Countdown time until arena starts
        countdownTime = getInt("countdown");

        // Default locale
        String defaultLocaleName = getString("default-locale","en");
        if (defaultLocaleName.equalsIgnoreCase("es")) {
            defaultLocaleName = "es_ES";
        }
        defaultLocale = CoreLocaleManager.getLocale(defaultLocaleName);

        // Levels to give to each player after they kill someone
        levelsToGive = getInt("levels-on-kill");

        // MainLobby
        // If there is no main lobby, the default world
        // spawn will be used
        if(isSet("MainLobby")) {
            mainLobby = Location.deserialize(getConfigurationSection("MainLobby").getValues(true));
        }

        if(isSet("teleport.location")) {
            useMainLobby = getString("teleport.location").equalsIgnoreCase("MainLobby");
        }
    }

    @Override
    public void writeFile(BufferedWriter writer) throws IOException {
        super.writeFile(writer);
        Utils.writeFileToWriter(writer, plugin.getResource("core-config.yml"));
        Utils.writeFileToWriter(writer, plugin.getResource("config.yml"));
    }

    @Override
    public void onReload() {
        load();
        readConfig();
    }

    @Override
    public String getReloadName() {
        return "config";
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    public int getCountdownTime() {
        return countdownTime;
    }

    public Location getMainLobby() {
        return mainLobby;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setMainLobby(Location mainLobby) {
        this.mainLobby = mainLobby;
    }

    public int getLevelsToGive() {
        return levelsToGive;
    }

    public boolean isUseMainLobby() {
        return useMainLobby;
    }

    public void setUseMainLobby(boolean useMainLobby) {
        this.useMainLobby = useMainLobby;
    }
}
