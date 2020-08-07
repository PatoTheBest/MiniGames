package me.patothebest.gamecore.sign;

import com.google.inject.Provider;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.placeholder.PlaceHolderManager;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.arena.ArenaManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ArenaSign implements ConfigurationSerializable {

    private final PlaceHolderManager placeHolderManager;
    private final ArenaManager arenaManager;
    private final Provider<NMS> nms;
    private final String arenaName;

    /*
    * The current arena is always defaulted to the arena this sign is bound to,
    * but it can be changed to display any arena on runtime without persisting
    * through saves. It is currently used by the Random-Arena-Mode
    */
    private String currentArena;
    private final Location location;
    private final Map<SignStatus, String[]> signLines;
    private final Map<SignStatus, ItemStack> blockBehindSign = new HashMap<>();
    private Block attachedBlock;

    ArenaSign(PluginScheduler pluginScheduler, CoreConfig coreConfig, PlaceHolderManager placeHolderManager, ArenaManager arenaManager, Provider<NMS> nms, String arenaName, Sign sign) {
        this.arenaManager = arenaManager;
        this.nms = nms;
        this.signLines = new HashMap<>();
        this.location = sign.getLocation();
        this.placeHolderManager = placeHolderManager;
        this.arenaName = currentArena = arenaName;
        loadConfig(coreConfig);

        pluginScheduler.runTaskLater(this::update, 20L);
    }

    @SuppressWarnings("unchecked")
    public ArenaSign(PluginScheduler pluginScheduler, CoreConfig coreConfig, PlaceHolderManager placeHolderManager, SignManager signManager, ArenaManager arenaManager, Provider<NMS> nms, Map<String, Object> map) {
        this.nms = nms;
        this.signLines = new HashMap<>();
        this.placeHolderManager = placeHolderManager;
        this.arenaManager = arenaManager;
        this.location = Location.deserialize((Map<String, Object>) map.get("location"));
        this.arenaName = currentArena = (String) map.get("arena");
        loadConfig(coreConfig);

        pluginScheduler.runTaskLater(this::update, 20L);
    }

    @SuppressWarnings("unchecked")
    private void loadConfig(CoreConfig coreConfig) {
        (coreConfig.getConfigurationSection("signs").getValues(true)).forEach((key, value) -> {
            SignStatus signStatus = Utils.getFromStream(Stream.of(SignStatus.values()), signStatusElement -> signStatusElement.name().equalsIgnoreCase(key));

            if (signStatus == null) {
                return;
            }

            List<String> lines = (List<String>) value;
            signLines.put(signStatus, lines.toArray(new String[4]));
        });

        if (coreConfig.getBoolean("sign-block-color.enabled")) {
            (coreConfig.getConfigurationSection("sign-block-color.status").getValues(true)).forEach((key, value) -> {
                SignStatus signStatus = Utils.getFromStream(Stream.of(SignStatus.values()),signStatusElement -> signStatusElement.name().equalsIgnoreCase(key));

                if (signStatus == null) {
                    return;
                }

                String itemRaw = (String) value;
                ItemStack itemStack = Utils.itemStackFromString(itemRaw);

                if (itemRaw == null) {
                    return;
                }

                blockBehindSign.put(signStatus, itemStack);
            });

            blockBehindSign.put(SignStatus.UNKNOWN, new ItemStackBuilder().material(Material.REDSTONE_BLOCK));
        }

        signLines.put(SignStatus.UNKNOWN, new String[]{"", "UNKNOWN STATUS", "{status}", ""});
    }

    public void update() {
        if(!(location.getBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) location.getBlock().getState();

        if (attachedBlock == null) {
            attachedBlock = nms.get().getBlockAttachedToSign(location.getBlock());
        }

        AbstractArena arena = arenaManager.getArena(currentArena);
        ArenaState arenaState = arena != null ? arena.getArenaState() : ArenaState.OTHER;
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, parseLine(signLines.get(transform(arenaState.getName()))[i], arena));
        }

        if (!blockBehindSign.isEmpty()) {
            ItemStack item = blockBehindSign.get(transform(arenaState.getName()));
            nms.get().setBlock(attachedBlock, item);
        }

        sign.update(true, false);
    }

    private SignStatus transform(String status) {
       return Arrays.stream(SignStatus.values()).filter(signStatus -> signStatus.name().replace("_", "-").equalsIgnoreCase(status)).findFirst().orElse(SignStatus.UNKNOWN);
    }

    private String parseLine(String line, AbstractArena arena) {
        return placeHolderManager.replace(arena, line);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> sign = new HashMap<>();
        sign.put("location", location.serialize());
        sign.put("arena", arenaName);
        return sign;
    }

    public Location getLocation() {
        return location;
    }

    public String getArena() {
        return arenaName;
    }

    public String getCurrentArena() {
        return currentArena;
    }

    public void setCurrentArena(String currentArena) {
        this.currentArena = currentArena;
    }

    @Override
    public String toString() {
        return "ArenaSign{" +
                ", arena=" + arenaName +
                ", location=" + location +
                ", signLines=" + signLines +
                '}';
    }
}
