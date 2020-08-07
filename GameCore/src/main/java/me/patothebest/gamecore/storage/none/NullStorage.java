package me.patothebest.gamecore.storage.none;

import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.storage.Storage;
import me.patothebest.gamecore.util.Callback;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class NullStorage implements Storage {

    public NullStorage() {

    }


    @Override
    public void loadPlayers(Callback<CorePlayer> playerCallback) {

    }

    @Override
    public void load(CorePlayer player, boolean async) {

    }

    @Override
    public void save(CorePlayer player) {

    }

    @Override
    public void unCache(CorePlayer player) {

    }

    @Override
    public void delete(CorePlayer player) {

    }

    @Override
    public void loadKits(Map<String, Kit> kitMap) {

    }

    @Override
    public void saveKits(Map<String, Kit> kitMap) {

    }

    @Override
    public Kit createKit(String name, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public void saveKit(Kit kit) {

    }

    @Override
    public void deleteKit(Kit kit) {

    }

    @Override
    public void enableStorage() {

    }

    @Override
    public void disableStorage() {

    }
}