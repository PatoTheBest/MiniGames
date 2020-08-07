package me.patothebest.gamecore.addon.addons;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.event.player.ArenaPlayerEvent;
import me.patothebest.gamecore.event.player.GameJoinEvent;
import me.patothebest.gamecore.event.player.LobbyJoinEvent;
import me.patothebest.gamecore.event.player.PlayerStateChangeEvent;
import me.patothebest.gamecore.event.player.SpectateEvent;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.addon.Addon;
import me.patothebest.gamecore.arena.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class HideNametags extends Addon {

    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;

    @Inject private HideNametags(PlayerManager playerManager, ArenaManager arenaManager) {
        this.playerManager = playerManager;
        this.arenaManager = arenaManager;
    }

    @Override
    public void configure(ConfigurationSection addonConfigSection) { }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(LobbyJoinEvent event) {
        handleEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(GameJoinEvent event) {
        handleEvent(event);
    }

    private void handleEvent(ArenaPlayerEvent event) {
        Bukkit.getOnlinePlayers().forEach(o -> {
            if (o.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            AbstractArena otherArena = null;
            if (playerManager.getPlayer(o).isInArena()) {
                otherArena = playerManager.getPlayer(o).getCurrentArena();
            }

            if (otherArena != event.getArena()) {
                event.getPlayer().hidePlayer(o);
            } else {
                o.showPlayer(event.getPlayer());
            }
        });

        event.getArena().getPlayers().forEach(player -> {
            if (player.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            event.getPlayer().showPlayer(player);
            player.showPlayer(event.getPlayer());
        });
    }

    @EventHandler
    public void onLeave(ArenaLeaveEvent event) {
        Bukkit.getOnlinePlayers().forEach(o -> {
            if (o.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            event.getPlayer().showPlayer(o);
        });

        event.getArena().getPlayers().forEach(player -> {
            if (player.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            player.hidePlayer(event.getPlayer());
        });

        event.getArena().getSpectators().forEach(player -> {
            if (player.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            player.hidePlayer(event.getPlayer());
        });
    }

    @EventHandler
    public void onDeath(SpectateEvent event) {
        event.getArena().getSpectators().forEach(spectator -> {
            if (spectator.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            event.getPlayer().showPlayer(spectator);
        });

        event.getArena().getPlayers().forEach(player -> {
            if (player.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            player.hidePlayer(event.getPlayer());
        });
    }

    @EventHandler
    public void onDeath(PlayerStateChangeEvent event) {
        if (event.getPlayerState() != PlayerStateChangeEvent.PlayerState.PLAYER) {
            return;
        }

        event.getArena().getSpectators().forEach(spectator -> {
            if (spectator.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            event.getPlayer().hidePlayer(spectator);
        });

        event.getArena().getPlayers().forEach(player -> {
            if (player.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            player.showPlayer(event.getPlayer());
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (AbstractArena arena : arenaManager.getArenas().values()) {
            for (Player player : arena.getPlayers()) {
                player.hidePlayer(event.getPlayer());
            }

            for (Player player : arena.getSpectators()) {
                player.hidePlayer(event.getPlayer());
            }
        }
    }

    @Override
    public String getConfigPath() {
        return "separate-nametags";
    }
}