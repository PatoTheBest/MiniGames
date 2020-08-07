package me.patothebest.gamecore.selection;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class DefaultSelectionManager implements SelectionManager, Module {

    private final Map<String, Selection> selectionMap;

    public DefaultSelectionManager() {
        this.selectionMap = new HashMap<>();
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onSelect(PlayerInteractEvent event){
        if(!event.getPlayer().hasPermission(Permission.SETUP.getBukkitPermission()) || PlayerManager.get().getPlayer(event.getPlayer()).isPlaying()) {
            return;
        }

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() != Material.BONE) {
                return;
            }

            Selection selection = getOrCreateSelection(event.getPlayer());
            selection.setPointB(event.getClickedBlock().getLocation());
            event.getPlayer().sendMessage(CoreLang.POINT2_SELECTED.replace(event.getPlayer(), event.getClickedBlock().getLocation().getBlockX() + ", " + event.getClickedBlock().getLocation().getBlockY() + ", " + event.getClickedBlock().getLocation().getBlockZ()));
            event.setCancelled(true);
        }

        if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() != Material.BONE) {
                return;
            }

            Selection selection = getOrCreateSelection(event.getPlayer());
            selection.setPointA(event.getClickedBlock().getLocation());
            event.getPlayer().sendMessage(CoreLang.POINT1_SELECTED.replace(event.getPlayer(), event.getClickedBlock().getLocation().getBlockX() + ", " + event.getClickedBlock().getLocation().getBlockY() + ", " + event.getClickedBlock().getLocation().getBlockZ()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        selectionMap.remove(event.getPlayer().getName());
    }

    private Selection getOrCreateSelection(Player player) {
        if(selectionMap.containsKey(player.getName())) {
            return selectionMap.get(player.getName());
        }

        Selection selection = new Selection();
        selectionMap.put(player.getName(), selection);
        return selection;
    }

    @Override
    public Selection getSelection(Player player) {
        return selectionMap.get(player.getName());
    }
}
