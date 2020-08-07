package me.patothebest.gamecore.leaderboards.holograms;

import com.google.inject.Inject;
import me.patothebest.gamecore.file.FlatFile;
import me.patothebest.gamecore.modules.Module;

public class HolographicFile extends FlatFile implements Module {

    @Inject private HolographicFile() {
        super("leader-holograms");
        this.header = "Leaderboard Holograms";

        load();
    }
}
