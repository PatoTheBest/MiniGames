package me.patothebest.gamecore.phase.phases;

import com.google.inject.Provider;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.event.player.GameJoinEvent;
import me.patothebest.gamecore.event.player.PlayerStateChangeEvent;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.phase.AbstractPhase;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.sign.SignManager;
import me.patothebest.gamecore.util.Callback;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class GamePhase<PluginType extends CorePlugin, Arena extends AbstractArena> extends AbstractPhase<PluginType, Arena> {

    private final Provider<NMS> nmsProvider;
    private final PlayerManager playerManager;
    private final SignManager signManager;
    private final UserGUIFactory userGUIFactory;

    public GamePhase(PluginType plugin, Provider<NMS> nmsProvider, SignManager signManager, PlayerManager playerManager, UserGUIFactory userGUIFactory) {
        super(plugin);
        this.nmsProvider = nmsProvider;
        this.signManager = signManager;
        this.playerManager = playerManager;
        this.userGUIFactory = userGUIFactory;
    }

    @Override
    public void playerJoin(Player player) {
        // checks if the player has the arena permission
        if (!arena.getPermissionGroup().hasPermission(player)) {
            // sends the no permission message to the player
            player.sendMessage(CoreLang.NO_PERMISSION_ARENA.getMessage(player));
            return;
        }

        Callback<Player> callback = (player2) -> {
            // add the player to the arena
            arena.getPlayers().add(player);

            // sets the arena of the player and saves the player state
            playerManager.getPlayer(player).setCurrentArena(arena);
            playerManager.getPlayer(player).getPlayerInventory().savePlayer();

            // gets a team for the player and add the player to the team
            AbstractGameTeam gameTeam = arena.getNewTeamForPlayer(player);
            gameTeam.addPlayer(player);
            gameTeam.giveStuff(player);

            // update the signs to display the new amount of players
            signManager.updateSigns();

            // call the PlayerJoinArenaEvent event
            callJoinEvent(player);
        };

        if (arena.getTeams().size() != 1 && player.hasPermission(Permission.CHOOSE_TEAM.getBukkitPermission())) {
            if (arena.hasLastTeam(player) && !player.hasPermission(Permission.CHOOSE_TEAM_OVERRIDE.getBukkitPermission())) {
                callback.call(player);
            } else {
                userGUIFactory.createTeamUI(player, arena, callback);
            }
        } else {
            callback.call(player);
        }
    }

    @Override
    public void start() {
        super.start();

        // iterate over each player
        // call the PlayerJoinArenaEvent event
        arena.getPlayers().forEach(player -> {
            callJoinEvent(player);
            playerManager.getPlayer(player).getGameTeam().giveStuff(player);
        });

        // update the signs to display the new arena state
        signManager.updateSigns();

        if (arena.getLobbyArea() != null) {
            ItemStackBuilder AIR = new ItemStackBuilder(Material.AIR);
            for (Block block : arena.getLobbyArea().getBlocks()) {
                if (block.getType() != Material.AIR) {
                    nmsProvider.get().setBlock(block, AIR);
                }
            }
        }
    }

    protected void callJoinEvent(Player player) {
        plugin.getServer().getPluginManager().callEvent(new GameJoinEvent(player, arena));
        plugin.getServer().getPluginManager().callEvent(new PlayerStateChangeEvent(player, arena, PlayerStateChangeEvent.PlayerState.PLAYER));
    }

    @Override
    public ArenaState getArenaState() {
        return ArenaState.IN_GAME;
    }
}
