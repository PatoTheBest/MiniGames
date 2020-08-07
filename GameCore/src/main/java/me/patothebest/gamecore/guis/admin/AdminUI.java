package me.patothebest.gamecore.guis.admin;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.feature.features.other.CountdownFeature;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AdminUI extends GUIPage {

    private final AbstractArena arena;

    public AdminUI(CorePlugin plugin, Player player, AbstractArena arena) {
        super(plugin, player, CoreLang.GUI_ADMIN_TITLE.getMessage(player), 9);
        this.arena = arena;
        build();
    }

    @Override
    protected void buildPage() {
        CountdownFeature countdown = arena.getFeature(CountdownFeature.class);

        ItemStackBuilder start = new ItemStackBuilder().material(Material.EMERALD_BLOCK).name(getPlayer(), CoreLang.GUI_ADMIN_START_COUNTDOWN);
        ItemStackBuilder enabled = new ItemStackBuilder().createTogglableItem(getPlayer(), countdown.isOverrideRunning()).lore(ChatColor.GRAY + "Countdown override");
        ItemStackBuilder stop = new ItemStackBuilder().material(Material.REDSTONE_BLOCK).name(getPlayer(), CoreLang.GUI_ADMIN_STOP_COUNTDOWN);

        addButton(new SimpleButton(start).action(countdown::startCountdown), 1);
        addButton(new SimpleButton(enabled).action(() -> {
            countdown.setOverrideRunning(!countdown.isOverrideRunning());
            refresh();
        }), 4);
        addButton(new SimpleButton(stop).action(() -> countdown.setRunning(false)), 7);
    }

    interface Factory {

        void create(Player player, AbstractArena arena);

    }
}
