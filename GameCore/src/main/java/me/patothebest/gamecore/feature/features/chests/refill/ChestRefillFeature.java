package me.patothebest.gamecore.feature.features.chests.refill;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.arena.types.ChestArena;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.vector.ArenaLocation;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChestRefillFeature extends AbstractFeature implements Module {

    private final static Map<ChestLocation, Map<ChestType, ChestFile>> CHEST_FILES = new HashMap<>();

    private final Map<ChestLocation, List<ArenaLocation>> arenaChestLocations = new HashMap<>();
    private final ChestType chestType = ChestType.NORMAL;
    private final Set<ChestLocation> chestLocations;
    private int minAmount = -1;
    private int maxAmount = -1;

    @Inject private ChestRefillFeature(CorePlugin plugin, CoreConfig coreConfig, Set<ChestLocation> chestLocations) {
        this.chestLocations = chestLocations;
        if (coreConfig.isSet("chests")) {
            minAmount = coreConfig.getInt("chests.min-items-amount", -1);
            maxAmount = coreConfig.getInt("chests.max-items-amount", -1);
        }

        if (CHEST_FILES.isEmpty()) {
            for (ChestLocation chestLocation : chestLocations) {
                Map<ChestType, ChestFile> chestMap = new HashMap<>();
                for (ChestType chestType : ChestType.values()) {
                    chestMap.put(chestType, new ChestFile(plugin, chestLocation, chestType));
                }

                CHEST_FILES.put(chestLocation, chestMap);
            }
        }
    }

    @Override
    public void initializeFeature() {
        if (!(arena instanceof ChestArena)) {
            throw new IllegalStateException(arena.getName() + " must implement the interface ChestArena!");
        }

        super.initializeFeature();

        arenaChestLocations.clear();

        for (ChestLocation chestLocation : chestLocations) {
            arenaChestLocations.put(chestLocation, ((ChestArena) arena).getArenaChests(chestLocation));
        }

        refill();
    }

    @Override
    public void stopFeature() {
        super.stopFeature();
        arenaChestLocations.clear();
    }

    public void refill() {
        arenaChestLocations.forEach((chestLocation, arenaLocations) -> {
            arenaLocations.forEach(location -> {
                if (location.getBlock().getType() != Material.CHEST.parseMaterial() && location.getBlock().getType() != Material.TRAPPED_CHEST.parseMaterial()) {
                    return;
                }

                Chest chest = (Chest) location.getBlock().getState();
                Inventory inventory = chest.getBlockInventory();

                ChestFile chestFile = CHEST_FILES.get(chestLocation).get(chestType);
                chestFile.fill(inventory, minAmount, maxAmount);
            });
        });
    }
}
