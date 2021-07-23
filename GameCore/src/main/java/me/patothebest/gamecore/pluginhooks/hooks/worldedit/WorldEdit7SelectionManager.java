package me.patothebest.gamecore.pluginhooks.hooks.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.selection.SelectionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldEdit7SelectionManager implements SelectionManager {

    private final WorldEditPlugin worldEdit;

    public WorldEdit7SelectionManager(WorldEditPlugin worldEdit) throws ClassNotFoundException {
        Class.forName("com.sk89q.worldedit.math.BlockVector3");
        this.worldEdit = worldEdit;
    }

    @Override
    public Selection getSelection(Player player) {
        Region selection;

        try {
            LocalSession session = worldEdit.getSession(player);
            selection = session.getSelection(session.getSelectionWorld());
        } catch(Exception e) {
            return new Selection();
        }

        if(selection == null) {
            return new Selection();
        }

        BlockVector3 maximumPoint = selection.getMaximumPoint();
        BlockVector3 minimumPoint = selection.getMinimumPoint();
        return new Selection().setPointA(toLocation(player.getWorld(), maximumPoint)).setPointB(toLocation(player.getWorld(), minimumPoint));
    }

    private Location toLocation(World world, BlockVector3 point) {
        return new Location(world, point.getBlockX(), point.getBlockY(), point.getBlockZ());
    }
}
