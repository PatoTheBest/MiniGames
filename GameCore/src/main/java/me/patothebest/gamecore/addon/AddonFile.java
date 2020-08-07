package me.patothebest.gamecore.addon;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.file.VersionFile;
import me.patothebest.gamecore.util.Utils;

import java.io.BufferedWriter;
import java.io.IOException;

public class AddonFile extends VersionFile {

    @Inject private AddonFile(CorePlugin plugin) {
        super(plugin, "addons");
        load();
    }

    @Override
    protected void writeFile(BufferedWriter writer) throws IOException {
        super.writeFile(writer);
        Utils.writeFileToWriter(writer, plugin.getResource("addons.yml"));
    }
}
