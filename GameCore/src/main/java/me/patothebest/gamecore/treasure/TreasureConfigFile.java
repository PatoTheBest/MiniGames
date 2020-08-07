package me.patothebest.gamecore.treasure;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.file.FlatFile;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.player.IPlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.io.BufferedWriter;
import java.io.IOException;

@Singleton
public class TreasureConfigFile extends FlatFile implements ActivableModule {

    private final CorePlugin plugin;

    // messages
    private String canOpenChests;
    private String mustBuyToOpen;
    private String notObtainable;

    @Inject private TreasureConfigFile(CorePlugin plugin) {
        super("treasure-chests-config");
        this.plugin = plugin;
        this.header = "Chests Config";
        load();
    }

    @Override
    public void onPreEnable() {
        for (TreasureType treasureType : TreasureType.values()) {
            ConfigurationSection treasureConfigSection = getConfigurationSection("types." + treasureType.getConfigPath());
            treasureType.setName(treasureConfigSection.getString("name"));
            treasureType.setCanBeBought(treasureConfigSection.getBoolean("can-be-bought"));
            treasureType.setDescription(treasureConfigSection.getStringList("description"));
            treasureType.setPrice(treasureConfigSection.getInt("price"));
            treasureType.setEnabled(treasureConfigSection.getBoolean("enabled", true));
        }

        canOpenChests = getString("messages.can-open-chest");
        mustBuyToOpen = getString("messages.must-buy-to-open");
        notObtainable = getString("messages.not-obtainable");
    }

    @Override
    public void writeFile(BufferedWriter writer) throws IOException {
        super.writeFile(writer);
        Utils.writeFileToWriter(writer, plugin.getResource("treasure-chests-config.yml"));
    }

    public String getMessage(IPlayer player, TreasureType treasureType) {
        if(player.getKeys(treasureType) > 0) {
            return canOpenChests;
        }

        return treasureType.canBeBought() ? mustBuyToOpen : notObtainable;
    }
}
