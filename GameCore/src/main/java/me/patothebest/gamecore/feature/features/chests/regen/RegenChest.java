package me.patothebest.gamecore.feature.features.chests.regen;

import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RegenChest {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final Chest chest;
    private final ItemStack[] chestContents;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    RegenChest(Chest chest) {
        this.chest = chest;

        // get the holder of the inventory and check if
        // the holder is a double chest
        InventoryHolder ih = chest.getInventory().getHolder();
        boolean doubleChest = ih instanceof DoubleChest;

        // If it is a double chest...
        if(doubleChest) {
            // ...get the whole inventory...
            DoubleChestInventory doubleChestInventory = (DoubleChestInventory) ih.getInventory();

            // ...and combine both chest inventories
            // into one large ItemStack array
            chestContents = Utils.concatenateArray(doubleChestInventory.getLeftSide().getContents(),  doubleChestInventory.getRightSide().getContents());
        } else {
            // ...else just clone the contents
            this.chestContents = chest.getBlockInventory().getContents().clone();
        }

        regen();
    }

    // -------------------------------------------- //
    // REGEN METHOD
    // -------------------------------------------- //

    void regen() {
        // if the block is not a chest...
        if(chest.getBlock().getType() != Material.CHEST.parseMaterial() && chest.getBlock().getType() != Material.TRAPPED_CHEST.parseMaterial()) {
            // ...return.
            // Chests are unbreakable and cannot be broken with explosions
            // nor players that are in an arena, however, player that are
            // in the arena by teleporting (eg creative op players) can
            // break the chests and blocks
            return;
        }

        // Check if the chest inventory size is the same as the saved
        // chest contents array size (length)
        if(chest.getInventory().getSize() != chestContents.length) {
            return;
        }

        // set the inventory contents with the saved
        // contents, this will override any changes,
        // any items added or removed
        chest.getInventory().setContents(chestContents);
    }

    // -------------------------------------------- //
    // GETTER
    // -------------------------------------------- //

    Chest getChest() {
        return chest;
    }

    @Override
    public String toString() {
        return "RegenChest{" +
                "chest=" + chest +
                ", chestContents=" + Arrays.toString(chestContents) +
                '}';
    }
}
