package me.patothebest.thetowers.guis.setup;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.thetowers.TheTowersRemastered;
import me.patothebest.thetowers.guis.setup.edit.EditArenaUI;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ChooseArenaToEditGUI extends GUIMultiPage {

    public ChooseArenaToEditGUI(Plugin plugin, Player player) {
        super(plugin, player, Lang.GUI_CHOOSE_ARENA_TITLE.getMessage(player), 54);
        build();
    }

    @Override
    protected void buildContent() {
        final int[] slot = {0};

        ((TheTowersRemastered)plugin).getArenaManager().getArenas().values().stream().skip(currentPage*pageSize).limit(pageSize).forEach(arena -> {
            ItemStack item = new ItemStackBuilder().material(Material.MAP).name(Lang.GUI_CHOOSE_ARENA_EDIT_ITEM.getMessage(getPlayer()).replace("%arena%", arena.getName()));
            addButton(new SimpleButton(item, () -> new EditArenaUI(plugin, player, (Arena)arena)), slot[0]);
            slot[0]++;
        });
    }

    @Override
    protected int getListCount() {
        return ((TheTowersRemastered)plugin).getArenaManager().getArenas().size();
    }

}
