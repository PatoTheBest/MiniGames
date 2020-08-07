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
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class MovePlayersUI extends DualListPage<Player> {

    private final AbstractArena arena;
    private final AbstractGameTeam gameTeam;
    private final ButtonAction backAction;

    protected MovePlayersUI(Plugin plugin, Player player, AbstractArena arena, AbstractGameTeam gameTeam, ButtonAction backAction) {
        super(plugin, player, "", () -> {
            List<Player> players = new ArrayList<>(arena.getPlayers());
            players.addAll(arena.getSpectators());
            List<Player> preferencePlayers = arena.getTeamPreferences().get(gameTeam);
            if (preferencePlayers != null) {
                players.removeAll(preferencePlayers);
            }
            return players;
        }, () -> {
            List<Player> players = new ArrayList<>(gameTeam.getPlayers());
            List<Player> preferencePlayers = arena.getTeamPreferences().get(gameTeam);
            if (preferencePlayers != null) {
                players.addAll(preferencePlayers);
            }
            return players;
        });
        this.arena = arena;
        this.gameTeam = gameTeam;
        this.backAction = backAction;
        build();
    }

    @Override
    protected void buildHeader() {
        addButton(new PlaceHolder(new ItemStackBuilder()
                .material(Material.WHITE_WOOL)
                .name(Utils.getColorFromDye(gameTeam.getColor()) + gameTeam.getName())),
                4);
    }

    @Override
    protected GUIButton createLeftButton(Player player) {
        ItemStackBuilder item = new ItemStackBuilder().skullOwner(player.getName()).name(ChatColor.GREEN + player.getName());
        if (arena.getPlayers().contains(player)) {
            AbstractGameTeam teamPreference = arena.getTeamPreference(player);
            if (teamPreference != null) {
                item.lore(CoreLang.GUI_PRIVATE_ARENA_CURRENT_TEAM.replace(getPlayer(), teamPreference.getName()));
            } else {
                item.lore(CoreLang.GUI_PRIVATE_ARENA_NO_CURRENT_TEAM.getMessage(getPlayer()));
            }
        } else {
            item.lore(CoreLang.GUI_PRIVATE_ARENA_CURRENT_SPEC.getMessage(getPlayer()));
        }

        item.lore("", CoreLang.GUI_PRIVATE_ARENA_CLICK_TO_CHANGE_TEAM.replace(getPlayer(), gameTeam));
        return new SimpleButton(item).action(() -> {
            AbstractGameTeam otherTeam = arena.getTeamPreference(player);
            if(otherTeam != null) {
                arena.getTeamPreferences().get(otherTeam).remove(player);
            }

            if (arena.getSpectators().contains(player)) {
                arena.changeToPlayer(player);
            }

            List<Player> players = arena.getTeamPreferences().computeIfAbsent(gameTeam, k -> new ArrayList<>());
            players.add(player);
            arena.getTeamPreferences().putIfAbsent(gameTeam, players);
            player.sendMessage(CoreLang.GUI_USER_CHOOSE_TEAM_YOU_JOINED.replace(player, Utils.getColorFromDye(gameTeam.getColor()), gameTeam.getName()));
            refresh();
        });
    }

    @Override
    protected GUIButton createRightButton(Player player) {
        ItemStackBuilder item = new ItemStackBuilder().skullOwner(player.getName()).name(ChatColor.GREEN + player.getName());
        item.lore(CoreLang.GUI_PRIVATE_ARENA_CURRENT_TEAM.replace(getPlayer(), gameTeam.getName()),
                "",
                CoreLang.GUI_PRIVATE_ARENA_CLICK_TO_REMOVE_TEAM.replace(getPlayer(), gameTeam));

        return new SimpleButton(item).action(() -> {
            AbstractGameTeam otherTeam = arena.getTeamPreference(player);
            if(otherTeam != null) {
                arena.getTeamPreferences().get(otherTeam).remove(player);
                refresh();
            }
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
