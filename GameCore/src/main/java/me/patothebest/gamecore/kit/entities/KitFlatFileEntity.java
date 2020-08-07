package me.patothebest.gamecore.kit.entities;

import me.patothebest.gamecore.kit.KitLayout;
import me.patothebest.gamecore.storage.StorageException;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.flatfile.PlayerProfileFile;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.player.CorePlayer;

import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;

public class KitFlatFileEntity implements FlatFileEntity {

    private final KitManager kitManager;

    @Inject private KitFlatFileEntity(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    public void loadPlayer(CorePlayer player, PlayerProfileFile playerProfileFile) throws StorageException {
        if(playerProfileFile.isSet("kit-uses")) {
            playerProfileFile.getConfigurationSection("kit-uses").getValues(true).forEach((kitName, uses) -> {
                Kit kit = kitManager.getKits().get(kitName);
                player.addKitUses(kit, (Integer) uses);
            });
        }

        if(playerProfileFile.isSet("kit-layouts")) {
            playerProfileFile.getConfigurationSection("kit-layouts").getValues(true).forEach((kitName, layout) -> {
                Kit kit = kitManager.getKits().get(kitName);
                player.modifyKitLayout(kit, new KitLayout((String) layout));
            });
        }
    }

    @Override
    public void savePlayer(CorePlayer player, PlayerProfileFile playerProfileFile) throws StorageException {
        playerProfileFile.set("kit-uses", player.getKitUses().entrySet().stream().collect(Collectors.toMap(o -> o.getKey().getKitName(), Map.Entry::getValue)));
        playerProfileFile.set("kit-layouts", player.getKitLayouts().entrySet().stream().collect(Collectors.toMap(o -> o.getKey().getKitName(), Map.Entry::getValue)));
    }
}
