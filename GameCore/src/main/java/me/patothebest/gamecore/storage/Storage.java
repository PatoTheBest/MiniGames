package me.patothebest.gamecore.storage;

import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.util.Callback;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public interface Storage {

    void loadPlayers(Callback<CorePlayer> playerCallback);

    void load(CorePlayer player, boolean async);

    void save(CorePlayer player);

    void unCache(CorePlayer player);

    void delete(CorePlayer player);

    void loadKits(Map<String, Kit> kitMap);

    void saveKits(Map<String, Kit> kitMap);

    Kit createKit(String name, PlayerInventory playerInventory);

    void saveKit(Kit kit);

    void deleteKit(Kit kit);

    void enableStorage();

    default void postEnable() {}

    default void preDisableStorage() {}

    void disableStorage();

}
