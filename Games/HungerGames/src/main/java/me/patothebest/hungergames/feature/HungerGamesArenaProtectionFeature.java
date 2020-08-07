package me.patothebest.hungergames.feature;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.feature.features.protection.GameProtectionFeature;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class HungerGamesArenaProtectionFeature extends GameProtectionFeature {

    private final Set<org.bukkit.Material> breakableBlocks = new HashSet<>();

    @Inject protected HungerGamesArenaProtectionFeature(CoreConfig config, PlayerManager playerManager) {
        super(playerManager);

        List<Material> materials = new ArrayList<>();
        List<String> breakableBlocksConfig = config.getStringList("breakable-blocks");
        for (String materialName : breakableBlocksConfig) {
            Optional<Material> optionalMaterial = Material.matchMaterial(materialName);
            if (optionalMaterial.isPresent()) {
                Material material = optionalMaterial.get();
                if (!materials.contains(material)) {
                    materials.add(material);
                }
            }
        }

        for (Material material : materials) {
            if (material.isSupported()) {
                org.bukkit.Material bukkitMaterial = material.parseMaterial(false);
                if (bukkitMaterial != null) {
                    breakableBlocks.add(bukkitMaterial);
                }
            }
        }
    }

    @Override
    protected boolean isBlockProtected(AbstractArena arena, Block block, Event event) {
        if (super.isBlockProtected(arena, block, event)) {
            return true;
        }

        if (!(event instanceof BlockBreakEvent) && !(event instanceof BlockPlaceEvent)) {
            return false;
        }

        return !breakableBlocks.contains(block.getType());
    }
}
