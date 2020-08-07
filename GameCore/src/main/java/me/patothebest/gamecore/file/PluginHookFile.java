package me.patothebest.gamecore.file;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.util.Utils;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.IOException;

public class PluginHookFile extends VersionFile implements Module {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject public PluginHookFile(CorePlugin plugin) {
        super(plugin, "plugin-hooks");
        this.header = "Plugin Hooks";
        load();
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    protected void writeFile(BufferedWriter writer) throws IOException {
        super.writeFile(writer);
        Utils.writeFileToWriter(writer, plugin.getResource("plugin-hooks.yml"));
    }
}
