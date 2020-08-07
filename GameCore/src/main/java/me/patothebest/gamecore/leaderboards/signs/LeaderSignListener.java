package me.patothebest.gamecore.leaderboards.signs;

import com.google.inject.Inject;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.util.Callback;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaderSignListener implements ListenerModule {

    private final LeaderSignsManager leaderSignsManager;

    @Inject LeaderSignListener(LeaderSignsManager leaderSignsManager) {
        this.leaderSignsManager = leaderSignsManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        leaderSignsManager.getBlockInteractCallback().remove(event.getPlayer());
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!event.getClickedBlock().getType().name().contains("SIGN")) {
            return;
        }

        Callback<Block> callback = leaderSignsManager.getBlockInteractCallback().remove(event.getPlayer());

        if(callback != null) {
            callback.call(event.getClickedBlock());
            event.setCancelled(true);
            return;
        }

        Callback<LeaderSign> signCallback = leaderSignsManager.getSignInteractCallback().get(event.getPlayer());

        if (signCallback == null) {
            return;
        }

        for (LeaderSign sign : leaderSignsManager.getSigns()) {
            if (sign.getLocation().equals(event.getClickedBlock().getLocation())) {
                signCallback.call(sign);
                leaderSignsManager.getSignInteractCallback().remove(event.getPlayer());
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignBreak(BlockBreakEvent event) {
        if(!event.getBlock().getType().name().contains("SIGN")) {
            return;
        }

        LeaderSign sign = null;
        for (LeaderSign signI : leaderSignsManager.getSigns()) {
            if (signI.getLocation().equals(event.getBlock().getLocation())) {
                sign = signI;
                break;
            }
        }

        if(sign == null) {
            return;
        }

        if(!event.getPlayer().hasPermission(Permission.ADMIN.getBukkitPermission())) {
            event.setCancelled(true);
            return;
        }

        sign.destroy();
        leaderSignsManager.getSigns().remove(sign);
        leaderSignsManager.saveData();
        event.getPlayer().sendMessage(CoreLang.SIGN_REMOVED.getMessage(event.getPlayer()));
    }
}
