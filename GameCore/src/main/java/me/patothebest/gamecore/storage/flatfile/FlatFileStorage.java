package me.patothebest.gamecore.storage.flatfile;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.lang.CoreLocaleManager;
import me.patothebest.gamecore.logger.InjectParentLogger;
import me.patothebest.gamecore.permission.PermissionGroupManager;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.storage.StorageException;
import me.patothebest.gamecore.storage.StorageManager;
import me.patothebest.gamecore.util.Callback;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.PlayerInventory;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FlatFileStorage implements IFlatFileStorage {

    private final Map<String, PlayerProfileFile> playerfiles = new HashMap<>();
    private final CorePlugin plugin;
    private final StorageManager storageManager;
    private final Set<FlatFileEntity> coreSQLEntities;
    private final File kitDirectory = new File(Utils.PLUGIN_DIR, "kits");
    private final PermissionGroupManager permissionGroupManager;
    @InjectParentLogger(parent = StorageManager.class) private Logger logger;

    @Inject private FlatFileStorage(CorePlugin plugin, StorageManager storageManager, Set<FlatFileEntity> coreSQLEntities, PermissionGroupManager permissionGroupManager) {
        this.plugin = plugin;
        this.storageManager = storageManager;
        this.coreSQLEntities = coreSQLEntities;
        this.permissionGroupManager = permissionGroupManager;
    }

    @Override
    public void loadPlayers(Callback<CorePlayer> playerCallback) {
        List<CorePlayer> players = new CopyOnWriteArrayList<>();

        File playerDir = new File(Utils.PLUGIN_DIR, "players/");

        if(playerDir.listFiles() != null) {
            for(File file : playerDir.listFiles()) {
                //TODO: Finish
            }
        }
    }

    @Override
    public void load(CorePlayer player, boolean async) {
        if(playerfiles.containsKey(player.getName())) {
            return;
        }

        PlayerProfileFile file = new PlayerProfileFile(storageManager.isUseUUIDs() ? player.getUniqueId().toString() : player.getName());
        player.setLocale(CoreLocaleManager.getLocale(file.getString("locale", CoreLocaleManager.DEFAULT_LOCALE.getName())));

        try {
            for (FlatFileEntity flatFileEntity : coreSQLEntities) {
                flatFileEntity.loadPlayer(player, file);
            }
        } catch (StorageException e) {
            e.printStackTrace();
        }

        playerfiles.put(player.getName(), file);
        player.setAllDataLoaded(true);
    }

    @Override
    public void save(CorePlayer player) {
        if(!playerfiles.containsKey(player.getName())) {
            playerfiles.put(player.getName(), new PlayerProfileFile(storageManager.isUseUUIDs() ? player.getUniqueId().toString() : player.getName()));
        }

        PlayerProfileFile file = playerfiles.get(player.getName());
        file.set("locale", player.getLocale().getName());
        file.set("name", player.getName());

        try {
            for (FlatFileEntity flatFileEntity : coreSQLEntities) {
                flatFileEntity.savePlayer(player, file);
            }
        } catch (StorageException e) {
            e.printStackTrace();
        }

        file.save();
    }

    @Override
    public void unCache(CorePlayer player) {
        if(!playerfiles.containsKey(player.getName())) {
           return;
        }

        playerfiles.remove(player.getName());
        player.deleteObservers();
    }

    @Override
    public void delete(CorePlayer player) {
        PlayerProfileFile file;
        if(playerfiles.containsKey(player.getName())) {
            file = playerfiles.get(player.getName());
        } else {
            file = new PlayerProfileFile(player.getName());
        }

        file.delete();
        unCache(player);
    }

    @Override
    public void loadKits(Map<String, Kit> kitMap) {
        if(kitDirectory.listFiles() != null) {
            for(File file : kitDirectory.listFiles()) {
                try {
                    Kit kit = new Kit(plugin, permissionGroupManager, file.getName().replace(".yml", ""));
                    kitMap.put(kit.getKitName(), kit);
                    logger.info("Loaded kit " + kit.getKitName());
                } catch(Throwable t) {
                    logger.log(Level.SEVERE, ChatColor.RED + "Could not load kit " + file.getName(), t);
                }
            }
        }
    }

    @Override
    public Kit createKit(String name, PlayerInventory playerInventory) {
        return new Kit(plugin, permissionGroupManager, name, playerInventory.getArmorContents(), playerInventory.getContents());
    }

    @Override
    public void saveKits(Map<String, Kit> kitMap) {
        kitMap.values().forEach(Kit::saveToFile);
    }

    @Override
    public void saveKit(Kit kit) {
        kit.saveToFile();
    }

    @Override
    public void deleteKit(Kit kit) {
        kit.delete();
    }

    @Override
    public void enableStorage() {
        if(!kitDirectory.exists()) {
            kitDirectory.mkdirs();
        }
    }

    @Override
    public void disableStorage() {
        // we don't leave streams open nor connections
        // opened so we don't have to close anything
    }
}