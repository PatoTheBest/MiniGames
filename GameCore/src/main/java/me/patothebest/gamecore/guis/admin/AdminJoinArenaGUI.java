package me.patothebest.gamecore.guis.admin;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.guis.AdminGUIFactory;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.stream.Collectors;

public class AdminJoinArenaGUI extends GUIPage {

    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;
    private final AdminGUIFactory adminGUIFactory;

    @Inject private AdminJoinArenaGUI(CorePlugin plugin, ArenaManager arenaManager, PlayerManager playerManager, @Assisted Player player, AdminGUIFactory adminGUIFactory) {
        super(plugin, player, "Arenas", ((int) Math.ceil((arenaManager.getArenas().values().stream().filter(AbstractArena::isEnabled)).count() / 9.0))*9);
        this.arenaManager = arenaManager;
        this.playerManager = playerManager;
        this.adminGUIFactory = adminGUIFactory;
        build();
    }

    @Override
    public void buildPage() {
        final int[] slot = {0};
        Comparator<AbstractArena> comparator = Comparator.comparingInt(arena2 -> -arena2.getPlayers().size());

        for (AbstractArena arena : arenaManager.getArenas().values().stream().filter(AbstractArena::isEnabled).sorted(comparator).collect(Collectors.toList())) {
            addButton(new SimpleButton(new ItemStackBuilder().material(Material.WHITE_WOOL).color(arena.getArenaState().getData()).name(arena.getArenaState().getColor() + arena.getWorldName()).lore(arena.getArenaState().getColor() + arena.getArenaState().getName(), "", ChatColor.WHITE + "Players: " + ChatColor.RED + arena.getPlayers().size() + "/" + arena.getMaxPlayers()).amount(arena.getPlayers().size())).action(() ->  {
                adminGUIFactory.createMenu(playerManager.getPlayer(player), arena);
            }), slot[0]);
            slot[0]++;
        }
    }
}
