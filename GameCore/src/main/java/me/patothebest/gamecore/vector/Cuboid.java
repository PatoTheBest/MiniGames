package me.patothebest.gamecore.vector;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.util.NameableObject;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is a region/cuboid from one location to another. It can be used for blocks protection and things like WorldEdit.
 * This class has been modified for GameCore
 *
 * @author desht (Original code), KingFaris10 (Editor of code), PatoTheBest (Editor of the edited code)
 */
public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable, NameableObject {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private AbstractArena arena;
    private final String name;
    private final String worldName;
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    /**
     * Construct a Cuboid given two Location objects which represent any two corners of the Cuboid.
     * Note: The 2 locations must be on the same world.
     *
     * @param l1 - One of the corners
     * @param l2 - The other corner
     */
    public Cuboid(String name, Location l1, Location l2) {
        if(! l1.getWorld().equals(l2.getWorld()))
            throw new IllegalArgumentException("Locations must be on the same world");
        this.name = name;
        this.worldName = l1.getWorld().getName();
        this.x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        this.y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        this.z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        this.x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        this.y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        this.z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
    }

    /**
     * Construct a Cuboid given two Location objects which represent any two corners of the Cuboid.
     * Note: The 2 locations must be on the same world.
     *
     * @param l1 - One of the corners
     * @param l2 - The other corner
     */
    public Cuboid(String name, Location l1, Location l2, AbstractArena arena) {
        this(name, l1, l2);
        this.arena = arena;
    }

    /**
     * Construct a one-block Cuboid at the given Location of the Cuboid.
     *
     * @param l1 location of the Cuboid
     */
    public Cuboid(String name, Location l1) {
        this(name, l1, l1);
    }

    /**
     * Copy constructor.
     *
     * @param other - The Cuboid to copy
     */
    private Cuboid(Cuboid other) {
        this(other.getName(), other.getWorld().getName(), other.x1, other.y1, other.z1, other.x2, other.y2, other.z2);
    }

    /**
     * Construct a Cuboid in the given World and xyz co-ordinates
     *
     * @param world - The Cuboid's world
     * @param x1    - X co-ordinate of corner 1
     * @param y1    - Y co-ordinate of corner 1
     * @param z1    - Z co-ordinate of corner 1
     * @param x2    - X co-ordinate of corner 2
     * @param y2    - Y co-ordinate of corner 2
     * @param z2    - Z co-ordinate of corner 2
     */
    public Cuboid(String name, World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.name = name;
        this.worldName = world.getName();
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    /**
     * Construct a Cuboid in the given world name and xyz co-ordinates.
     *
     * @param worldName - The Cuboid's world name
     * @param x1        - X co-ordinate of corner 1
     * @param y1        - Y co-ordinate of corner 1
     * @param z1        - Z co-ordinate of corner 1
     * @param x2        - X co-ordinate of corner 2
     * @param y2        - Y co-ordinate of corner 2
     * @param z2        - Z co-ordinate of corner 2
     */
    private Cuboid(String name, String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.name = name;
        this.worldName = worldName;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    /**
     * Construct a Cuboid using a map with the following keys: worldName, x1, x2, y1, y2, z1, z2
     *
     * @param map - The map of keys.
     */
    public Cuboid(Map<String, Object> map, AbstractArena abstractArena) {
        this.name = (String) map.get("name");
        int offsetX = abstractArena.getOffset().getBlockX();
        int offsetY = abstractArena.getOffset().getBlockY();
        int offsetZ = abstractArena.getOffset().getBlockZ();

        this.x1 = (Integer) map.get("x1") + offsetX;
        this.x2 = (Integer) map.get("x2") + offsetX;
        this.y1 = (Integer) map.get("y1") + offsetY;
        this.y2 = (Integer) map.get("y2") + offsetY;
        this.z1 = (Integer) map.get("z1") + offsetZ;
        this.z2 = (Integer) map.get("z2") + offsetZ;
        this.arena = abstractArena;
        this.worldName = abstractArena.getWorldName();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", this.name);

        if (arena == null) {
            map.put("worldName", this.getWorld().getName());
        }

        map.put("x1", this.x1);
        map.put("y1", this.y1);
        map.put("z1", this.z1);
        map.put("x2", this.x2);
        map.put("y2", this.y2);
        map.put("z2", this.z2);
        return map;
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    /**
     * Returns the cuboid name used to easily identify one cuboid from another
     *
     * @return the cuboid name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the Location of the lower northeast corner of the Cuboid (minimum XYZ co-ordinates).
     *
     * @return Location of the lower northeast corner
     */
    public Location getLowerNE() {
        return new Location(this.getWorld(), this.x1, this.y1, this.z1);
    }

    /**
     * Get the Location of the upper southwest corner of the Cuboid (maximum XYZ co-ordinates).
     *
     * @return Location of the upper southwest corner
     */
    public Location getUpperSW() {
        return new Location(this.getWorld(), this.x2, this.y2, this.z2);
    }

    /**
     * Get the blocks in the Cuboid.
     *
     * @return The blocks in the Cuboid
     */
    public List<Block> getBlocks() {
        Iterator<Block> blockI = this.iterator();
        List<Block> copy = new ArrayList<>();
        while(blockI.hasNext()) copy.add(blockI.next());
        return copy;
    }

    /**
     * Get the the centre of the Cuboid.
     *
     * @return Location at the centre of the Cuboid
     */
    public Location getCenter() {
        int x1 = this.getUpperX() + 1;
        int y1 = this.getUpperY() + 1;
        int z1 = this.getUpperZ() + 1;
        return new Location(this.getWorld(), this.getLowerX() + (x1 - this.getLowerX()) / 2.0, this.getLowerY() + (y1 - this.getLowerY()) / 2.0, this.getLowerZ() + (z1 - this.getLowerZ()) / 2.0);
    }

    /**
     * Get the Cuboid's world.
     *
     * @return The World object representing this Cuboid's world
     * @throws IllegalStateException if the world is not loaded
     */
    private World getWorld() {
        if(arena != null) {
            return arena.getWorld();
        }

        World world = Bukkit.getWorld(this.worldName);
        if(world == null) throw new IllegalStateException("World '" + this.worldName + "' is not loaded");
        return world;
    }

    /**
     * Get the size of this Cuboid along the X axis
     *
     * @return Size of Cuboid along the X axis
     */
    public int getSizeX() {
        return (this.x2 - this.x1) + 1;
    }

    /**
     * Get the size of this Cuboid along the Y axis
     *
     * @return Size of Cuboid along the Y axis
     */
    public int getSizeY() {
        return (this.y2 - this.y1) + 1;
    }

    /**
     * Get the size of this Cuboid along the Z axis
     *
     * @return Size of Cuboid along the Z axis
     */
    public int getSizeZ() {
        return (this.z2 - this.z1) + 1;
    }

    /**
     * Get the minimum X co-ordinate of this Cuboid
     *
     * @return the minimum X co-ordinate
     */
    private int getLowerX() {
        return this.x1;
    }

    /**
     * Get the minimum Y co-ordinate of this Cuboid
     *
     * @return the minimum Y co-ordinate
     */
    private int getLowerY() {
        return this.y1;
    }

    /**
     * Get the minimum Z co-ordinate of this Cuboid
     *
     * @return the minimum Z co-ordinate
     */
    private int getLowerZ() {
        return this.z1;
    }

    /**
     * Get the maximum X co-ordinate of this Cuboid
     *
     * @return the maximum X co-ordinate
     */
    private int getUpperX() {
        return this.x2;
    }

    /**
     * Get the maximum Y co-ordinate of this Cuboid
     *
     * @return the maximum Y co-ordinate
     */
    private int getUpperY() {
        return this.y2;
    }

    /**
     * Get the maximum Z co-ordinate of this Cuboid
     *
     * @return the maximum Z co-ordinate
     */
    private int getUpperZ() {
        return this.z2;
    }

    /**
     * Return true if the point at (x,y,z) is contained within this Cuboid.
     *
     * @param x - The X co-ordinate
     * @param y - The Y co-ordinate
     * @param z - The Z co-ordinate
     * @return true if the given point is within this Cuboid, false otherwise
     */
    private boolean contains(int x, int y, int z) {
        return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
    }

    /**
     * Check if the given Block is contained within this Cuboid.
     *
     * @param b - The Block to validateLicense for
     * @return true if the Block is within this Cuboid, false otherwise
     */
    public boolean contains(Block b) {
        return this.contains(b.getLocation());
    }

    /**
     * Check if the given Location is contained within this Cuboid.
     *
     * @param l - The Location to validateLicense for
     * @return true if the Location is within this Cuboid, false otherwise
     */
    public boolean contains(Location l) {
        return getWorld().getName().equals(l.getWorld().getName()) && this.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    /**
     * Get a list of the chunks which are fully or partially contained in this cuboid.
     *
     * @return A list of Chunk objects
     */
    public List<Chunk> getChunks() {
        List<Chunk> res = new ArrayList<>();

        World w = this.getWorld();
        int x1 = this.getLowerX() & ~ 0xf;
        int x2 = this.getUpperX() & ~ 0xf;
        int z1 = this.getLowerZ() & ~ 0xf;
        int z2 = this.getUpperZ() & ~ 0xf;
        for(int x = x1; x <= x2; x += 16) {
            for(int z = z1; z <= z2; z += 16) {
                res.add(w.getChunkAt(x >> 4, z >> 4));
            }
        }
        return res;
    }

    public List<Location> getChunkLocations() {
        List<Location> res = new ArrayList<>();

        World w = this.getWorld();
        int x1 = this.getLowerX() & ~ 0xf;
        int x2 = this.getUpperX() & ~ 0xf;
        int z1 = this.getLowerZ() & ~ 0xf;
        int z2 = this.getUpperZ() & ~ 0xf;
        for(int x = x1; x <= x2; x += 16) {
            for(int z = z1; z <= z2; z += 16) {
                res.add(new Location(w, x >> 4, 0, z >> 4));
            }
        }
        return res;
    }

    public Iterator<Block> iterator() {
        return new CuboidIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }
    
    public void show(Player player) {
        drawStructure(player, false);
    }

    public void hide(Player player) {
        drawStructure(player, true);
    }

    private void drawStructure(Player player, boolean original) {
        drawLine(player, original, x1, y1, z1, x2, y1, z1);
        drawLine(player, original,  x1, y1, z1, x1, y2, z1);
        drawLine(player, original,  x1, y1, z1, x1, y1, z2);

        drawLine(player, original,  x1, y2, z1, x2, y2, z1);
        drawLine(player, original,  x1, y2, z1, x1, y2, z2);

        drawLine2(player, original,  x2, y2, z2, x1, y2, z2);
        drawLine2(player, original,  x2, y2, z2, x2, y1, z2);
        drawLine2(player, original,  x2, y2, z2, x2, y2, z1);

        drawLine2(player, original,  x2, y1, z2, x1, y1, z2);
        drawLine2(player, original,  x2, y1, z2, x2, y1, z1);

        drawLine(player, original,  x1, y1, z2, x1, y2, z2);
        drawLine(player, original,  x2, y1, z1, x2, y2, z1);
    }

    private void drawLine(Player player, boolean original, int startx, int starty, int startz, int endx, int endy, int endz) {
        for(int x = startx; x <= endx; x++) {
            for(int y = starty; y <= endy; y++) {
                for(int z = startz; z <= endz; z++) {
                    if(original) {
                        Block block = new Location(getWorld(), x, y, z).getBlock();
                        player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                    } else {
                        player.sendBlockChange(new Location(getWorld(), x, y, z), Material.GLOWSTONE, (byte) 0);
                    }
                }
            }
        }
    }

    private void drawLine2(Player player, boolean original, int startx, int starty, int startz, int endx, int endy, int endz) {
        for(int x = startx; x >= endx; x--) {
            for(int y = starty; y >= endy; y--) {
                for(int z = startz; z >= endz; z--) {
                    if(original) {
                        Block block = new Location(getWorld(), x, y, z).getBlock();
                        player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                    } else {
                        player.sendBlockChange(new Location(getWorld(), x, y, z), Material.GLOWSTONE, (byte) 0);
                    }
                }
            }
        }
    }

    @Override
    public Cuboid clone() {
        try {
            super.clone();
        } catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new Cuboid(this);
    }

    @Override
    public String toString() {
        return "Cuboid: " + this.getWorld().getName() + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2;
    }

    public String toString(CommandSender sender, int number) {
        return CoreLang.CUBOID_TO_STRING.replace(sender, number, name, this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2);
    }

    public Cuboid setArena(AbstractArena arena) {
        this.arena = arena;
        return this;
    }
}