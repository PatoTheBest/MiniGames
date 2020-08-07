package me.patothebest.gamecore.treasure.chest;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.treasure.reward.RewardData;
import me.patothebest.gamecore.treasure.reward.RewardRandomizer;
import me.patothebest.gamecore.block.BlockRestorer;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.treasure.TreasureFactory;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.util.Callback;
import me.patothebest.gamecore.util.Sounds;
import me.patothebest.gamecore.util.WrappedBukkitRunnable;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The code was originally used in MineBone network when
 * I was the developer. This code is about a year old and
 * was adapted for the game core. Some things can definitely
 * be improved and some others can be left alone.
 * <p>
 * An addition to the original code was Guice, you can now
 * create a TreasureChest by using the TreasureFactory.createChest
 * method by passing the necessary parameters.
 *
 */
public class TreasureChest {

    private final PluginScheduler scheduler;
    private final IPlayer player;
    private final BlockRestorer blockRestore;
    private final TreasureType treasureType;
    private final TreasureChestLocation treasureLocation;
    private final TreasureFactory treasureFactory;
    private final NMS nms;
    private final Callback<TreasureChestLocation> callback;

    private Location center;
    private RewardRandomizer randomizer;
    private int stage;
    private int chestIndex = 4;
    private BukkitTask task;

    private final List<Block> blocksToRestore = new ArrayList<>();
    private final List<Entity> items = new ArrayList<>();
    private final List<Chest> chests = new CopyOnWriteArrayList<>();
    private final List<ArmorStand> holograms = new ArrayList<>();

    @Inject private TreasureChest(PluginScheduler scheduler, @Assisted IPlayer player, BlockRestorer blockRestore, @Assisted TreasureType treasureType, @Assisted TreasureChestLocation treasureLocation, TreasureFactory treasureFactory, NMS nms, @Assisted Callback<TreasureChestLocation> callback) {
        this.scheduler = scheduler;
        this.player = player;
        this.blockRestore = blockRestore;
        this.treasureType = treasureType;
        this.treasureLocation = treasureLocation;
        this.treasureFactory = treasureFactory;
        this.nms = nms;
        this.callback = callback;
    }

    public void playAnimation(Location location) {
        stage = 0;
        treasureLocation.getHologram().hide();
        this.randomizer = treasureFactory.createRewardRandomizer(player, player.getPlayer(), treasureType);
        task = scheduler.runTaskTimer(() -> {
            if (stage >= 6) {
                task.cancel();

                playChestAnimation();
                return;
            }

            makeSchematic(location, TreasureChestStructure.STRUCTURE[stage], stage == 0);
            stage++;
        }, 0L, 20L);
    }

    private void playChestAnimation() {
        task = scheduler.runTaskTimer(() -> {
            if (chestIndex == 0) {
                task.cancel();
                task = scheduler.runTaskLater(this::forceOpen, 1200L);
                return;
            }

            playHelix(getChestLocation(chestIndex, center.clone()), 0.0F, ParticleType.FLAME);
            playHelix(getChestLocation(chestIndex, center.clone()), 3.5F, ParticleType.FLAME);

            scheduler.runTaskLater(() -> {
                Block b = getChestLocation(chestIndex, center.clone()).getBlock();
                b.setType(Material.CHEST);

                Sounds.BLOCK_ANVIL_LAND.play(player.getPlayer(), 2, 1);
                FastParticle.spawnParticle(b.getWorld(), ParticleType.SMOKE_LARGE, b.getLocation(), 5);
                FastParticle.spawnParticle(b.getWorld(), ParticleType.LAVA, b.getLocation(), 5);

                BlockFace blockFace = BlockFace.SOUTH;
                switch (chestIndex) {
                    case 4:
                        blockFace = BlockFace.SOUTH;
                        break;
                    case 3:
                        blockFace = BlockFace.NORTH;
                        break;
                    case 2:
                        blockFace = BlockFace.EAST;
                        break;
                    case 1:
                        blockFace = BlockFace.WEST;
                        break;
                }

                nms.setDirectionalBlockData(b, blockFace, false);
                BlockState state = b.getState();
                chests.add((Chest) state);
                chestIndex--;
            }, 20L);
        }, 0L, 50L);
    }

    public void playHelix(final Location loc, final float i, final ParticleType effect) {
        BukkitRunnable runnable = new WrappedBukkitRunnable() {
            double radius = 0;
            double step;
            final double y = loc.getY();
            final Location location = loc.clone().add(0, 3, 0);

            @Override
            public void run() {
                double inc = (2 * Math.PI) / 50;
                double angle = step * inc + i;
                Vector v = new Vector();
                v.setX(Math.cos(angle) * radius);
                v.setZ(Math.sin(angle) * radius);
                if (effect == ParticleType.REDSTONE) {
                    FastParticle.spawnParticle(loc.getWorld(), ParticleType.REDSTONE, loc.add(v), -1, -1, 1, 1, 0);
                } else {
                    FastParticle.spawnParticle(loc.getWorld(), effect, location.add(v), 1, 0, 0, 0, 0);
                }
                location.subtract(v);
                location.subtract(0, 0.1d, 0);
                if (location.getY() <= y) {
                    cancel();
                }
                step += 4;
                radius += 1 / 30f;
            }
        };

        scheduler.runTaskTimer(runnable, 0, 1);
    }

