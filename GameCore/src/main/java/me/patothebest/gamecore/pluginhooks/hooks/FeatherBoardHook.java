package me.patothebest.gamecore.pluginhooks.hooks;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.event.player.LobbyJoinEvent;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.pluginhooks.PluginHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import javax.inject.Inject;

public class FeatherBoardHook extends PluginHook implements ListenerModule {

    private final CorePlugin plugin;

    private String leaveScoreboard;
    private String lobbyScoreboard;

    @Inject public FeatherBoardHook(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onHook(ConfigurationSection pluginHookSection) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        leaveScoreboard = pluginHookSection.getString("lobby");
        lobbyScoreboard = pluginHookSection.getString("pre-game");
    }

    @EventHandler
    public void onLeaveArena(ArenaLeaveEvent event) {
        showScoreboard(event.getPlayer(), leaveScoreboard);
    }

    @EventHandler
    public void onJoinLobby(LobbyJoinEvent event) {
        showScoreboard(event.getPlayer(), lobbyScoreboard);
    }

    private void showScoreboard(Player player, String scoreboardName) {
        /*if(scoreboardName == null || scoreboardName.isEmpty() || scoreboardName.equalsIgnoreCase("default")) {
            FeatherBoardAPI.resetDefaultScoreboard(player);
            return;
        }*/

        FeatherBoardAPI.showScoreboard(player, scoreboardName);
    }

    @Override
    public String getPluginName() {
        return "FeatherBoard";
    }
}