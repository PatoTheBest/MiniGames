package me.patothebest.gamecore.feature.features.chests.refill;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.file.ReadOnlyFile;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChestFile extends ReadOnlyFile {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final static List<Integer> RANDOM_SLOTS = new ArrayList<>();
    private final List<ChestItem> chestItems = new ArrayList<>();
    private final CorePlugin plugin;

    static {
        for (int i = 0; i < 27; i++) {
            RANDOM_SLOTS.add(i);
        }
    }

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public ChestFile(CorePlugin plugin, ChestLocation chestLocation, ChestType chestType) {
        super("chests" + File.separatorChar + chestLocation.getFolderName() + "-" + chestType.getFileName());
        this.header = "Chest " + chestType.name();
        this.plugin = plugin;

        // load the file to memory
        load();
    }

    @Override
    protected void writeFile(BufferedWriter writer) throws IOException {
        super.writeFile(writer);
        Utils.writeFileToWriter(writer, plugin.getResource("chests.yml"));
    }

    @Override
    public void load() {
        super.load();

        if(!isSet("items")) {
            return;
        }

        for (String item : getStringList("items")) {
            try {
                String[] split = item.split(" ");

                int chance = Integer.parseInt(split[0]);
                ItemStack itemStack = Utils.parseItem(Arrays.copyOfRange(split, 1, split.length));

                if (itemStack != null) {
                    chestItems.add(new ChestItem(itemStack, chance));
                }
            } catch (Exception e) {
                Utils.printError("Could not parse item " + item, e);
            }
        }
    }

    public void fill(Inventory inventory, int minAmount, int maxAmount) {
        Collections.shuffle(chestItems);
        int added = 0;
        do {
            added = fill(added, inventory, maxAmount);
        } while (added < minAmount);
    }

    private int fill(int added, Inventory inventory, int maxAmount) {
        for (ChestItem chestItem : chestItems) {
            if (Utils.random(100) + 1 <= chestItem.getChance()) {
                if(inventory.contains(chestItem.getItem())) {
                    inventory.addItem(chestItem.getItem());
                } else {
                    int slot;
                    int attempts = 0;
                    Collections.shuffle(RANDOM_SLOTS);

                    do {
                        if(attempts >= RANDOM_SLOTS.size()) {
                            slot = -1;
                            break;
                        }

                        slot = RANDOM_SLOTS.get(attempts);
                        attempts++;
                    } while(inventory.getItem(slot) != null);

                    if(slot != -1) {
                        inventory.setItem(slot, chestItem.getItem());
                    }
                }

                if(maxAmount == -1) {
                    if (added++ > inventory.getSize()) {
                        return added;
                    }
                } else {
                    if (added++ > maxAmount) {
                        return added;
                    }
                }
            }
        }

        return added;
    }

    /**
     * Gets the chest items
     *
     * @return the chest items
     */
    public List<ChestItem> getChestItems() {
        return chestItems;
    }

}