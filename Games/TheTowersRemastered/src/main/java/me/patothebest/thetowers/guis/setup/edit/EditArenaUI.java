package me.patothebest.thetowers.guis.setup.edit;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.guis.setup.ChooseArenaToEditGUI;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.ConfirmButton;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.guis.grouppermissible.ChoosePermissionGroup;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.thetowers.TheTowersRemastered;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class EditArenaUI extends GUIPage {

    private final Arena arena;

    public EditArenaUI(Plugin plugin, Player player, Arena arena) {
        super(plugin, player, Lang.GUI_EDIT_ARENA_TITLE.getMessage(player).replace("%arena%", arena.getName()), 27);
        this.arena = arena;
        build();
    }

    @Override
    public void buildPage() {
        ItemStackBuilder backItem = new ItemStackBuilder().createBackItem(getPlayer());
        ItemStackBuilder gameTeams = new ItemStackBuilder().material(Material.WHITE_WOOL).name(getPlayer(), Lang.GUI_EDIT_ARENA_GAME_TEAMS);
        ItemStackBuilder permission = new ItemStackBuilder().material(Material.BLAZE_POWDER).name(getPlayer(), Lang.GUI_EDIT_ARENA_SET_PERMISSION);
        ItemStackBuilder players = new ItemStackBuilder().createPlayerHead().name(getPlayer(), Lang.GUI_EDIT_ARENA_PLAYERS);
        ItemStackBuilder teleportToArena = new ItemStackBuilder().material(Material.ENDER_PEARL).name(getPlayer(), Lang.GUI_EDIT_ARENA_TELEPORT);
        ItemStackBuilder dropper = new ItemStackBuilder().material(Material.DROPPER).name(getPlayer(), Lang.GUI_EDIT_ARENA_DROPPERS);
        ItemStackBuilder enabled = new ItemStackBuilder().createTogglableItem(getPlayer(), arena.isEnabled());
        ItemStackBuilder bed = new ItemStackBuilder().material(Material.WHITE_BED).name(getPlayer(), Lang.GUI_EDIT_ARENA_SET_LOBBY);
        ItemStackBuilder barrier = new ItemStackBuilder().material(Material.BARRIER).name(getPlayer(), Lang.GUI_EDIT_ARENA_DELETE);

        addButton(new SimpleButton(enabled, () -> {
            if(arena.isEnabled()) {
                arena.disableArena();
                player.sendMessage(CoreLang.ARENA_DISABLED.getMessage(player));
                refresh();
            } else {
                if(arena.canArenaBeEnabled(player)) {
                    arena.enableArena();
                    player.sendMessage(CoreLang.ARENA_ENABLED.getMessage(player));
                    arena.save();
                    refresh();
                }
            }
        }), 13);

        addButton(new SimpleButton(teleportToArena, () -> {
            player.closeInventory();
            player.teleport(arena.getWorld().getSpawnLocation());
            player.sendMessage(CoreLang.TELEPORTED_TO_ARENA.getMessage(player));
        }), 15);

        if(arena.isEnabled()) {
            addButton(new SimpleButton(players.lore(Lang.GUI_EDIT_ARENA_DISABLE_TO_ACCESS.getMessage(getPlayer()))), 3);
            addButton(new SimpleButton(permission.lore(Lang.GUI_EDIT_ARENA_DISABLE_TO_ACCESS.getMessage(getPlayer()))), 5);
            addButton(new SimpleButton(dropper.lore(Lang.GUI_EDIT_ARENA_DISABLE_TO_ACCESS.getMessage(getPlayer()))), 11);
            addButton(new SimpleButton(gameTeams.lore(Lang.GUI_EDIT_ARENA_DISABLE_TO_ACCESS.getMessage(getPlayer()))), 21);
            addButton(new SimpleButton(bed.lore(Lang.GUI_EDIT_ARENA_DISABLE_TO_ACCESS.getMessage(getPlayer()))), 23);
        } else {
            addButton(new SimpleButton(players, () -> new EditPlayersUI(plugin, player, arena)), 3);
            addButton(new SimpleButton(permission, () -> new ChoosePermissionGroup(plugin, player, arena, () -> new EditArenaUI(plugin, player, arena), ((TheTowersRemastered)plugin).getPermissionGroupManager())), 5);
            addButton(new SimpleButton(dropper, () -> new ChooseDropperUI(plugin, player, arena)), 11);
            addButton(new SimpleButton(gameTeams, () -> new ChooseTeamUI(plugin, player, arena)), 21);
            addButton(new SimpleButton(bed, () -> {
                arena.setLobbyLocation(player.getLocation());
                player.sendMessage(CoreLang.LOBBY_LOCATION_SET.getMessage(player));
            }), 23);
        }

        addButton(new SimpleButton(backItem, () -> new ChooseArenaToEditGUI(plugin, player)), 18);
        addButton(new ConfirmButton(barrier, barrier, new ItemStackBuilder().material(Material.MAP).name(ChatColor.GREEN + arena.getName())).action(() -> {
            arena.delete();
            ((TheTowersRemastered)plugin).getArenaManager().getArenas().remove(arena.getName());
            player.closeInventory();
            CoreLang.ARENA_DELETED.sendMessage(player);
        }), 26);
    }

}
