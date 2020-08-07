package me.patothebest.gamecore.guis.user;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.event.player.PlayerStateChangeEvent;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SpectatorUI extends GUIMultiPage {

    private final AbstractArena arena;

    public SpectatorUI(CorePlugin plugin, Player player, AbstractArena arena) {
        super(plugin, player, "Spectate", Math.min(54, Utils.transformToInventorySize(arena.getPlayers().size())));
        this.arena = arena;
        build();
    }

    @Override
    protected void buildContent() {
        final int[] slot = {0};
        arena.getPlayers().stream().skip(45*currentPage).limit(45).forEach(gamePlayer -> {
            ItemStackBuilder itemStack = new ItemStackBuilder().skullOwner(gamePlayer.getName()).name(ChatColor.GREEN + gamePlayer.getName());
            addButton(new SimpleButton(itemStack).action(() -> {
                player.teleport(gamePlayer);
            }), slot[0]);
            slot[0]++;
        });
    }

    @Override
    protected int getListCount() {
        if(arena.getPlayers().size() <= 45) {
            return -1;
        }

        return arena.getPlayers().size();
    }

    @EventHandler
    public void onPlayerStateChange(PlayerStateChangeEvent event) {
        if(event.getArena() == arena) {
            refresh();
        }
    }
}