    @SuppressWarnings("deprecation")
    private void makeSchematic(final Location loc, char[][][] structure, boolean clear) {
        Block centerBlock = loc.getBlock();
        final org.bukkit.World world = centerBlock.getWorld();
        int bX = centerBlock.getX();
        int bY = centerBlock.getY();
        int bZ = centerBlock.getZ();
        for (char[][] structurePiece : structure) {
            if (structurePiece == null) {
                continue;
            }

            for (char[] aStructurePiece : structurePiece) {
                for (char anAStructurePiece : aStructurePiece) {
                    Block block = world.getBlockAt(bX, bY, bZ);
                    if (clear) {
                        blocksToRestore.add(block);

                        if (block.getType() != Material.AIR && bY != loc.getBlockY() - 4) {
                            FastParticle.spawnParticle(world, ParticleType.BLOCK_CRACK, bX, bY, bZ, 1, new MaterialData(block.getType(), block.getData()));
                            world.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
                        }

                        if (bY != loc.getBlockY() - 4) {
                            blockRestore.changeBlockTemporarily(block, new ItemStack(Material.AIR), 150000L);
                        } else {
                            blockRestore.changeBlockTemporarily(block, new ItemStackBuilder(block.getType()), 150000L);
                        }
                    }

                    if (anAStructurePiece == 'a') {
                        if(clear && bY != loc.getBlockY() - 4){
                            blockRestore.changeBlockTemporarily(centerBlock, new ItemStack(Material.AIR), 150000L);
                        }
                    } else {
                        Map<TreasureChestStructurePiece, TreasureChestPiece> treasureChestStructurePieceTreasureChestPieceMap = TreasureChestStructure.PIECES.get(treasureType);
                        TreasureChestStructurePiece treasureChestStructurePiece = TreasureChestStructurePiece.getStructurePiece(anAStructurePiece);
                        TreasureChestPiece treasureChestPiece = treasureChestStructurePieceTreasureChestPieceMap.get(treasureChestStructurePiece);

                        if(treasureChestPiece == null) {
                            System.err.println("Unknown treasure piece " + anAStructurePiece);
                            continue;
                        }

                        blockRestore.changeBlockTemporarily(centerBlock, new ItemStack(Material.AIR), 150000L);
                        nms.setBlock(block, new ItemStackBuilder(treasureChestPiece.getMaterial()));

                        if(treasureChestStructurePiece.getTreasureChestCallBack() != null) {
                            treasureChestStructurePiece.getTreasureChestCallBack().call(this, block);
                        }

                        FastParticle.spawnParticle(block.getWorld(), ParticleType.BLOCK_CRACK, block.getLocation(), 1, new MaterialData(block.getType()));
                        world.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
                    }

                    bX++;
                }

                bX -= aStructurePiece.length;
                --bY;
            }

            bY += 5;
            bZ++;
        }

        if(stage == 0) {
            Block relative = center.getBlock().getRelative(BlockFace.DOWN);
            blockRestore.changeBlockTemporarily(relative, null, 150000L);
            blocksToRestore.add(relative);
            Map<TreasureChestStructurePiece, TreasureChestPiece> treasureChestStructurePieceTreasureChestPieceMap = TreasureChestStructure.PIECES.get(treasureType);
            TreasureChestStructurePiece treasureChestStructurePiece = TreasureChestStructurePiece.getStructurePiece('l');
            TreasureChestPiece treasureChestPiece = treasureChestStructurePieceTreasureChestPieceMap.get(treasureChestStructurePiece);
            nms.setBlock(relative, new ItemStackBuilder(treasureChestPiece.getMaterial()));

            Block redstoneBlock = relative.getRelative(BlockFace.DOWN);
            blockRestore.changeBlockTemporarily(redstoneBlock, null, 150000L);
            blocksToRestore.add(redstoneBlock);
            redstoneBlock.setType(Material.REDSTONE_BLOCK);
        }
    }

    public void setStairsDirection(Block block) {
        Location blockLocation = block.getLocation();
        if (center.getBlockX() < blockLocation.getBlockX()) {
            setDirectionalBlockData(block, BlockFace.EAST, false);
            return;
        }

        if (center.getBlockX() > blockLocation.getBlockX()) {
            setDirectionalBlockData(block, BlockFace.WEST, false);
            return;
        }

        if (center.getBlockZ() < blockLocation.getBlockZ()) {
            setDirectionalBlockData(block, BlockFace.SOUTH, false);
            return;
        }

        if (center.getBlockZ() > blockLocation.getBlockZ()) {
            setDirectionalBlockData(block, BlockFace.NORTH, false);
        }
    }

