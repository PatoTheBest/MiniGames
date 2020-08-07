package me.patothebest.gamecore.feature.features.chests.regen;

import org.bukkit.block.Chest;

class NoRegenChest extends RegenChest {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //
    // This class represents the other half of a large
    // chest, which will be regenerated automatically
    // with the RegenChest class, so there is no need
    // to regen this side again

    NoRegenChest(Chest chest) {
        super(chest);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    void regen() {
        // do nothing
    }
}
