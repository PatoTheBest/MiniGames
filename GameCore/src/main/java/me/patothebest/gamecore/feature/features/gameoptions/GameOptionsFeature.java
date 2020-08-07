package me.patothebest.gamecore.feature.features.gameoptions;

import com.google.inject.Inject;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.event.player.LobbyJoinEvent;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.phase.Phase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class GameOptionsFeature extends AbstractFeature {

    private final Map<Integer, GameOption> buttonsInMenu = new HashMap<>();
    private final UserGUIFactory userGUIFactory;
    private int slot = 4;

    @Inject private GameOptionsFeature(UserGUIFactory userGUIFactory) {
        this.userGUIFactory = userGUIFactory;
    }

    public <T extends GameOption> void registerGameOption(Phase<?> phase, Class<T> gameOptionClass, int slot) {
        T gameOption = arena.createFeature(gameOptionClass);
        phase.getFeatures().put(gameOptionClass, gameOption);
        buttonsInMenu.put(slot, gameOption);
    }

    @EventHandler
    public void onArenaJoin(LobbyJoinEvent event) {
        if(!isPlayingInArena(event.getPlayer())) {
            return;
        }

        event.getPlayer().getInventory().setItem(slot, new ItemStackBuilder().material(Material.CHEST).name(event.getPlayer(), CoreLang.LOBBY_GAME_OPTIONS));
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        if(event.getPlayer().getItemInHand() == null) {
            return;
        }

        switch (event.getPlayer().getItemInHand().getType()) {
            case CHEST:
                userGUIFactory.openGameOptions(event.getPlayer(), this);
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

        switch (event.getCurrentItem().getType()) {
            case CHEST:
                userGUIFactory.openGameOptions((Player) event.getWhoClicked(), this);
                break;
            default:
        }

        event.setCancelled(true);
    }

    public Map<Integer, GameOption> getButtonsInMenu() {
        return buttonsInMenu;
    }

    /**
     * Sets the slot
     */
    public void setSlot(int slot) {
        this.slot = slot;
    }
}
