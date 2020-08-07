package me.patothebest.thetowers.guis.setup.edit;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.GameTeam;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.vector.Cuboid;
import me.patothebest.thetowers.TheTowersRemastered;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class ChoosePointArea extends GUIMultiPage {

    private final Arena arena;
    private final GameTeam gameTeam;

    ChoosePointArea(Plugin plugin, Player player, Arena arena, GameTeam gameTeam) {
        super(plugin, player, Lang.GUI_CHOOSE_POINT_AREA_TITLE.getMessage(player), 54);
        this.arena = arena;
        this.gameTeam = gameTeam;
        build();
    }

    @Override
    protected void buildContent() {
        final int[] i = {0};
        gameTeam.getPointAreas().entrySet().stream().skip(currentPage*pageSize).limit(pageSize).forEach(stringCuboidEntry -> {
            String s = stringCuboidEntry.getKey();
            Cuboid cuboid = stringCuboidEntry.getValue();
            addButton(new SimpleButton(new ItemStackBuilder().material(Material.NETHER_STAR).name(ChatColor.GOLD + s).lore(ChatColor.GRAY + cuboid.toString()), () -> new EditPointArea(plugin, player, arena, gameTeam, s, cuboid)), i[0]);
            i[0]++;
        });

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), () -> new EditTeamUI(plugin, player, arena, gameTeam)), 47);
        addButton(new AnvilButton(new ItemStackBuilder().material(Material.DRAGON_EGG).name(Lang.GUI_CHOOSE_POINT_AREA_CREATE.getMessage(getPlayer())), new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                Selection selection = ((TheTowersRemastered)plugin).getSelectionManager().getSelection(player);
                if(selection == null || !selection.arePointsSet()) {
                    player.sendMessage(CoreLang.SELECT_AN_AREA.getMessage(player));
                    return;
                }

                Cuboid cuboid = selection.toCubiod(output, arena);
                gameTeam.addArea(cuboid);
                player.sendMessage(CoreLang.TEAM_POINT_AREA_SET.getMessage(player));
                arena.save();
                new EditPointArea(plugin, player, arena, gameTeam, cuboid.getName(), cuboid);
            }

            @Override
            public void onCancel() {
                new ChoosePointArea(plugin, player, arena, gameTeam);
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.NETHER_STAR).name("Area")), 51);
    }

    @Override
    protected int getListCount() {
        return gameTeam.getPointAreas().size();
    }

}