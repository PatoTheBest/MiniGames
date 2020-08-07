package me.patothebest.thetowers.arena;

import me.patothebest.gamecore.util.Tickable;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.ArenaLocation;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ItemDropper implements ConfigurationSerializable, Tickable {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    // temporary objects only for the game
    private final ArenaLocation location;
    private final String name;

    // things that are going to be saved
    private ItemStack itemStack;
    private Entity lastItem;
    private int ticksSinceLastDrop;
    private int interval;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public ItemDropper(Arena arena, String name, Location location, ItemStack itemStack, int interval) {
        this.name = name;
        this.location = new ArenaLocation(arena, location.getWorld(), location.getBlockX()+0.5, location.getBlockY()+1, location.getBlockZ() + 0.5);
        this.itemStack = itemStack;
        this.interval = interval;
    }

    public ItemDropper(Arena arena, Map<String, Object> data) {
        this.name = (String) data.get("name");
        this.location = ArenaLocation.deserialize((Map<String, Object>) data.get("location"), arena);
        this.itemStack = Utils.itemStackFromString((String) data.get("itemstack"));
        this.interval = (int) data.get("interval");
    }

    // -------------------------------------------- //
    // PUBLIC METHODS
    // -------------------------------------------- //

    @Override
    public void tick() {
        // tick
        ticksSinceLastDrop++;

        // If it is time to drop an item...
        if(interval*20 <= ticksSinceLastDrop) {
            // ...reset time
            ticksSinceLastDrop = 0;

            // If the last item has not been picked up (by a hopper or player)...
            if(lastItem != null && lastItem.isOnGround()) {
                // ...remove the item
                lastItem.remove();
            }

            // ...and drop item
            lastItem = location.getWorld().dropItem(location, itemStack);
            lastItem.setVelocity(new Vector(0, 0, 0));
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("location", location.serialize());
        data.put("itemstack", Utils.itemStackToString(itemStack));
        data.put("interval", interval);
        return data;
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "ItemDropper{" +
                "location=" + location +
                ", name='" + name + '\'' +
                ", itemStack=" + itemStack +
                ", lastItem=" + lastItem +
                ", ticksSinceLastDrop=" + ticksSinceLastDrop +
                ", interval=" + interval +
                '}';
    }
}
