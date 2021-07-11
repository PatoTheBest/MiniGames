package me.patothebest.gamecore.quests;

import com.google.inject.Inject;
import me.patothebest.gamecore.file.ReadOnlyFile;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.IOException;

public class QuestFile extends ReadOnlyFile implements Module {

    private final Plugin plugin;

    @Inject private QuestFile(Plugin plugin) {
        super("quests");
        this.plugin = plugin;

        this.header = "Quests File";
        load();
    }

    @Override
    protected void writeFile(BufferedWriter writer) throws IOException {
        super.writeFile(writer);
        Utils.writeFileToWriter(writer, plugin.getResource("quests.yml"));
    }
}
