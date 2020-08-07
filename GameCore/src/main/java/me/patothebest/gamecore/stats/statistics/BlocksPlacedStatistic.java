package me.patothebest.gamecore.stats.statistics;

import me.patothebest.gamecore.stats.AbstractStatistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlocksPlacedStatistic extends AbstractStatistic {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if(!getPlayer(player).isInArena()) {
            return;
        }

        updateStat(player, 1);
    }

    @Override
    public String getStatName() {
        return "blocks_placed";
    }
}
