package me.patothebest.thetowers.guis.setup.edit;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.GameTeam;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.vector.Cuboid;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class EditPointArea extends GUIPage {

    private final Arena arena;
    private final GameTeam gameTeam;
    private final String pointName;
    private final Cuboid cuboid;

    EditPointArea(Plugin plugin, Player player, Arena arena, GameTeam gameTeam, String pointName, Cuboid cuboid) {
        super(plugin, player, Lang.GUI_EDIT_POINT_AREA_TITLE.getMessage(player), 9);
        this.arena = arena;
        this.gameTeam = gameTeam;
        this.pointName = pointName;
        this.cuboid = cuboid;
        build();
    }

    @Override
    public void buildPage() {
        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), () -> new ChoosePointArea(plugin, player, arena, gameTeam)), 0);
        addButton(new PlaceHolder(new ItemStackBuilder().material(Material.PAPER).name(ChatColor.YELLOW + pointName).lore(ChatColor.GRAY + cuboid.toString())), 4);
        addButton(new SimpleButton(new ItemStackBuilder().material(Material.BARRIER).name(Lang.GUI_EDIT_POINT_AREA_DELETE.getMessage(getPlayer())), () -> {
            gameTeam.getPointAreas().remove(pointName);
            arena.save();
            new ChoosePointArea(plugin, player, arena, gameTeam);
        }), 8);
    }

}