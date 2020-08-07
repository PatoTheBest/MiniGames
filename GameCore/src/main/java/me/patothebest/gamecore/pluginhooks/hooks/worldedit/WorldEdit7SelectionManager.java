package me.patothebest.gamecore.pluginhooks.hooks.worldedit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

public class WorldEdit7SelectionManager implements SelectionManager {

    private final WorldEditPlugin worldEdit;

    public WorldEdit7SelectionManager(WorldEditPlugin worldEdit) {
        this.worldEdit = worldEdit;
    }

    @Override
    public Selection getSelection(Player player) {
        com.sk89q.worldedit.bukkit.selections.Selection selection;

        try {
            selection = (com.sk89q.worldedit.bukkit.selections.Selection) Utils.invokeMethod(worldEdit, "getSelection", new Class[]{Player.class}, player);
        } catch(Exception e) {
            return new Selection();
        }

        if(selection == null) {
            return new Selection();
        }

        return new Selection().setPointA(selection.getMaximumPoint()).setPointB(selection.getMinimumPoint());
    }
}
