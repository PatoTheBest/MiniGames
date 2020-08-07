package me.patothebest.gamecore.privatearenas.ui;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.page.DynamicPaginatedUI;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.button.ButtonAction;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class SelectTeamUI extends DynamicPaginatedUI<AbstractGameTeam> {

    static final AbstractGameTeam SPEC_DUMMY = new AbstractGameTeam(null, "Spectators", DyeColor.GRAY) {};
    private final AbstractArena arena;
    private final ButtonAction backAction;
    private final ButtonAction hereAction;

    protected SelectTeamUI(Plugin plugin, Player player, AbstractArena arena, List<AbstractGameTeam> teams, ButtonAction backAction) {
        super(plugin, player, CoreLang.GUI_PRIVATE_ARENA_SELECT_TEAM, () -> teams, Math.min(54, Utils.transformToInventorySize(teams.size()) + 9));
        this.arena = arena;
        this.backAction = backAction;
        this.hereAction = () -> {
            new SelectTeamUI(plugin, player, arena, teams, backAction);
        };
        build();
    }

    @Override
    protected GUIButton createButton(AbstractGameTeam team) {
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder().material(Material.YELLOW_WOOL).color(team.getColor());
        if (team == SPEC_DUMMY) {
            itemStackBuilder.name(ChatColor.GRAY + CoreLang.SPECTATOR.getMessage(getPlayer()));
            return new SimpleButton(itemStackBuilder).action(() -> {
                new MoveSpectatorsUI(plugin, player, arena, hereAction);
            });
        } else {
            itemStackBuilder.name(Utils.getColorFromDye(team.getColor()) + team.getName());
            return new SimpleButton(itemStackBuilder).action(() -> {
                new MovePlayersUI(plugin, player, arena, team, hereAction);
            });
        }
    }

    @Override
    public void buildPage() {
        super.buildPage();
        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer())).action(backAction), getSize()-9);
    }
}
