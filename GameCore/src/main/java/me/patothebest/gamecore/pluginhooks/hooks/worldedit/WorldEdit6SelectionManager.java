package me.patothebest.gamecore.pluginhooks.hooks.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldEdit6SelectionManager implements SelectionManager {

    private final WorldEditPlugin worldEdit;

    public WorldEdit6SelectionManager(WorldEditPlugin worldEdit) throws ClassNotFoundException {
        Class.forName("com.sk89q.worldedit.Vector");
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

        Object minPoint = Utils.invokeMethod(selection, "getMinimumPoint", null);
        Object maxPoint = Utils.invokeMethod(selection, "getMaximumPoint", null);
        Vector minimumPoint = (Vector) minPoint;
        Vector maximumPoint = (Vector) maxPoint;
        return new Selection().setPointA(toLocation(player.getWorld(), maximumPoint)).setPointB(toLocation(player.getWorld(), minimumPoint));
    }

    private Location toLocation(World world, Vector point) {
        return new Location(world, point.getBlockX(), point.getBlockY(), point.getBlockZ());
    }
}
