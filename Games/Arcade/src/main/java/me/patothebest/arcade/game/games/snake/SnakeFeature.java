package me.patothebest.arcade.game.games.snake;

import com.google.inject.Inject;
import me.patothebest.arcade.Arcade;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.game.components.floor.FloorComponent;
import me.patothebest.gamecore.combat.CombatManager;
import me.patothebest.gamecore.event.player.PlayerStateChangeEvent;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.util.Sounds;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.Cuboid;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SnakeFeature extends AbstractRunnableFeature {

    private final Map<Player, SnakeNode> tails = new HashMap<>();
    private final Map<Player, List<Integer>> headBits = new HashMap<>(); // Map of player and List of entity ids
    private final List<Location> spawnableSlimeLocations = new ArrayList<>();

    private final Arcade plugin;
    private final CombatManager combatManager;
    private World world;
    private int ticks = 0;

    @Inject private SnakeFeature(Arcade plugin, CombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
    }

    @Override
    public void initializeFeature() {
        ticks = 0;
        int colorIndex = 0;
        this.world = arena.getWorld();

        Cuboid floor = ((Arena) arena).getCurrentGame().getComponent(FloorComponent.class).getFloor();
        for (Block block : floor.getBlocks()) {
            if (block.getRelative(BlockFace.UP).getType() == Material.AIR.parseMaterial()) {
                spawnableSlimeLocations.add(block.getRelative(BlockFace.UP).getLocation());
            }
        }

        runTaskTimer(plugin, 1L, 1L);
        for (Player player : arena.getPlayers()) {
            tails.put(player, new SnakeNode(player, null, DyeColor.values()[colorIndex++ % DyeColor.values().length]));
            headBits.put(player, new LinkedList<>());
            for (int i = 0; i <= 3; i++) {
                addTailPart(player);
            }
        }
    }

    @Override
    public void stopFeature() {
        tails.clear();
        headBits.clear();
        cancel();
    }

    private void addTailPart(Player player) {
        SnakeNode snakeNode = tails.get(player);
        Sheep sheep = world.spawn(snakeNode.entity.getLocation(), Sheep.class);
        sheep.setColor(snakeNode.color);
        Utils.setAiEnabled(sheep, false);
        tails.put(player, new SnakeNode(sheep, snakeNode, snakeNode.color));
        if (headBits.get(player).size() < 5) {
            headBits.get(player).add(sheep.getEntityId());
        }
    }

    @EventHandler
    public void onPlayerOut(PlayerStateChangeEvent event) {
        if (event.getPlayerState() != PlayerStateChangeEvent.PlayerState.SPECTATOR) {
            return;
        }

        SnakeNode tailPart = tails.remove(event.getPlayer());
        headBits.remove(event.getPlayer());

        while (tailPart != null) {
            if (!(tailPart.entity instanceof Player)) {
                tailPart.entity.remove();
            }
            tailPart = tailPart.next;
        }
    }

    @Override
    public void run() {
        // move
        for (Player player : arena.getPlayers()) {
            if (ticks % 3 != 0) {SnakeNode snakeNode = tails.get(player);
                while (snakeNode.next != null) {
                    Sheep sheep = (Sheep) snakeNode.entity;
                    sheep.teleport(snakeNode.next.entity.getLocation());
                    snakeNode = snakeNode.next;
                }
            }

            player.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(0.4));
        }

       ticks++;
       if (ticks < 40 || ticks % 3 != 0) {
           return;
       }

        // check collisions
        for (Player player : arena.getPlayers()) {
            List<Integer> snakeHead = headBits.get(player);
            for (Entity nearbyEntity : player.getNearbyEntities(0.5, 0.5, 0.5)) {
                if (snakeHead.contains(nearbyEntity.getEntityId())) {
                    continue;
                }

                if (nearbyEntity.getType() == EntityType.SHEEP) {
                    //((Sheep)nearbyEntity).setBaby();
                    combatManager.killPlayer(player, false);
                }

                if (nearbyEntity.getType() == EntityType.SLIME) {
                    nearbyEntity.remove();
                    addTailPart(player);
                    Sounds.ENTITY_GENERIC_EAT.play(player, 10, 1);
                }
            }
        }

        // 3% chance of spawning a slime randomly every 3 ticks
        // ~18% chance per second
        // We expect a slime spawning every 100 ticks, around every 5 seconds
        if (Utils.random(100) > 96) {
            Location randomLocation = Utils.getRandomElementFromList(spawnableSlimeLocations);
            Slime slime = world.spawn(randomLocation, Slime.class);
            slime.setSize(1);
            Utils.setAiEnabled(slime, false);
        }
    }


    private static class SnakeNode {

        private final Entity entity;
        private final DyeColor color;
        private final SnakeNode next;

        private SnakeNode(Entity entity, SnakeNode next, DyeColor color) {
            this.entity = entity;
            this.next = next;
            this.color = color;
        }
    }
}