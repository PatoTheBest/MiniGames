package me.patothebest.thetowers.guis.setup.edit;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.GameTeam;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

class EditTeamUI extends GUIPage {

    private final Arena arena;
    private final GameTeam team;

    EditTeamUI(Plugin plugin, Player player, Arena arena, GameTeam team) {
        super(plugin, player, Lang.GUI_EDIT_TEAM_TITLE.getMessage(player), 9);
        this.arena = arena;
        this.team = team;
        build();
    }

    @Override
    public void buildPage() {
        ItemStack backItem = new ItemStackBuilder().createBackItem(getPlayer());
        ItemStack setSpawn = new ItemStackBuilder().material(Material.WHITE_BED).name(Lang.GUI_EDIT_TEAM_SET_SPAWN.getMessage(getPlayer()));
        ItemStack pointAreas = new ItemStackBuilder().material(Material.NETHER_STAR).name(Lang.GUI_EDIT_TEAM_POINT_AREAAS.getMessage(getPlayer()));
        ItemStack barrier = new ItemStackBuilder().material(Material.BARRIER).name(Lang.GUI_EDIT_TEAM_DELETE.getMessage(getPlayer()));

        addButton(new SimpleButton(backItem, () -> new ChooseTeamUI(plugin, player, arena)), 0);
        addButton(new SimpleButton(setSpawn, () -> {
            team.setSpawn(player.getLocation());
            arena.save();
            player.sendMessage(CoreLang.TEAM_SPAWN_SET.getMessage(getPlayer()));
        }), 3);

        addButton(new SimpleButton(pointAreas, () -> new ChoosePointArea(plugin, player, arena, team)), 5);
        addButton(new SimpleButton(barrier, () -> {
            arena.removeGameTeam(team.getName());
            arena.save();
            player.sendMessage(Lang.GUI_EDIT_TEAM_DELETED.getMessage(getPlayer()));
            new ChooseTeamUI(plugin, player, arena);
        }), 8);
    }

}