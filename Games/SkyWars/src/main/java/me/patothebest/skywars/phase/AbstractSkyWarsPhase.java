package me.patothebest.skywars.phase;

import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.event.arena.ArenaPrePhaseChangeEvent;
import me.patothebest.gamecore.feature.features.other.LimitedTimePhaseFeature;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.phase.AbstractPhase;
import me.patothebest.gamecore.util.Sounds;
import me.patothebest.skywars.phase.phases.BorderShrinkSkyWarsPhase;
import me.patothebest.skywars.SkyWars;
import me.patothebest.skywars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbstractSkyWarsPhase extends AbstractPhase<SkyWars, Arena> implements SkyWarsPhase<Arena>, Listener {

    private int phaseTime;
    private BukkitTask bukkitTask;

    public AbstractSkyWarsPhase(SkyWars plugin) {
        super(plugin);
    }

    @Override
    public void start() {
        ((LimitedTimePhaseFeature)getFeatures().get(LimitedTimePhaseFeature.class)).setTimeUntilNextStage(phaseTime);
        super.start();
        if (arena.getNextPhase() instanceof BorderShrinkSkyWarsPhase) {
            bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                arena.sendMessageToArena(CoreLang.BORDER_SHRINK_SOON_CHAT::getMessage);
                arena.sendTitleToArena((player, titleBuilder) -> {
                    titleBuilder.withTitle(CoreLang.BORDER_SHRINK_SOON_SUBTITLE.getMessage(player))
                            .withFadeInTime(1)
                            .withStayTime(2)
                            .withFadeOutTime(1);
                });
                arena.playSound(Sounds.BLOCK_NOTE_BLOCK_PLING);
            }, (phaseTime - 30) * 20);
            plugin.registerListener(this);
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onPhasePreChange(ArenaPrePhaseChangeEvent event) {
        if (event.getArena() != arena) {
            return;
        }

        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
            HandlerList.unregisterAll(this);
        }
    }

    public void setTimeTilNextPhase(int phaseTime) {
        this.phaseTime = phaseTime;
    }

    @Override
    public ArenaState getArenaState() {
        return ArenaState.IN_GAME;
    }
}
