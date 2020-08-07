package me.patothebest.gamecore.arena.modes.random;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.page.DynamicPaginatedUI;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ChooseMap extends DynamicPaginatedUI<String> {

    private final RandomArenaGroup randomArenaGroup;
    private final PlayerManager playerManager;

    @Inject private ChooseMap(CorePlugin plugin, @Assisted Player player, @Assisted RandomArenaGroup randomArenaGroup, PlayerManager playerManager) {
        super(plugin, player, CoreLang.CHOOSE_MAP_UI_TITLE, randomArenaGroup::getAllArenas);
        this.randomArenaGroup = randomArenaGroup;
        this.playerManager = playerManager;
        build();
    }

    @Override
    protected GUIButton createButton(String mapName) {
        return new SimpleButton(new ItemStackBuilder()
                .material(Material.MAP)
                .name(CoreLang.CHOOSE_MAP_BUTTON_NAME.replace(getPlayer(), mapName))
                .lore((!randomArenaGroup.getEnabledArenas().contains(mapName) ? CoreLang.CHOOSE_MAP_BUTTON_JOIN_DESC : CoreLang.CHOOSE_MAP_BUTTON_SELECT_DESC).getMessage(getPlayer())),
                () -> {
                    if (randomArenaGroup.getArenas().containsKey(mapName)) {
                        AbstractArena arena = randomArenaGroup.getArenas().get(mapName);
                        if(playerManager.getPlayer(player.getName()).getCurrentArena() != null) {
                            player.sendMessage(CoreLang.ALREADY_IN_ARENA.getMessage(player));
                            return;
                        }

                        if(arena.getPhase().canJoin() && !arena.isFull()) {
                            arena.addPlayer(player);
                            return;
                        }
                    }

                    randomArenaGroup.addPlayerToQueue(player, mapName);
                    CoreLang.CHOOSE_MAP_ARENA_QUEUED.sendMessage(player);

                    for (AbstractArena value : randomArenaGroup.getArenas().values()) {
                        if (value.getPlayers().size() == 0) {
                            randomArenaGroup.destroyArena(value, true);
                            CoreLang.CHOOSE_MAP_ARENA_CREATED.sendMessage(player);
                            return;
                        }
                    }
                });
    }
}
