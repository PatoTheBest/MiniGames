package me.patothebest.gamecore.treasure;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.file.FlatFile;
import me.patothebest.gamecore.modules.Module;

@Singleton
public class TreasureFile extends FlatFile implements Module {

    @Inject private TreasureFile() {
        super("treasure-chests");
        this.header = "Chests";
        load();
    }
}
