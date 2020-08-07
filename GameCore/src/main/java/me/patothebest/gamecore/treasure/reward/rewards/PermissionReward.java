package me.patothebest.gamecore.treasure.reward.rewards;

import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.treasure.reward.Reward;
import me.patothebest.gamecore.treasure.reward.RewardData;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.ConfigurationSection;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

public class PermissionReward extends Reward {

    private final List<String> permissions = new ArrayList<>();
    private final Provider<Permission> permissionProvider;

    @Inject private PermissionReward(Provider<Permission> permissionProvider) {
        this.permissionProvider = permissionProvider;
    }

    @Override
    public boolean parse(ConfigurationSection configurationSection) {
        boolean parsedSuper = super.parse(configurationSection);
        boolean parsed = false;

        if(configurationSection.isSet("permission")) {
            permissions.add(configurationSection.getString("permission"));
            parsed = true;
        }

        if(configurationSection.isSet("permissions")) {
            permissions.addAll(configurationSection.getStringList("permissions"));
            parsed = true;
        }

        if(!parsed) {
            System.err.println("Reward " + configurationSection.getCurrentPath() + " is missing either a permission or a permission list!");
        }

        return parsed && parsedSuper;
    }

    @Override
    public RewardData give(IPlayer player) {
        if(permissionProvider.get() == null) {
            System.err.println("No compatible permission plugin has been found!");
            return new RewardData("Error", new ItemStackBuilder(Material.BARRIER));
        }

        for (String permission : permissions) {
            permissionProvider.get().playerAdd(player.getPlayer(), permission);
        }

        return new RewardData(hologramMessage, displayItem);
    }
}