package me.patothebest.gamecore.treasure.chest;

import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.hologram.Hologram;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.title.Title;
import me.patothebest.gamecore.title.TitleManager;
import me.patothebest.gamecore.treasure.TreasureFactory;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.util.SerializableObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class TreasureChestLocation implements SerializableObject {

    private final Provider<NMS> nmsProvider;
    private final Location location;
    private final Chest chest;
    private final TreasureFactory treasureFactory;
    private Hologram hologram;
    private TreasureChest currentTreasureChest;

    @AssistedInject private TreasureChestLocation(Provider<NMS> nmsProvider, TreasureFactory treasureFactory, @Assisted Location location) {
        this.nmsProvider = nmsProvider;
        this.location = location;
        this.location.getBlock().setType(Material.CHEST.parseMaterial());
        this.treasureFactory = treasureFactory;

        chest = (Chest) location.getBlock().getState();
        createHologram();
    }

    @SuppressWarnings("unchecked")
    @AssistedInject private TreasureChestLocation(Provider<NMS> nmsProvider, TreasureFactory treasureFactory, @Assisted Map<String, Object> data) {
        this.nmsProvider = nmsProvider;
        this.location = Location.deserialize((Map<String, Object>) data.get("location"));
        this.location.getBlock().setType(Material.CHEST.parseMaterial());
        this.treasureFactory = treasureFactory;

        chest = (Chest) location.getBlock().getState();
        createHologram();
    }

    public boolean openChest(IPlayer player, TreasureType treasureType) {
        if(currentTreasureChest != null) {
            return false;
        }

        currentTreasureChest = treasureFactory.createChest(player, treasureType, this, (location) -> location.currentTreasureChest = null);
        currentTreasureChest.playAnimation(location.clone().add(-3, 3, -3));

        player.getPlayer().teleport(location.clone().add(0.5, 1, 0.5));
        Title title = TitleManager.newInstance(ChatColor.GREEN + "Cofre " + treasureType.getName());
        title.setFadeInTime(1);
        title.setStayTime(3);
        title.setFadeOutTime(1);
        title.send(player.getPlayer());
        return true;
    }

    public void createHologram() {
        hologram = nmsProvider.get().createHologram(location.clone().add(0.5, 1, 0.5));
        hologram.setName(ChatColor.GREEN + "Cofre de Tesoros");
    }

    /**
     * Gets the location
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the bukkit chest
     *
     * @return the chest
     */
    public Chest getChest() {
        return chest;
    }

    /**
     * Gets hologram
     *
     * @return the hologram
     */
    public Hologram getHologram() {
        return hologram;
    }

    /**
     * @return true if another player is not opening the chest
     */
    public boolean canOpen() {
        return currentTreasureChest == null;
    }

    /**
     * @return true if another player is opening the chest
     */
    public boolean isNotAvailable() {
        return currentTreasureChest != null;
    }

    /**
     * @return the current treasure chest
     */
    public TreasureChest getCurrentTreasureChest() {
        return currentTreasureChest;
    }

    /**
     * Creates a Map representation of this class.
     * <p>
     * This class must provide a method to restore this class, as defined in
     * the {@link ConfigurationSerializable} interface javadocs.
     *
     */
    @Override
    public void serialize(Map<String, Object> data) {
        data.put("location", location.serialize());
    }
}