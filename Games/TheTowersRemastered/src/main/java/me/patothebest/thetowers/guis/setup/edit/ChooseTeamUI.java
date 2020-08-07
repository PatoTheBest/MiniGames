package me.patothebest.thetowers.guis.setup.edit;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.GameTeam;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.stream.Stream;

class ChooseTeamUI extends GUIPage {

    private final Arena arena;

    ChooseTeamUI(Plugin plugin, Player player, Arena arena) {
        super(plugin, player, Lang.GUI_CHOOSE_TEAM_TITLE.getMessage(player), 18);
        this.arena = arena;
        build();
    }

    @Override
    public void buildPage() {
        final int[] i = {0};
        Stream.of(DyeColor.values()).sorted((o1, o2) -> Boolean.compare(arena.getTeam(o1) == null, arena.getTeam(o2) == null)).forEach(dyeColor ->  {
            GameTeam team = (GameTeam) arena.getTeam(dyeColor);
            ItemStackBuilder itemStackBuilder = new ItemStackBuilder().material(Material.WHITE_WOOL).color(dyeColor);

            if(team == null) {
                itemStackBuilder.name(Lang.GUI_CHOOSE_TEAM_CREATE_TEAM.getMessage(getPlayer()).replace("%teamcolor%", Utils.getColorFromDye(dyeColor).toString()));
                addButton(new AnvilButton(itemStackBuilder, new AnvilButtonAction() {
                    @Override
                    public void onConfirm(String output) {
                        GameTeam gameTeam = new GameTeam(arena, output, dyeColor);
                        arena.addTeam(gameTeam);

                        new EditTeamUI(plugin, player, arena, gameTeam);
                        player.sendMessage(CoreLang.TEAM_CREATED.getMessage(getPlayer()));
                        arena.save();
                    }

                    @Override
                    public void onCancel() {
                        new ChooseTeamUI(plugin, player, arena);
                    }
                }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.WHITE_WOOL).color(dyeColor).name("Team")), i[0]);
            } else {
                itemStackBuilder.name(Lang.GUI_CHOOSE_TEAM_EDIT_TEAM.getMessage(getPlayer()).replace("%teamcolor%", Utils.getColorFromDye(dyeColor).toString()).replace("%teamname%", team.getName()));
                addButton(new SimpleButton(itemStackBuilder, () -> new EditTeamUI(plugin, player, arena, team)), i[0]);
            }
            i[0]++;
        });

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), () -> new EditArenaUI(plugin, player, arena)), 17);
    }

}
