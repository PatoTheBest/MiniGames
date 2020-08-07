package me.patothebest.gamecore.scoreboard;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.file.VersionFile;
import me.patothebest.gamecore.modules.ActivableModule;

import java.io.BufferedWriter;
import java.io.IOException;

@Singleton
@ModuleName("Scoreboard File")
public class ScoreboardFile extends VersionFile implements ActivableModule {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final CorePlugin plugin;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private ScoreboardFile(CorePlugin plugin) {
        super(plugin, "scoreboards");
        this.header = "Scoreboards";
        this.plugin = plugin;
        load();
    }

    // -------------------------------------------- //
    // CACHE
    // -------------------------------------------- //

    @Override
    public void onPreEnable() {
        for (ScoreboardType scoreboardType : CoreScoreboardType.values()) {
            if(scoreboardType == CoreScoreboardType.NONE) {
                continue;
            }

            scoreboardType.setEnabled(getBoolean(scoreboardType.getConfigName() + ".enabled"));
        }
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    protected void writeFile(BufferedWriter writer) throws IOException {
        super.writeFile(writer);
        Utils.writeFileToWriter(writer, plugin.getResource("scoreboards.yml"));
    }
}
