package me.patothebest.gamecore.arena.option.options;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TNTExplosionOption extends EnableableOption {

    public TNTExplosionOption() {
        setEnabled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if(isEnabled()) {
           return;
        }

        if (event.getEntityType() != EntityType.PRIMED_TNT) {
            return;
        }

        if (!isEventInArena(event)) {
            return;
        }

        event.blockList().removeIf(block -> block.getType() != Material.TNT);
    }

    @Override
    public String getName() {
        return "tnt-explosions";
    }

    @Override
    public ILang getDescription() {
        return CoreLang.OPTION_TNT_EXPLOSIONS;
    }
}
