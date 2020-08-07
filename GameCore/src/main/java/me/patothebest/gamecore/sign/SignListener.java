package me.patothebest.gamecore.sign;

import com.google.inject.Provider;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.placeholder.PlaceHolderManager;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.inject.Inject;

public class SignListener implements ListenerModule {

    private final ArenaManager arenaManager;
    private final SignManager signManager;
    private final Provider<NMS> nms;
    private final PlayerManager playerManager;
    private final PluginScheduler pluginScheduler;
    private final CoreConfig coreConfig;
    private final PlaceHolderManager placeHolderManager;

    @Inject private SignListener(ArenaManager arenaManager, SignManager signManager, Provider<NMS> nms, PluginScheduler pluginScheduler, CoreConfig coreConfig, PlaceHolderManager placeHolderManager, PlayerManager playerManager) {
        this.arenaManager = arenaManager;
        this.signManager = signManager;
        this.nms = nms;
        this.pluginScheduler = pluginScheduler;
        this.coreConfig = coreConfig;
        this.placeHolderManager = placeHolderManager;
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(!event.getClickedBlock().getType().name().contains("SIGN")) {
            return;
        }

        if(signManager.getNewSigns().containsKey(event.getPlayer().getName())) {
            signManager.getSigns().add(new ArenaSign(pluginScheduler, coreConfig, placeHolderManager, arenaManager, nms, signManager.getNewSigns().get(event.getPlayer().getName()), (Sign) event.getClickedBlock().getState()));
            signManager.getNewSigns().remove(event.getPlayer().getName());
            event.getPlayer().sendMessage(CoreLang.SIGN_CREATED.getMessage(event.getPlayer()));
            signManager.saveSigns();
            signManager.updateSigns();
            event.setCancelled(true);
            return;
        }

        ArenaSign sign = signManager.getSigns().stream().filter(arenaSign -> arenaSign.getLocation().equals(event.getClickedBlock().getLocation())).findFirst().orElse(null);
        if(sign == null) {
            return;
        }

        if(event.getPlayer().hasPermission(Permission.ADMIN.getBukkitPermission()) && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        if(playerManager.getPlayer(event.getPlayer()).getCurrentArena() != null) {
            return;
        }

        AbstractArena arena = arenaManager.getArena(sign.getCurrentArena());

        if(arena == null || !arena.isEnabled()) {
            event.getPlayer().sendMessage(CoreLang.ARENA_IS_NOT_PLAYABLE.getMessage(event.getPlayer()));
            return;
        }

        if(arena.getPhase().canJoin()) {
            if(arena.isFull()) {
                event.getPlayer().sendMessage(CoreLang.ARENA_IS_FULL.getMessage(event.getPlayer()));
                return;
            }

            arena.addPlayer(event.getPlayer());
        } else {
            event.getPlayer().sendMessage(CoreLang.ARENA_IS_RESTARTING.getMessage(event.getPlayer()));
            //player.sendMessage(Lang.ARENA_IS_NOT_PLAYABLE.getMessage(player));
        }

        signManager.updateSigns();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignBreak(BlockBreakEvent event) {
        if(!event.getBlock().getType().name().contains("SIGN")) {
            return;
        }

        ArenaSign sign = signManager.getSigns().stream().filter(arenaSign -> arenaSign.getLocation().equals(event.getBlock().getLocation())).findFirst().orElse(null);
        if(sign == null) {
            return;
        }

        if(!event.getPlayer().hasPermission(Permission.ADMIN.getBukkitPermission())) {
            return;
        }

        signManager.getSigns().remove(sign);
        signManager.saveSigns();
        event.getPlayer().sendMessage(CoreLang.SIGN_REMOVED.getMessage(event.getPlayer()));
    }
}