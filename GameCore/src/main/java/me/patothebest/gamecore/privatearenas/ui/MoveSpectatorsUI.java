package me.patothebest.gamecore.privatearenas.ui;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.page.DualListPage;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.button.ButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

public class MoveSpectatorsUI extends DualListPage<Player> {

    private final AbstractArena arena;
    private final ButtonAction backAction;

    protected MoveSpectatorsUI(Plugin plugin, Player player, AbstractArena arena, ButtonAction backAction) {
        super(plugin, player, "", arena::getPlayers, arena::getSpectators);
        this.arena = arena;
        this.backAction = backAction;
        build();
    }

    @Override
    protected void buildHeader() {
        addButton(new PlaceHolder(new ItemStackBuilder()
                        .material(Material.WHITE_WOOL)
                        .name(ChatColor.GRAY + CoreLang.SPECTATOR.getMessage(getPlayer()))),
                4);
    }

    @Override
    protected GUIButton createLeftButton(Player player) {
        ItemStackBuilder item = new ItemStackBuilder().skullOwner(player.getName()).name(ChatColor.GREEN + player.getName());
        AbstractGameTeam teamPreference = arena.getTeamPreference(player);
        if (teamPreference != null) {
            item.lore(CoreLang.GUI_PRIVATE_ARENA_CURRENT_TEAM.replace(getPlayer(), teamPreference.getName()));
        } else {
            item.lore(CoreLang.GUI_PRIVATE_ARENA_NO_CURRENT_TEAM.getMessage(getPlayer()));
        }

        item.lore("", CoreLang.GUI_PRIVATE_ARENA_CLICK_TO_CHANGE_SPEC.getMessage(getPlayer()));
        return new SimpleButton(item).action(() -> {
            AbstractGameTeam otherTeam = arena.getTeamPreference(player);
            if(otherTeam != null) {
                arena.getTeamPreferences().get(otherTeam).remove(player);
            }

            if (!arena.getSpectators().contains(player)) {
                arena.changeToSpectator(player, false);
            }
            refresh();
        });
    }

    @Override
    protected GUIButton createRightButton(Player player) {
        ItemStackBuilder item = new ItemStackBuilder().skullOwner(player.getName()).name(ChatColor.GREEN + player.getName());
        item.lore(CoreLang.GUI_PRIVATE_ARENA_CURRENT_SPEC.getMessage(player),
                "",
                CoreLang.GUI_PRIVATE_ARENA_CLICK_TO_CHANGE_PLAYER.getMessage(getPlayer()));

        return new SimpleButton(item).action(() -> {
            if (arena.getSpectators().contains(player)) {
                arena.changeToPlayer(player);
            }
            refresh();
        });
    }

    @Override
    protected void buildFooter() {
        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer())).action(backAction), getSize()-9);
    }

    @EventHandler
    public void onLeave(ArenaLeaveEvent event) {
        refresh();
    }
}
