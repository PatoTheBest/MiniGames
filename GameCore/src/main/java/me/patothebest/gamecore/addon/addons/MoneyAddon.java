package me.patothebest.gamecore.addon.addons;

import com.google.inject.Inject;
import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.event.arena.GameEndEvent;
import me.patothebest.gamecore.permission.PermissionGroup;
import me.patothebest.gamecore.permission.PermissionGroupManager;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.addon.Addon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

public class MoneyAddon extends Addon {

    // Vault objects
    private final PermissionGroupManager permissionGroupManager;
    private final Map<PermissionGroup, Double> moneyMultiplier = new HashMap<>();
    private final PlayerManager playerManager;
    private double moneyToKill;
    private double moneyToWin;

    @Inject private MoneyAddon(PermissionGroupManager permissionGroupManager, PlayerManager playerManager) {
        this.permissionGroupManager = permissionGroupManager;
        this.playerManager = playerManager;
    }

    @Override
    public void configure(ConfigurationSection addonConfigSection) {
        moneyToKill = addonConfigSection.getDouble("kill");
        moneyToWin = addonConfigSection.getDouble("win");

        if(addonConfigSection.getBoolean("money-multiplier.enabled")) {
            Map<String, Object> limits = addonConfigSection.getConfigurationSection("money-multiplier.multipliers").getValues(true);
            limits.forEach((string, limit) -> {
                if(!(limit instanceof Double)) {
                    return;
                }

                PermissionGroup permissionGroup = permissionGroupManager.getOrCreatePermissionGroup(string);
                moneyMultiplier.put(permissionGroup, (Double) limit);
            });
        }
    }

    @EventHandler
    public void onKill(CombatDeathEvent event) {
        if(event.getKillerPlayer() == null) {
            return;
        }

        playerManager.getPlayer(event.getKillerPlayer()).giveMoney(getMultiplier(event.getKillerPlayer()) * moneyToKill);
    }

    @EventHandler
    public void onWin(GameEndEvent event) {
        event.getWinners().forEach(player -> {
            playerManager.getPlayer(player).giveMoney(getMultiplier(player) * moneyToWin);
        });
    }

    private double getMultiplier(Player player) {
        final double[] multiplier = {1.0};

        moneyMultiplier.forEach((permissionGroup, multiplierValue) -> {
            if(multiplierValue > multiplier[0]) {
                if(permissionGroup.hasPermission(player)) {
                    multiplier[0] = multiplierValue;
                }
            }
        });

        return multiplier[0];
    }

    @Override
    public String getConfigPath() {
        return "money";
    }
}