    public void setUpsideDownStairsDirection(Block block) {
        Location blockLocation = block.getLocation();
        if (center.getBlockX() < blockLocation.getBlockX()) {
            setDirectionalBlockData(block, BlockFace.WEST, true);
            return;
        }

        if (center.getBlockX() > blockLocation.getBlockX()) {
            setDirectionalBlockData(block, BlockFace.EAST, true);
            return;
        }

        if (center.getBlockZ() < blockLocation.getBlockZ()) {
            setDirectionalBlockData(block, BlockFace.NORTH, true);
            return;
        }

        if (center.getBlockZ() > blockLocation.getBlockZ()) {
            setDirectionalBlockData(block, BlockFace.SOUTH, true);
        }
    }

    private void setDirectionalBlockData(Block block, BlockFace dir, boolean upsidedown) {
        nms.setDirectionalBlockData(block, dir.getOppositeFace(), upsidedown);
    }

    public void forceDestroy() {
        for (Chest chest : chests) {
            randomizer.giveRandomThing();
            chests.remove(chest);
        }

        clear();
    }

    private void forceOpen() {
        for (Chest chest : chests) {
            openChest(chest.getBlock());
        }
    }

    public void openChest(Block chest) {
        RewardData rewardData = randomizer.giveRandomThing();

        org.bukkit.inventory.ItemStack is = rewardData.getItemStack();
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(UUID.randomUUID().toString());
        is.setItemMeta(itemMeta);

        Entity itemEntity = nms.spawnItem(is, chest.getLocation());

        this.items.add(itemEntity);
        final String name = rewardData.getName();
        scheduler.runTaskLater(() -> spawnHologram(chest.getLocation().add(0.5D, -0.7, 0.5D), name), 15L);

        if(rewardData.isRareItem()) {
            FireworkEffect effect = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.RED).withFade(Color.ORANGE).with(FireworkEffect.Type.BALL).build();
            Firework fw = chest.getLocation().getWorld().spawn(chest.getLocation().add(0.5D, 0, 0.5D), Firework.class);
            FireworkMeta meta = fw.getFireworkMeta();
            meta.addEffect(effect);
            meta.setPower(0);
            fw.setFireworkMeta(meta);
            scheduler.runTaskLater(fw::detonate, 2L);
        }

        playChestAction(chest, true);
        chests.remove(chest.getState());

        if (chests.isEmpty() && chestIndex == 0) {
            scheduler.runTaskLater(this::clear, 100L);
        }
    }

    @SuppressWarnings("deprecation")
    private void clear() {
        task.cancel();
        holograms.forEach(Entity::remove);
        holograms.clear();
        items.forEach(Entity::remove);
        items.clear();

        blocksToRestore.forEach(block -> {
            if (block.getType() != Material.AIR) {
                FastParticle.spawnParticle(block.getWorld(), ParticleType.BLOCK_CRACK, block.getLocation(), 1, new MaterialData(block.getType(), block.getData()));
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
            }
            blockRestore.restore(block);
        });

        blocksToRestore.clear();
        chests.clear();
        callback.call(treasureLocation);

        if(treasureLocation.getHologram().isAlive()) {
            treasureLocation.getHologram().show();
        }
    }

    public void addItem(Entity entity) {
        this.items.add(entity);
    }

    public void playChestAction(Block b, boolean open) {
        nms.playChestAction(b, open);
    }

    private Location getChestLocation(int i, Location loc) {
        Location chestLocation = this.center.clone();
        chestLocation.setX(loc.getBlockX() + 0.5D);
        chestLocation.setY(loc.getBlockY() + 1.0D);
        chestLocation.setZ(loc.getBlockZ() + 0.5D);
        switch (i) {
            case 1:
                chestLocation.add(2.0D, 0.0D, 0.0D);
                break;
            case 2:
                chestLocation.add(-2.0D, 0.0D, 0.0D);
                break;
            case 3:
                chestLocation.add(0.0D, 0.0D, 2.0D);
                break;
            case 4:
                chestLocation.add(0.0D, 0.0D, -2.0D);
        }

        return chestLocation;
    }

    private void spawnHologram(Location location, String s) {
        location.setY(location.getY() + 1);
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setSmall(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setCustomName(s);
        armorStand.setCustomNameVisible(true);
        this.holograms.add(armorStand);
    }

    /**
     * Gets the player
     *
     * @return the player
     */
    public IPlayer getPlayer() {
        return player;
    }

    /**
     * Gets the dropped item entities
     *
     * @return the dropped item entities
     */
    public List<Entity> getItems() {
        return items;
    }

    /**
     * Gets the chests that haven't been opened
     *
     * @return the chests that haven't been opened
     */
    public List<Chest> getChests() {
        return chests;
    }

    /**
     * Sets the center location
     */
    public void setCenter(Location center) {
        this.center = center;
    }
}