package me.patothebest.arcade.game.scoreboard;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.arcade.game.GameType;
import me.patothebest.arcade.Arcade;
import me.patothebest.arcade.game.helpers.EndGamePhase;
import me.patothebest.arcade.game.helpers.IntroduceGamePhase;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.event.arena.ArenaPhaseChangeEvent;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.event.player.GameJoinEvent;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.pluginhooks.PluginHookManager;
import me.patothebest.gamecore.pluginhooks.hooks.FeatherBoardHook;
import me.patothebest.gamecore.scoreboard.CustomScoreboard;
import me.patothebest.gamecore.scoreboard.ScoreboardFile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Singleton
@ModuleName("Arcade Scoreboard Manager")
public class ArcadeScoreboardManager implements Listener, ActivableModule, ReloadableModule {

    private final Arcade plugin;
    private final ScoreboardFile scoreboardFile;
    private final PlayerManager playerManager;
    private final PluginHookManager pluginHookManager;
    private final EventRegistry eventRegistry;

    @Inject private ArcadeScoreboardManager(Arcade plugin, ScoreboardFile scoreboardFile, PlayerManager playerManager, PluginHookManager pluginHookManager, EventRegistry eventRegistry) {
        this.plugin = plugin;
        this.scoreboardFile = scoreboardFile;
        this.playerManager = playerManager;
        this.pluginHookManager = pluginHookManager;
        this.eventRegistry = eventRegistry;
    }

    @Override
    public void onPreEnable() {
        for (ArcadeScoreboardType scoreboardType : ArcadeScoreboardType.values()) {
            scoreboardType.setEnabled(scoreboardFile.getBoolean(scoreboardType.getConfigName() + ".enabled"));
        }
    }

    @Override
    public void onPostEnable() {
        if(pluginHookManager.isHookLoaded(FeatherBoardHook.class)) {
            return;
        }

        eventRegistry.registerListener(this);
    }

    @EventHandler
    public void onJoinGame(GameJoinEvent event) {
        for (ArcadeScoreboardType scoreboardType : ArcadeScoreboardType.values()) {
            playerManager.getPlayer(event.getPlayer()).getScoreboards().put(scoreboardType,
                    new CustomScoreboard(plugin,
                            event.getPlayer().getPlayer(),
                            scoreboardType.name(),
                            scoreboardFile.getConfigurationSection(scoreboardType.getConfigName())));
        }
    }

    @EventHandler
    public void onPhaseChange(ArenaPhaseChangeEvent event) {
        if (event.getNewPhase() instanceof IntroduceGamePhase) {
            GameType gameType = ((IntroduceGamePhase) event.getNewPhase()).getGame().getGameType();
            for (Player player : event.getArena().getPlayers()) {
                playerManager.getPlayer(player).show(gameType.getScoreboardType());
            }
            for (Player player : event.getArena().getSpectators()) {
                playerManager.getPlayer(player).show(gameType.getScoreboardType());
            }
        }

        if (event.getNewPhase() instanceof EndGamePhase) {
            for (Player player : event.getArena().getPlayers()) {
                playerManager.getPlayer(player).show(ArcadeScoreboardType.STAR_SUMMARY);
            }
            for (Player player : event.getArena().getSpectators()) {
                playerManager.getPlayer(player).show(ArcadeScoreboardType.STAR_SUMMARY);
            }
        }
    }

    @EventHandler
    public void onLeaveArena(ArenaLeaveEvent event) {
        IPlayer player = playerManager.getPlayer(event.getPlayer());

        for (ArcadeScoreboardType value : ArcadeScoreboardType.values()) {
            if (player.getScoreboards().containsKey(value)) {
                player.getScoreboards().remove(value).destroy();
            }
        }
    }

    @Override
    public void onReload() {
        onPreEnable();
        onPostEnable();
    }

    @Override
    public String getReloadName() {
        return "arcade-board";
    }
}
