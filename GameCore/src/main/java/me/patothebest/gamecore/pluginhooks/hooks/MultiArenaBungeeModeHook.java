package me.patothebest.gamecore.pluginhooks.hooks;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.modes.bungee.BungeeMode;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.event.arena.ArenaLoadEvent;
import me.patothebest.gamecore.event.arena.ArenaUnLoadEvent;
import me.patothebest.gamecore.pluginhooks.PluginHook;
import net.megaplanet.multiarenabungeemode.IArena;
import net.megaplanet.multiarenabungeemode.MultiArenaBungeeMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MultiArenaBungeeModeHook extends PluginHook implements Listener {

    private final ArenaManager arenaManager;
    private final CorePlugin plugin;
    private final BungeeMode bungeeMode;

    @Inject private MultiArenaBungeeModeHook(ArenaManager arenaManager, CorePlugin plugin, BungeeMode bungeeMode) {
        this.arenaManager = arenaManager;
        this.plugin = plugin;
        this.bungeeMode = bungeeMode;
    }

    @Override
    protected void onHook(ConfigurationSection pluginHookSection) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        for (AbstractArena abstractArena : arenaManager.getArenas().values()) {
            MultiArenaBungeeMode.registerArena(abstractArena.getName(), new ArenaWrapper(abstractArena));
        }
    }

    @EventHandler
    public void onArenaLoad(ArenaLoadEvent event) {
        MultiArenaBungeeMode.registerArena(event.getArena().getName(), new ArenaWrapper(event.getArena()));
    }

    @EventHandler
    public void onArenaUnLoad(ArenaUnLoadEvent event) {
        MultiArenaBungeeMode.unregisterArena(event.getArena().getName());
    }

    @Override
    public String getPluginName() {
        return "MultiArenaBungeeMode";
    }

    private class ArenaWrapper implements IArena {

        private final AbstractArena arena;

        private ArenaWrapper(AbstractArena arena) {
            this.arena = arena;
        }

        @Override
        public String getName() {
            return arena.getName();
        }

        @Override
        public void queuePlayerToJoin(String playerName) {
            bungeeMode.getPlayerQueue().put(playerName, arena);
        }

        @Override
        public int getMaxPlayers() {
            return arena.getMaxPlayers();
        }

        @Override
        public int getPlayers() {
            return arena.getPlayers().size();
        }

        @Override
        public String getStatus() {
            return arena.getArenaState().getName();
        }

        @Override
        public boolean canJoin() {
            return arena.getPhase().canJoin();
        }
    }
}