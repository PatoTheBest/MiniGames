package me.patothebest.gamecore.addon.addons;

import com.google.inject.Inject;
import it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.patothebest.gamecore.addon.Addon;
import me.patothebest.gamecore.addon.AddonManager;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.logger.InjectParentLogger;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class KillStreaksAddon extends Addon {

    private final Int2IntMap playerStreaks = new Int2IntAVLTreeMap();
    private final Int2ObjectMap<KillStreak> killStreaks = new Int2ObjectAVLTreeMap<>();
    private final PlayerManager playerManager;
    @InjectParentLogger(parent = AddonManager.class) private Logger logger;
    private int smallestStreak = 1000;
    private String shutdownMessage = "";
    private String shutdownPlayerMessage = "";
    private boolean teamColors;

    @Inject private KillStreaksAddon(PlayerManager playerManager) {
        this.playerManager = playerManager;
        playerStreaks.defaultReturnValue(-1);
        killStreaks.defaultReturnValue(null);
    }

    @Override
    public void configure(ConfigurationSection addonConfigSection) {
        killStreaks.clear();
        smallestStreak = 100;
        ConfigurationSection streaks = addonConfigSection.getConfigurationSection("streaks");

        teamColors = addonConfigSection.getBoolean("team-color");
        shutdownMessage = addonConfigSection.getString("shutdown");
        shutdownPlayerMessage = addonConfigSection.getString("shutdown-player");

        if (streaks == null) {
            logger.log(Level.WARNING, "Killstreaks are enabled but there are no configured streaks!");
            return;
        }

        for (String streak : streaks.getKeys(false)) {
            if (!Utils.isNumber(streak)) {
                Utils.printError("Error while parsing killStreaks. ", streak + " is not a number");
                continue;
            }
            int streakAmount = Integer.parseInt(streak);
            killStreaks.put(streakAmount,
                    new KillStreak(streaks.getString(streak + ".message"), streaks.getInt(streak + ".money")));
            smallestStreak = Math.min(streakAmount, smallestStreak);
        }
    }

    @EventHandler
    public void onKill(CombatDeathEvent event) {
        IPlayer player = playerManager.getPlayer(event.getPlayer());
        IPlayer killerPlayer = null;

        if (event.getKillerPlayer() != null) {
            killerPlayer = playerManager.getPlayer(event.getKillerPlayer());
        }

        AbstractArena currentArena = player.getCurrentArena();

        if (currentArena == null || player.getGameTeam() == null) {
            return;
        }

        String teamColor = teamColors ? Utils.getColorFromDye(player.getGameTeam().getColor()).toString() : "";
        String killerColor = "";

        if (teamColors && killerPlayer != null && killerPlayer.getGameTeam() != null) {
            killerColor = Utils.getColorFromDye(killerPlayer.getGameTeam().getColor()).toString();
        }

        int i = playerStreaks.get(event.getPlayer().getEntityId());
        if (i >= smallestStreak) {
            if (killerPlayer == null) {
                String message = ChatColor.translateAlternateColorCodes('&', shutdownMessage.replace("%player_name%", teamColor + player.getName()));
                currentArena.sendMessageToArena(locale -> message);
            } else {
                String message = ChatColor.translateAlternateColorCodes('&', shutdownPlayerMessage
                        .replace("%player_name%", teamColor + player.getName())
                        .replace("%killer%", killerColor + killerPlayer.getName()));
                currentArena.sendMessageToArena(locale -> message);
            }
        }

        playerStreaks.remove(event.getPlayer().getEntityId());
        if (killerPlayer == null) {
            return;
        }

        int killstreakCount = playerStreaks.getOrDefault(killerPlayer.getEntityId(), 0);
        playerStreaks.put(killerPlayer.getEntityId(), ++killstreakCount);
        KillStreak killstreak = killStreaks.get(killstreakCount);
        if (killstreak == null) {
            return;
        }

        if (killstreak.money > 0) {
            killerPlayer.giveMoney(killstreak.money);
        }
        String message = ChatColor.translateAlternateColorCodes('&', killstreak.message.replace("%player_name%", killerColor + killerPlayer.getName()));
        currentArena.sendMessageToArena(locale -> message);
    }

    @EventHandler
    public void onArenaLeave(ArenaLeaveEvent event) {
        playerStreaks.remove(event.getPlayer().getEntityId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerStreaks.remove(event.getPlayer().getEntityId());
    }

    @Override
    public String getConfigPath() {
        return "killstreaks";
    }

    private static class KillStreak {
        private final String message;
        private final int money;

        private KillStreak(String message, int money) {
            this.message = message;
            this.money = money;
        }
    }
}
