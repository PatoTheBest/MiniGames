package me.patothebest.gamecore.guis.user;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.page.DynamicPaginatedUI;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Predicate;

public class JoinArenaGUI extends DynamicPaginatedUI<AbstractArena> implements Runnable {

    private final BukkitTask task;
    private final PlayerManager playerManager;
    private final Predicate<AbstractArena> filter;
    private final ArenaManager arenaManager;

    @AssistedInject private JoinArenaGUI(CorePlugin plugin, PluginScheduler pluginScheduler, ArenaManager arenaManager, PlayerManager playerManager, @Assisted Player player) {
        super(plugin, player, CoreLang.GUI_USER_JOIN_ARENA_TITLE, () -> arenaManager.getAvailableArenas(player, abstractArena -> true));
        this.arenaManager = arenaManager;
        this.playerManager = playerManager;
        filter = abstractArena -> true;
        task = pluginScheduler.runTaskTimer(this, 20L, 20L);
        build();
    }

    @AssistedInject private JoinArenaGUI(CorePlugin plugin, PluginScheduler pluginScheduler, ArenaManager arenaManager, PlayerManager playerManager, @Assisted Player player, @Assisted Predicate<AbstractArena> filter) {
        super(plugin, player, CoreLang.GUI_USER_JOIN_ARENA_TITLE, () -> arenaManager.getAvailableArenas(player, filter));
        this.arenaManager = arenaManager;
        this.playerManager = playerManager;
        this.filter = filter;
        task = pluginScheduler.runTaskTimer(this, 20L, 20L);
        build();
    }

    @Override
    protected GUIButton createButton(AbstractArena arena) {
        ItemStackBuilder displayItem = new ItemStackBuilder().
                material(Material.WHITE_WOOL)
                .color(arena.getArenaState().getData())
                .glowing(arena.isPrivateArena())
                .name(arena.getArenaState().getColor() + arena.getDisplayName())
                .lore(arena.getArenaState().getColor() + arena.getArenaState().getName(),
                        "",
                        ChatColor.WHITE + "Players: " + ChatColor.RED + arena.getPlayers().size() + "/" + arena.getMaxPlayers(),
                        "")
                .amount(arena.getPlayers().size());

        if(arena.getPhase().canJoin() && arena.canJoin(getPlayer())) {
            if(arena.isFull()) {
                displayItem.addLore(CoreLang.GUI_USER_JOIN_ARENA_ARENA_FULL.getMessage(getPlayer()));
            } else {
                displayItem.addLore(CoreLang.GUI_USER_JOIN_ARENA_CLICK_TO_JOIN.getMessage(getPlayer()));
            }
        } else if(arena.canJoinArena()) {
            displayItem.addLore(CoreLang.GUI_USER_JOIN_ARENA_CLICK_TO_SPECTATE.getMessage(getPlayer()));
        }

        return new SimpleButton(displayItem).action(() ->  {
            if(playerManager.getPlayer(player.getName()).getCurrentArena() != null) {
                player.sendMessage(CoreLang.ALREADY_IN_ARENA.getMessage(player));
                return;
            }

            if(!arena.isEnabled()) {
                player.sendMessage(CoreLang.ARENA_IS_NOT_PLAYABLE.getMessage(player));
                return;
            }

            if(arena.getPhase().canJoin() && arena.canJoin(player)) {
                if(arena.isFull()) {
                    player.sendMessage(CoreLang.ARENA_IS_FULL.getMessage(player));
                    return;
                }

                arena.addPlayer(player);
            } else if(arena.canJoinArena()) {
                arena.addSpectator(player);
            } else {
                player.sendMessage(CoreLang.ARENA_IS_RESTARTING.getMessage(player));
            }
        });
    }

    @Override
    public void run() {
        refresh();
    }

    @Override
    public void destroy() {
        task.cancel();
    }
}
