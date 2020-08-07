package me.patothebest.gamecore.privatearenas.ui;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import me.patothebest.gamecore.gui.inventory.button.ConfirmButton;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.privatearenas.PrivateArena;
import me.patothebest.gamecore.privatearenas.PrivateArenasManager;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.feature.features.other.CountdownFeature;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.button.ButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PrivateArenaUI extends GUIPage {

    private final PrivateArenasManager privateArenasManager;
    private final PrivateArena privateArena;
    private final PlayerManager playerManager;

    @AssistedInject private PrivateArenaUI(Plugin plugin, @Assisted Player player, PrivateArenasManager privateArenasManager, @Assisted PrivateArena privateArena, PlayerManager playerManager) {
        super(plugin, player, CoreLang.GUI_PRIVATE_ARENA_TITLE, 36);
        this.privateArenasManager = privateArenasManager;
        this.privateArena = privateArena;
        this.playerManager = playerManager;
        build();
    }

    @Override
    protected void buildPage() {
        CountdownFeature countdown = privateArena.getArena().getFeature(CountdownFeature.class);

        ItemStackBuilder head = new ItemStackBuilder().skullOwner(privateArena.getOwnerName()).name(CoreLang.GUI_PRIVATE_ARENA_HEAD.replace(getPlayer(), privateArena.getOwnerName()));
        ItemStackBuilder rename = new ItemStackBuilder().material(Material.NAME_TAG).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_RENAME);
        ItemStackBuilder teamsItem = new ItemStackBuilder().material(Material.LIGHT_BLUE_WOOL).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_MANAGE_TEAMS);
        ItemStackBuilder start = new ItemStackBuilder().material(Material.EMERALD_BLOCK).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_START_COUNTDOWN);
        ItemStackBuilder stop = new ItemStackBuilder().material(Material.REDSTONE_BLOCK).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_STOP_COUNTDOWN);
        ItemStackBuilder kickPlayers = new ItemStackBuilder().material(Material.DIAMOND_SWORD).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_KICK_PLAYERS);
        ItemStackBuilder changeMap = new ItemStackBuilder().material(Material.MAP).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_CHANGE_MAP);
        ItemStackBuilder giveCoHost = new ItemStackBuilder().material(Material.NETHER_STAR).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_ADD_CO_HOST);
        ItemStackBuilder removeCoHost = new ItemStackBuilder().material(Material.RED_DYE).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_REMOVE_CO_HOST);
        ItemStackBuilder addToWhiteList = new ItemStackBuilder().material(Material.WRITABLE_BOOK).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_ADD_PLAYERS_TO_WHITELIST);
        ItemStackBuilder removefromWhitelist = new ItemStackBuilder().material(Material.BOOK).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_REMOVE_PLAYERS_FROM_WHITELIST);
        ItemStackBuilder arenaOptions = new ItemStackBuilder().material(Material.COMPARATOR).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_OPTIONS);
        ItemStackBuilder close = new ItemStackBuilder().material(Material.BARRIER).name(getPlayer(), CoreLang.GUI_PRIVATE_ARENA_CLOSE);

        ButtonAction backAction = () -> {
            new PrivateArenaUI(plugin, player, privateArenasManager, privateArena, playerManager);
        };

        addButton(new PlaceHolder(head), 4);

        addButton(new AnvilButton(rename).action(new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                privateArena.getArena().setDisplayName(output);
                backAction.onClick();
            }

            @Override
            public void onCancel() {
                backAction.onClick();
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.NAME_TAG).name(privateArena.getArena().getDisplayName())), 11);

        addButton(new SimpleButton(teamsItem).action(() -> {
            List<AbstractGameTeam> teams = new ArrayList<>(privateArena.getArena().getTeams().values());
            teams.add(SelectTeamUI.SPEC_DUMMY);
            new SelectTeamUI(plugin, player, privateArena.getArena(), teams, backAction);
        }), 15);

        addButton(new SimpleButton(start).action(countdown::startCountdown), 18);
        addButton(new SimpleButton(stop).action(() -> countdown.setRunning(false)), 27);

        addButton(new SimpleButton(kickPlayers).action(() -> {
            new KickPlayerUI(plugin, getPlayer(), privateArena, kickPlayers, backAction);
        }), 20);

        addButton(new SimpleButton(changeMap).action(() -> {
            new ChangeMapUI(plugin, player, privateArena, privateArenasManager, backAction);
        }), 29);

        addButton(new SimpleButton(giveCoHost).action(() -> {
           new GiveCoHostUI(plugin, playerManager, player, privateArena, giveCoHost, backAction);
        }), 22);

        addButton(new SimpleButton(removeCoHost).action(() -> {
            new RemoveCoHostUI(plugin, playerManager, player, privateArena, removeCoHost, backAction);
        }), 31);

        addButton(new SimpleButton(addToWhiteList).action(() -> {
            new AddPlayerToWhitelistUI(plugin, player, privateArena, addToWhiteList, backAction);
        }), 24);

        addButton(new SimpleButton(removefromWhitelist).action(() -> {
            new RemovePlayerFromWhitelistUI(plugin, player, privateArena, removefromWhitelist, backAction);
        }), 33);

        addButton(new SimpleButton(arenaOptions).action(() -> {
            new ArenaOptionsUI(plugin, player, backAction, privateArena);
        }), 26);

        addButton(new ConfirmButton(close, close, close).action(() -> {
            privateArenasManager.removePrivateArena(privateArena);
        }), 35);
    }
}
