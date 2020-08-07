package me.patothebest.gamecore.phase.phases;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.feature.features.other.CountdownFeature;
import me.patothebest.gamecore.feature.features.protection.PlayerProtectionFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorFeature;
import me.patothebest.gamecore.feature.features.spectator.SpectatorProtectionFeature;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.phase.AbstractPhase;
import me.patothebest.gamecore.privatearenas.PrivateArena;
import me.patothebest.gamecore.privatearenas.PrivateArenasManager;
import me.patothebest.gamecore.event.player.LobbyJoinEvent;
import me.patothebest.gamecore.feature.features.other.LobbyFeature;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.sign.SignManager;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class LobbyPhase extends AbstractPhase<CorePlugin, AbstractArena> {

    private final PrivateArenasManager privateArenasManager;
    private final PlayerManager playerManager;
    private final SignManager signManager;
    private int teamItemSlot = 4;

    @Inject public LobbyPhase(CorePlugin plugin, PrivateArenasManager privateArenasManager, PlayerManager playerManager, SignManager signManager) {
        super(plugin);
        this.privateArenasManager = privateArenasManager;
        this.playerManager = playerManager;
        this.signManager = signManager;
    }

    @Override
    public void configure() {
        registerFeature(CountdownFeature.class);
        registerFeature(LobbyFeature.class);
        registerFeature(PlayerProtectionFeature.class);
        registerFeature(SpectatorProtectionFeature.class);
        registerFeature(SpectatorFeature.class);

        setCanJoin(true);
    }

    @Override
    public void playerJoin(Player player) {
        // checks if the player has the arena permission
        if(!arena.getPermissionGroup().hasPermission(player)) {
            // sends the no permission message to the player
            player.sendMessage(CoreLang.NO_PERMISSION_ARENA.getMessage(player));
            return;
        }

        IPlayer iPlayer = playerManager.getPlayer(player);

        // add the player to the arena
        arena.getPlayers().add(player);

        // sets the arena of the player and saves the player state
        playerManager.getPlayer(player).setCurrentArena(arena);
        iPlayer.executeWhenFullyLoaded(player1 -> {
            player1.getPlayerInventory().savePlayer();
            player.getInventory().setItem(0, new ItemStackBuilder().material(Material.NETHER_STAR).name(player, CoreLang.LOBBY_CHOOSE_KIT));
            player.getInventory().setItem(teamItemSlot, new ItemStackBuilder().material(Material.LIGHT_BLUE_WOOL).name(player, CoreLang.LOBBY_CHOOSE_TEAM));
            player.getInventory().setItem(8, new ItemStackBuilder().material(Material.MAGMA_CREAM).name(player, CoreLang.LOBBY_LEAVE));

            if (!arena.isPrivateArena()) {
                if(player.hasPermission(Permission.ADMIN.getBukkitPermission())) {
                    player.getInventory().setItem(7, new ItemStackBuilder().material(Material.COMPARATOR).name(player, CoreLang.LOBBY_ADMIN_MENU));
                }
            } else {
                for (PrivateArena value : privateArenasManager.getPrivateArenaMap().values()) {
                    if (value.getArena() == arena) {
                        if (value.getCoHosts().contains(player.getName()) || player.hasPermission(Permission.ADMIN.getBukkitPermission())) {
                            player.getInventory().setItem(7, new ItemStackBuilder().material(Material.COMPARATOR).name(player, CoreLang.GUI_PRIVATE_ARENA_LOBBY_MENU));
                        }
                        break;
                    }
                }
            }

        });

        if(iPlayer.isFullyJoined()) {
            // teleport the player and give items
            player.teleport(arena.getLobbyLocation());
        } else {
            iPlayer.setJoinLocation(arena.getLobbyLocation());
        }

        // send the player joined message to the arena
        arena.sendMessageToArena(locale -> CoreLang.PLAYER_JOINED.getMessage(locale).replace("%player%", player.getName()).replace("%players%", arena.getPlayers().size() + "").replace("%max_players%", arena.getMaxPlayers() + ""));

        // add the player to the cooldown list
        // this prevents instantly leaving the arena if the slot selected
        // was the one with the leave item and the join method was by right
        // clicking a sign to join
        arena.getFeature(LobbyFeature.class).playerJoin(player);

        // call event
        iPlayer.executeWhenFullyLoaded(player1 -> {
            plugin.getServer().getPluginManager().callEvent(new LobbyJoinEvent(player, arena));
        });

        // update signs
        signManager.updateSigns();
    }

    /**
     * Sets the slot where the team item will be given to the player
     */
    public void setTeamItemSlot(int teamItemSlot) {
        this.teamItemSlot = teamItemSlot;
    }

    @Override
    public ArenaState getArenaState() {
        return ArenaState.WAITING;
    }
}
