package me.patothebest.gamecore.file;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class VersionFile extends FlatFile {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    protected final JavaPlugin plugin;
    protected String versionFileName;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public VersionFile(JavaPlugin plugin, String fileName) {
        super(fileName);
        this.plugin = plugin;
        this.versionFileName = this.fileName;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void load() {
        super.load();

        try {
            // read jar config
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.loadFromString(fileToYAML(plugin.getResource(versionFileName)));

            // compare config versions
            if(!isSet("file-version") || get("file-version") != yamlConfiguration.get("file-version")) {
                Map<String, Object> toOverride = new HashMap<>();

                // get this config's values
                getValues(true).forEach((key, object) -> {
                    // filter out all the comments as we only want values
                    if(key.contains("GAMECORE_COMMENT") || key.contains("GAMECORE_BLANKLINE") || key.contains("file-version")) {
                        return;
                    }

                    // store in cache map
                    toOverride.put(key, object);
                });

                // delete the config
                delete();

                // clear the map from MemorySection, this is
                // to avoid clashes
                map.clear();

                // generate new file and load it
                super.load();

                // set all the values from the cache to the new config
                toOverride.forEach(this::set);

                // save the updated config
                save();
            }
        } catch (Exception e) {
            System.err.println("Could not load file from jar");
            e.printStackTrace();
        }
    }
}
