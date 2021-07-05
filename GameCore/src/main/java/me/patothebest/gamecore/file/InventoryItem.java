package me.patothebest.gamecore.file;

import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryItem implements ConfigurationSerializable {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final String configName;
    private final String name;
    private final int slot;
    private final List<String> lore;
    private final ItemStackBuilder itemStack;
    private String command;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @SuppressWarnings("unchecked")
    public InventoryItem(String configName, Map<String, Object> map) {
        this.configName = configName;
        this.name = (String) map.get("name");
        this.slot = (int) map.get("slot");
        this.lore = (List<String>) map.get("lore");
        this.itemStack = new ItemStackBuilder(Utils.itemStackFromString((String) map.get("item")));

        if (map.containsKey("command")) {
            command = (String) map.get("command");
        }

        itemStack.name(name);
        itemStack.lore(lore);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("lore", lore.toArray());
        map.put("slot", slot);
        map.put("item", Utils.itemStackToString(itemStack));
        return map;
    }

    // -------------------------------------------- //
    // GETTERS
    // -------------------------------------------- //

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public String getName() {
        return configName;
    }

    public String getCommand() {
        return command;
    }
}
