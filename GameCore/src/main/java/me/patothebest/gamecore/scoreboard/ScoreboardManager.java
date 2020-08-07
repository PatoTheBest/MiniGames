package me.patothebest.gamecore.scoreboard;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.pluginhooks.PluginHookManager;
import me.patothebest.gamecore.pluginhooks.hooks.FeatherBoardHook;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.event.player.GameJoinEvent;
import me.patothebest.gamecore.event.player.LobbyJoinEvent;
import me.patothebest.gamecore.event.player.PlayerJoinPrepareEvent;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ReloadPriority;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Priority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Singleton
@ReloadPriority(priority = Priority.LOW)
@ModuleName("Scoreboard Manager")
public class ScoreboardManager implements Listener, ActivableModule, ReloadableModule {

    private final CorePlugin plugin;
    private final PluginHookManager pluginHookManager;
    private final PlayerManager playerManager;
    private final ScoreboardFile scoreboardFile;
    private final EventRegistry eventRegistry;

    @Inject private ScoreboardManager(CorePlugin plugin, PluginHookManager pluginHookManager, PlayerManager playerManager, ScoreboardFile scoreboardFile, EventRegistry eventRegistry) {
        this.plugin = plugin;
        this.pluginHookManager = pluginHookManager;
        this.playerManager = playerManager;
        this.scoreboardFile = scoreboardFile;
        this.eventRegistry = eventRegistry;
    }

    @Override
    public void onPostEnable() {
        if(pluginHookManager.isHookLoaded(FeatherBoardHook.class)) {
            return;
        }

        eventRegistry.registerListener(this);
    }

    @Override
    public void onReload() {
        scoreboardFile.load();
    }

    @Override
    public String getReloadName() {
        return "scoreboards";
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrepare(PlayerJoinPrepareEvent event) {
        event.getPlayer().getScoreboards().put(CoreScoreboardType.LOBBY, new CustomScoreboard(plugin, event.getPlayer().getPlayer(), "lobby", scoreboardFile.getConfigurationSection("lobby-scoreboard")));
        event.getPlayer().getScoreboards().put(CoreScoreboardType.WAITING, new CustomScoreboard(plugin, event.getPlayer().getPlayer(), "waiting", scoreboardFile.getConfigurationSection("waiting-scoreboard")));
        event.getPlayer().show(event.getPlayer().getScoreboardToShow());
    }

    @EventHandler
    public void onLeaveArena(ArenaLeaveEvent event) {
        playerManager.getPlayer(event.getPlayer()).show(CoreScoreboardType.LOBBY);

        if(playerManager.getPlayer(event.getPlayer()).getScoreboards().containsKey(CoreScoreboardType.GAME)) {
            playerManager.getPlayer(event.getPlayer()).destroy(CoreScoreboardType.GAME);
        }
    }

    @EventHandler
    public void onJoinLobby(LobbyJoinEvent event) {
        playerManager.getPlayer(event.getPlayer()).show(CoreScoreboardType.WAITING);
    }

    @EventHandler
    public void onJoinGame(GameJoinEvent event) {
        playerManager.getPlayer(event.getPlayer()).getScoreboards().put(CoreScoreboardType.GAME, new CustomScoreboard(plugin, event.getPlayer().getPlayer(), "game", scoreboardFile.getConfigurationSection("game-scoreboard")));
        playerManager.getPlayer(event.getPlayer()).show(CoreScoreboardType.GAME);
    }
}