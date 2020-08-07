package me.patothebest.gamecore.feature.features.spectator;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.command.ChatColor;
import me.patothebest.gamecore.event.player.SpectateEvent;
import me.patothebest.gamecore.guis.user.SpectatorUI;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.title.Title;
import me.patothebest.gamecore.title.TitleManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpectatorFeature extends AbstractFeature {

    private final CorePlugin plugin;
    private final PlayerManager playerManager;
    private final PluginScheduler pluginScheduler;

    @Inject private SpectatorFeature(CorePlugin plugin, PlayerManager playerManager, PluginScheduler pluginScheduler) {
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.pluginScheduler = pluginScheduler;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isSpectating(event.getPlayer())) {
            return;
        }

        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().getType() == null) {
            return;
        }

        switch (event.getItem().getType()) {
            case COMPASS:
                new SpectatorUI(plugin, event.getPlayer(), arena);
                break;
            case MAGMA_CREAM:
                arena.removePlayer(event.getPlayer());
                break;
            default:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onPlayerStateChange(SpectateEvent event) {
        Player player = event.getPlayer();
        player.getInventory().setItem(0, new ItemStackBuilder().material(Material.COMPASS).name(ChatColor.GREEN + "Spectate"));
        player.getInventory().setItem(8, new ItemStackBuilder().material(Material.MAGMA_CREAM).name(ChatColor.RED + "Leave"));

        pluginScheduler.runTaskLater(() -> {
            if(!player.isOnline()) {
                return;
            }

            Title title = TitleManager.newInstance(CoreLang.DEATH_TITLE.getMessage(player));
            title.setSubtitle(CoreLang.DEATH_SUBTITLE.getMessage(player));
            title.setFadeInTime(0);
            title.setFadeOutTime(1);
            title.setStayTime(2);
            title.send(player);
        }, 1L);
    }

    private boolean isSpectating(Player player) {
        return playerManager.getPlayer(player).getCurrentArena() != null && playerManager.getPlayer(player).getCurrentArena() == arena && playerManager.getPlayer(player).getCurrentArena().getSpectators().contains(player);
    }
}