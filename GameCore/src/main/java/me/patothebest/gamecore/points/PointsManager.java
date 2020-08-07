package me.patothebest.gamecore.points;

import com.google.inject.Inject;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.permission.PermissionGroup;
import me.patothebest.gamecore.permission.PermissionGroupManager;
import me.patothebest.gamecore.event.player.ArenaLeaveMidGameEvent;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.stats.StatsUpdateEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

@ModuleName("Points Manager")
public class PointsManager implements ListenerModule, ActivableModule {

    private final CoreConfig config;
    private final PlayerManager playerManager;
    private final PermissionGroupManager permissionGroupManager;
    private final Map<PermissionGroup, Integer> pointsMultipliers = new HashMap<>();
    private boolean enabled;
    private int wins;
    private int kills;
    private int deaths;
    private int loses;
    private int leaveGame;

    @Inject private PointsManager(CoreConfig config, PlayerManager playerManager, PermissionGroupManager permissionGroupManager) {
        this.config = config;
        this.playerManager = playerManager;
        this.permissionGroupManager = permissionGroupManager;
    }

    @Override
    public void onPostEnable() {
        ConfigurationSection pointsSection = config.getConfigurationSection("points");
        enabled = pointsSection.getBoolean("enabled");
        kills = pointsSection.getInt("kill");
        wins = pointsSection.getInt("win");
        deaths = pointsSection.getInt("death");
        loses = pointsSection.getInt("lose");
        leaveGame = pointsSection.getInt("leave-game");

        if(config.getBoolean("points-multiplier.enabled")) {
            Map<String, Object> limits = config.getConfigurationSection("points-multiplier.multipliers").getValues(true);
            limits.forEach((string, limit) -> {
                if(!(limit instanceof Integer)) {
                    return;
                }

                PermissionGroup permissionGroup = permissionGroupManager.getOrCreatePermissionGroup(string);
                pointsMultipliers.put(permissionGroup, (Integer) limit);
            });
        }
    }

    @EventHandler
    public void onPlayerPrepare(ArenaLeaveMidGameEvent event) {
        if(!enabled) {
            return;
        }

        playerManager.getPlayer(event.getPlayer()).setPoints(playerManager.getPlayer(event.getPlayer()).getPoints() + leaveGame);
    }

    @EventHandler
    public void onStatUpdate(StatsUpdateEvent event) {
        if(!enabled) {
            return;
        }

        final int[] multiplier = {1};
        IPlayer player = event.getPlayer();

        pointsMultipliers.forEach((permissionGroup, multiplierValue) -> {
            if(multiplierValue > multiplier[0]) {
                if(permissionGroup.hasPermission(player)) {
                    multiplier[0] = multiplierValue;
                }
            }
        });

        switch (event.getStatistic().getStatName()) {
            case "wins":
                player.setPoints(player.getPoints() + getTotalPoints(wins, multiplier[0]));
                break;
            case "kills":
                player.setPoints(player.getPoints() +  getTotalPoints(kills, multiplier[0]));
                break;
            case "loses":
                player.setPoints(player.getPoints() + getTotalPoints(loses, multiplier[0]));
                break;
            case "deaths":
                player.setPoints(player.getPoints() + getTotalPoints(deaths, multiplier[0]));
                break;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    private int getTotalPoints(int points, int multiplier) {
        return points > 0 ? points * multiplier : points;
    }
}
