package me.patothebest.gamecore.scoreboard;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.util.WrappedBukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomScoreboard extends WrappedBukkitRunnable {

    private final CorePlugin plugin;
    private final Player player;
    private final String name;
    private final ScoreboardEntry title;
    private final PlayerScoreboard playerScoreboard;
    private final Map<String, ScoreboardEntry> scoreboardEntryMap;
    private final WrappedBukkitRunnable tickRunnable;

    public CustomScoreboard(CorePlugin plugin, Player player, String name, ConfigurationSection data) {
        this.plugin = plugin;
        this.player = player;
        this.name = name;
        this.playerScoreboard = new PlayerScoreboard(player);
        this.scoreboardEntryMap = new ConcurrentHashMap<>();

        final int[] size = {data.getConfigurationSection("content").getKeys(false).size()};
        data.getConfigurationSection("content").getKeys(false).forEach(s -> scoreboardEntryMap.put(s, new ScoreboardEntry(this, player, playerScoreboard.getOrCreateTeam(size[0]--), data.getConfigurationSection("content." + s).getValues(true))));
        title = new ScoreboardEntry(this, player, playerScoreboard, data.getConfigurationSection("title").getValues(true));
        playerScoreboard.update();

        this.tickRunnable = new WrappedBukkitRunnable() {
            @Override
            public void run() {
                title.tick();
                scoreboardEntryMap.values().forEach(ScoreboardEntry::tick);
            }
        };
    }

    public void show() {
        title.prepare();
        scoreboardEntryMap.values().forEach(ScoreboardEntry::prepare);
        tickRunnable.runTaskTimerAsynchronously(plugin, 1L, 1L);
        runTaskTimer(plugin, 1L, 1L);
        playerScoreboard.show();
    }

    @Override
    public void run() {
        playerScoreboard.update();
    }

    public void destroy() {
        if(hasBeenScheduled()) {
           cancel();
           tickRunnable.cancel();
        }

        playerScoreboard.destroy();
        scoreboardEntryMap.clear();
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        tickRunnable.cancel();

        if(player.getScoreboard() == playerScoreboard.getScoreboard()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public Map<String, ScoreboardEntry> getScoreboardEntryMap() {
        return scoreboardEntryMap;
    }
}
