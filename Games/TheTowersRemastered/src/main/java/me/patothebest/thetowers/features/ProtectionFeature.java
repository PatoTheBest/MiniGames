package me.patothebest.thetowers.features;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.feature.features.protection.GameProtectionFeature;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.Cuboid;
import me.patothebest.thetowers.arena.Arena;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public class ProtectionFeature extends GameProtectionFeature {

    private final List<org.bukkit.Material> whitelistedMaterials;

    @Inject private ProtectionFeature(PlayerManager playerManager) {
        super(playerManager);
        this.whitelistedMaterials = new ArrayList<>();

        whitelistedMaterials.add(Material.CHEST.parseMaterial());
        whitelistedMaterials.add(Material.TRAPPED_CHEST.parseMaterial());
        whitelistedMaterials.add(Material.CRAFTING_TABLE.parseMaterial());
        whitelistedMaterials.add(Material.BOOKSHELF.parseMaterial());
        whitelistedMaterials.add(Material.ANVIL.parseMaterial());
        whitelistedMaterials.add(Material.ENCHANTING_TABLE.parseMaterial());
        whitelistedMaterials.add(Material.ARMOR_STAND.parseMaterial());
        whitelistedMaterials.add(Material.BREWING_STAND.parseMaterial());
        whitelistedMaterials.add(Material.FURNACE.parseMaterial());
        whitelistedMaterials.add(Material.CAKE.parseMaterial());
        whitelistedMaterials.add(Material.JUKEBOX.parseMaterial());
        whitelistedMaterials.add(Material.NOTE_BLOCK.parseMaterial());
        whitelistedMaterials.add(Material.ITEM_FRAME.parseMaterial());
        for (Material value : Material.values()) {
            if (value.name().contains("SIGN")) {
                whitelistedMaterials.add(value.parseMaterial());
            }
        }
    }

    @Override
    protected boolean isBlockProtected(AbstractArena arena, Block block, Event event) {
        if(super.isBlockProtected(arena, block, event)) {
            return true;
        }

        for(AbstractGameTeam gameTeam : arena.getTeams().values()) {
            if(Utils.isLocationInRadius(gameTeam.getSpawn(), block.getLocation(), 4)) {
                return true;
            }
        }

        if(whitelistedMaterials.contains(block.getType())) {
            return false;
        }

        for (Cuboid cuboid : ((Arena)arena).getProtectedAreas()) {
            if(cuboid.contains(block)) {
                return true;
            }
        }

        return false;
    }
}
