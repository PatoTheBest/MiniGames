package me.patothebest.gamecore.leaderboards.signs;

import com.google.inject.Inject;
import me.patothebest.gamecore.file.FlatFile;
import me.patothebest.gamecore.modules.Module;

public class LeaderSignsFile extends FlatFile implements Module {

    @Inject private LeaderSignsFile() {
        super("leader-signs");
        this.header = "Leaderboard Signs";

        load();
    }
}
