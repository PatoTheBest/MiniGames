package me.patothebest.gamecore.treasure.chest;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.file.FlatFile;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.treasure.type.TreasureType;
import org.bukkit.configuration.ConfigurationSection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TreasureChestStructureFile extends FlatFile implements ActivableModule, ReloadableModule {

    private final CorePlugin plugin;

    @Inject private TreasureChestStructureFile(CorePlugin plugin) {
        super("treasure-chest-structure");
        this.plugin = plugin;

        load();
    }

    @Override
    protected void writeFile(BufferedWriter writer) throws IOException {
        super.writeFile(writer);
        Utils.writeFileToWriter(writer, plugin.getResource("treasure-chest-structure.yml"));
    }

    @Override
    public void onEnable() {
        TreasureChestStructure.PIECES.clear();

        for (TreasureType treasureType : TreasureType.values()) {
            ConfigurationSection treasureConfigSection = getConfigurationSection(treasureType.name().toLowerCase());
            Map<TreasureChestStructurePiece, TreasureChestPiece> treasureChestStructurePieceTreasureChestPieceMap = new HashMap<>();

            for (TreasureChestStructurePiece treasureChestStructurePiece : TreasureChestStructurePiece.values()) {
                String material = treasureConfigSection.getString(treasureChestStructurePiece.getConfigPath());

                try {
                    if(material.contains(":")) {
                        String[] split = material.split(":");
                        treasureChestStructurePieceTreasureChestPieceMap.put(treasureChestStructurePiece, new TreasureChestPiece(Material.matchMaterial(split[0]), Byte.valueOf(split[1])));
                    } else {
                        treasureChestStructurePieceTreasureChestPieceMap.put(treasureChestStructurePiece, new TreasureChestPiece(Material.matchMaterial(material)));
                    }
                } catch (Exception e) {
                    Utils.printError("Could not parse item " + treasureConfigSection.getCurrentPath() + "." + treasureChestStructurePiece.getConfigPath(), e);
                }
            }

            TreasureChestStructure.PIECES.put(treasureType, treasureChestStructurePieceTreasureChestPieceMap);
        }
    }

    @Override
    public void onReload() {
        load();
        onEnable();
    }

    @Override
    public String getReloadName() {
        return "treasure-structure";
    }
}
