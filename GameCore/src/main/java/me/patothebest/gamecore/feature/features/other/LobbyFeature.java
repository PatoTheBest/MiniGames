package me.patothebest.gamecore.feature.features.other;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.guis.admin.AdminUI;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.privatearenas.PrivateArena;
import me.patothebest.gamecore.privatearenas.PrivateArenasManager;
import me.patothebest.gamecore.privatearenas.ui.PrivateArenaUIFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class LobbyFeature extends AbstractFeature {

    private final Cache<String, Long> cooldown = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build();
    private final CorePlugin plugin;
    private final UserGUIFactory userGUIFactory;
    private final PrivateArenaUIFactory privateArenaUIFactory;
    private final PrivateArenasManager privateArenasManager;

    @Inject private LobbyFeature(CorePlugin plugin, UserGUIFactory userGUIFactory, PrivateArenaUIFactory privateArenaUIFactory, PrivateArenasManager privateArenasManager) {
        this.plugin = plugin;
        this.userGUIFactory = userGUIFactory;
        this.privateArenaUIFactory = privateArenaUIFactory;
        this.privateArenasManager = privateArenasManager;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        if(cooldown.getIfPresent(event.getPlayer().getName()) != null) {
            return;
        }

        if(event.getPlayer().getItemInHand() == null) {
            return;
        }

        switch (Material.matchMaterial(event.getPlayer().getItemInHand())) {
            case NETHER_STAR:
                userGUIFactory.openKitShop(event.getPlayer());
                break;
            case LIGHT_BLUE_WOOL:
                if (!arena.isTeamSelector()) {
                    CoreLang.GUI_PRIVATE_ARENA_TEAM_SELECTION_DISABLED.sendMessage(event.getPlayer());
                    return;
                }

                if(!event.getPlayer().hasPermission(Permission.CHOOSE_TEAM.getBukkitPermission())) {
                    event.getPlayer().sendMessage(CoreLang.GUI_USER_CHOOSE_TEAM_NO_PERMISSION.getMessage(event.getPlayer()));
                    break;
                }

                userGUIFactory.createTeamUI(event.getPlayer(), arena, null);
                break;
            case MAGMA_CREAM:
                arena.removePlayer(event.getPlayer());
                break;
            case COMPARATOR:
                handleAdminClick(event.getPlayer(), arena);
                break;
            default:
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        if(event.getCurrentItem() == null) {
            return;
        }

        if(event.getClickedInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        switch (Material.matchMaterial(event.getCurrentItem().getType())) {
            case NETHER_STAR:
                userGUIFactory.openKitShop((Player) event.getWhoClicked());
                break;
            case LIGHT_BLUE_WOOL:
                if (!arena.isTeamSelector()) {
                    CoreLang.GUI_PRIVATE_ARENA_TEAM_SELECTION_DISABLED.sendMessage(event.getWhoClicked());
                    return;
                }

                if(!event.getWhoClicked().hasPermission(Permission.CHOOSE_TEAM.getBukkitPermission())) {
                    event.getWhoClicked().sendMessage(CoreLang.GUI_USER_CHOOSE_TEAM_NO_PERMISSION.getMessage(event.getWhoClicked()));
                    break;
                }

                userGUIFactory.createTeamUI((Player) event.getWhoClicked(), arena, null);
                break;
            case MAGMA_CREAM:
                arena.removePlayer((Player) event.getWhoClicked());
                break;
            case COMPARATOR:
                handleAdminClick((Player) event.getWhoClicked(), arena);
                break;
            default:
        }

        event.setCancelled(true);
    }

    private void handleAdminClick(Player player, AbstractArena arena) {
        if (!arena.isPrivateArena()) {
            new AdminUI(plugin, player, arena);
        } else {
            for (PrivateArena value : privateArenasManager.getPrivateArenaMap().values()) {
                if (value.getArena() == arena) {
                    privateArenaUIFactory.createPrivateArenaMenu(player, value);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        arena.getTeamPreferences().forEach((team, players) -> players.remove(event.getPlayer()));
    }

    public void playerJoin(Player player) {
        cooldown.put(player.getName(), System.currentTimeMillis());
    }
}
