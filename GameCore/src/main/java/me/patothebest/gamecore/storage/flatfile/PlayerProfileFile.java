package me.patothebest.gamecore.storage.flatfile;

import me.patothebest.gamecore.file.FlatFile;

import java.io.BufferedWriter;
import java.io.IOException;

public class PlayerProfileFile extends FlatFile {

    PlayerProfileFile(String playername) {
        super("players/" + playername);
        load();
    }

    @Override
    public void writeFile(BufferedWriter writer) throws IOException {
        // Don't write anything to the file
    }
}
