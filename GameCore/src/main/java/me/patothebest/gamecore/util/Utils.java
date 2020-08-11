package me.patothebest.gamecore.util;

import com.google.common.io.ByteStreams;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.actionbar.ActionBar;
import me.patothebest.gamecore.chat.DefaultFontInfo;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.kit.WrappedPotionEffect;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.vector.ArenaLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import sun.net.www.protocol.http.Handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static me.patothebest.gamecore.PluginConfig.*;

/**
 * A collection of many utilities
 */
public class Utils {

    private final static SplittableRandom RANDOM = new SplittableRandom();
    public final static String SERVER_VERSION = ServerVersion.getVersion();
    public final static File PLUGIN_DIR = new File("plugins" + File.separatorChar + PLUGIN_NAME + File.separatorChar);
    private static final DecimalFormat ONE_DECIMAL_FORMAT = new DecimalFormat("0.0");
    private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");
    public static final String DECRYPT_KEY = "TheTowers";
    private static final String USER_ID = "%%__USER__%%";
    //private static String USER_ID = new CryptoUtil().decryptSilently(DECRYPT_KEY, "QllFvMZZ/BY=");
    //private static String USER_ID = new CryptoUtil().decryptSilently(DECRYPT_KEY, "/dB5CK74bTQ=");
    private static Constructor<?> gameProfileConstructor;
    private static Constructor<?> propertyConstructor;
    private static Logger logger = Logger.getLogger("Minecraft"); // will be replaced

    public static void setLogger(Logger logger) {
        Utils.logger = logger;
    }

    private Utils () {}

    /**
     * Sets final field to the provided value
     *
     * @param clazz
     * @param fieldName
     * @param newValue
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setStaticFinalField(Class<?> clazz, String fieldName, Object newValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        setFinalField(clazz.getDeclaredField(fieldName), null, newValue);
    }

    /**
     * Sets final field to the provided value
     *
     * @param field    - the field which should be modified
     * @param obj      - the object whose field should be modified
     * @param newValue - the new value for the field of obj being modified
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setFinalField(Field field, Object obj, Object newValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        setAccessible(Field.class.getDeclaredField("modifiers")).setInt(field, field.getModifiers() & ~Modifier.FINAL);
        setAccessible(Field.class.getDeclaredField("root")).set(field, null);
        setAccessible(Field.class.getDeclaredField("overrideFieldAccessor")).set(field, null);
        setAccessible(field).set(obj, newValue);
    }

    /**
     * Gets field reflectively
     *
     * @param clazz
     * @param fieldName
     * @param obj
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Class<?> clazz, String fieldName, Object obj) {
        try {
            return (T) setAccessible(clazz.getDeclaredField(fieldName)).get(obj);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Could not get field value " + fieldName + " of " + clazz.getName(), t);
        }
        return null;
    }

    /**
     * Gets field reflectively
     *
     * @param clazz
     * @param fieldName
     * @param obj
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValueNotDeclared(Class<?> clazz, String fieldName, Object obj) {
        try {
            return (T) setAccessible(clazz.getField(fieldName)).get(obj);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Could not get field value " + fieldName + " of " + clazz.getName(), t);
        }
        return null;
    }

    /**
     * Gets Method reflectively
     *
     * @param clazz
     * @param methodName
     * @param obj
     *
     * @return
     */
    private static Method getMethodValue(Class<?> clazz, String methodName, Class<?>... obj) {
        try {
            return clazz.getDeclaredMethod(methodName, obj);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Could not get method value " + methodName + " of " + clazz.getName(), t);
        }
        return null;
    }

    /**
     * Gets Method reflectively
     *
     * @param clazz
     * @param methodName
     * @param obj
     *
     * @return
     */
    public static Method getMethodNotDeclaredValue(Class<?> clazz, String methodName, Class<?>... obj) {
        try {
            return clazz.getMethod(methodName, obj);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Could not get method value " + methodName + " of " + clazz.getName(), t);
        }
        return null;
    }

    /**
     * Sets accessibleobject accessible state an returns this object
     *
     * @param <T>
     * @param object
     *
     * @return
     */
    private static <T extends AccessibleObject> T setAccessible(T object) {
        object.setAccessible(true);
        return object;
    }

    /**
     * Sets field reflectively
     *
     * @param clazz
     * @param fieldName
     * @param obj
     * @param value
     */
    public static void setFieldValue(Class<?> clazz, String fieldName, Object obj, Object value) {
        try {
            setAccessible(clazz.getDeclaredField(fieldName)).set(obj, value);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Could not set field value " + fieldName + " of " + clazz.getName(), t);
        }
    }

    /**
     * Sets field reflectively
     *
     * @param clazz
     * @param fieldName
     * @param obj
     * @param value
     */
    public static void setFieldValueNotDeclared(Class<?> clazz, String fieldName, Object obj, Object value) {
        try {
            setAccessible(clazz.getField(fieldName)).set(obj, value);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Could not set field value " + fieldName + " of " + clazz.getName(), t);
        }
    }


    /**
     * Gets a method from the specified class
     * If it cannot retrieve the method, it will return null
     *
     * @param clazz          the class
     * @param name           the name of the method
     * @param parameterTypes the parameter array
     *
     * @return the {@code Method} object for the method of this class matching the specified name and parameters
     */
    public static Method getMethodOrNull(Class<?> clazz, String name, Class<?>... parameterTypes) {
        if (clazz == null) {
            return null;
        }

        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Gets a field from the specified class
     * If it cannot retrieve the field, it will return null
     *
     * @param clazz the class
     * @param name  the name of the field
     *
     * @return the {@code Field} object for the specified field in this class
     */
    public static Field getFieldOrNull(Class<?> clazz, String name) {
        if (clazz == null) {
            return null;
        }

        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * Invoke a static method
     *
     * @param clazz
     * @param method
     * @param parameterClasses
     * @param parameters
     *
     * @return
     */
    public static Object invokeStaticMethod(Class<?> clazz, String method, Class<?>[] parameterClasses, Object... parameters) {
        return invokeStaticMethod(getMethodValue(clazz, method, parameterClasses), parameters);
    }

    /**
     * Invoke a static method
     *
     * @param method
     * @param parameters
     *
     * @return
     */
    private static Object invokeStaticMethod(Method method, Object... parameters) {
        try {
            return method.invoke(null, parameters);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not invoke static method " + method.getName() + "!", e);
        }

        return null;
    }

    /**
     * Invoke a non static method
     *
     * @param object
     * @param method
     * @param parameterClasses
     * @param parameters
     *
     * @return
     */
    public static Object invokeMethod(Object object, String method, Class<?>[] parameterClasses, Object... parameters) {
        return invokeMethod(object, getMethodValue(object.getClass(), method, parameterClasses), parameters);
    }

    /**
     * Invoke a non static method
     *
     * @param object
     * @param method
     * @param parameters
     *
     * @return
     */
    public static Object invokeMethod(Object object, Method method, Object... parameters) {
        try {
            return method.invoke(object, parameters);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not invoke method " + method.getName() + "!", e);
        }

        return null;
    }

    /**
     * Returns a random number using Java's Random
     *
     * @param next Maximum value.
     *
     * @return Random integer
     * @see java.util.Random#nextInt(int)
     */
    public static int random(int next) {
        return RANDOM.nextInt(next);
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param number1 Value number 1
     * @param number2 Value number 2.
     *
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int number1, int number2) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return Math.min(number1, number2) + (int) Math.round(-0.5f + (1 + Math.abs(number1 - number2)) * RANDOM.nextDouble());
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param number1 Value number 1
     * @param number2 Value number 2.
     *
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static double randDouble(double number1, double number2) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return Math.min(number1, number2) + (-0.5f + (1 + Math.abs(number1 - number2)) * RANDOM.nextDouble());
    }

    /**
     * Returns a craftbukkit CBS class
     *
     * @param nmsClassString the class name
     *
     * @return the NMS class
     */
    public static Class<?> getCBSClass(String nmsClassString) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + "." + nmsClassString);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Could not get CBS class " + nmsClassString + "!", e);
        }

        return null;
    }

    /**
     * Returns a minecraft NMS class
     *
     * @param nmsClassString the class name
     *
     * @return the NMS class
     */
    public static Class<?> getNMSClass(String nmsClassString) {
        try {
            return Class.forName("net.minecraft.server." + SERVER_VERSION + "." + nmsClassString);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Could not NMS CBS class " + nmsClassString + "!", e);
        }

        return null;
    }

    /**
     * Returns a craftbukkit CBS class
     *
     * @param nmsClassString the class name
     *
     * @return the NMS class
     */
    public static Class<?> getCBSClassOrNull(String nmsClassString) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + "." + nmsClassString);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Returns a minecraft NMS class
     *
     * @param nmsClassString the class name
     *
     * @return the NMS class
     */
    public static Class<?> getNMSClassOrNull(String nmsClassString) {
        try {
            return Class.forName("net.minecraft.server." + SERVER_VERSION + "." + nmsClassString);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Sends a packet to the player via reflection
     *
     * @param p      the bukkit player
     * @param packet the packet object
     */
    public static void sendPacket(Player p, Object packet) {
        try {
            //noinspection ConstantConditions
            getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet")).invoke(getNMSClass("EntityPlayer").getDeclaredField("playerConnection").get(getHandle(p)), packet);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not send packet " + packet.getClass() + "!", e);
        }
    }

    /**
     * Get's the player handle, also known as the EntityPlayer
     *
     * @param player the bukkit player
     *
     * @return the NMS player object
     */
    public static Object getHandle(Player player) {
        try {
            return player.getClass().getMethod("getHandle").invoke(player);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not get player's handle!", e);
        }

        return null;
    }

    /**
     * Creates a new object instance
     *
     * @param tClass      the object class
     * @param constructor the object constructor to initialize the class
     * @param <T>         the object type
     *
     * @return the created object
     */
    public static <T> T createInstance(Class<T> tClass, Object... constructor) {
        try {
            Class[] classes = new Class[constructor.length];

            for (int i = 0; i < constructor.length; i++) {
                classes[i] = constructor[i].getClass();
            }

            Constructor<T> ctor = tClass.getConstructor(classes);
            return ctor.newInstance(constructor);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not create a new instance of " + tClass.getName() + "!", e);
        }

        return null;
    }

    /**
     * Returns a serialized itemstack into string form
     * <p>
     * The serialized itemstack will look like:
     * material:data,amount
     *
     * @param itemStack the itemstack to serialize
     *
     * @return the serialized itemstack
     */
    public static String itemStackToString(final ItemStack itemStack) {
        return (itemStack != null) ? (itemStack.getType() + ":" + itemStack.getDurability() + "," + itemStack.getAmount()) : null;
    }

    /**
     * Returns an itemstack from a string
     * <p>
     * The format can be:
     * - material
     * - material:data
     * - material,amount
     * - material:data,amount
     *
     * @param string the serialized itemstack
     *
     * @return the deserialized itemtack
     */
    public static ItemStack itemStackFromString(String string) {
        if (string == null) {
            return null;
        }

        String materialName = string;

        int amount = 1;
        if (materialName.contains(",")) {
            materialName = string.substring(0, string.indexOf(','));
            amount = isNumber(string.substring(string.indexOf(',') + 1)) ? Integer.parseInt(string.substring(string.indexOf(',') + 1)) : 1;
        }

        int data = materialName.indexOf(':');
        if (data != -1 && isNumber(materialName.substring(data + 1)) && Integer.parseInt(materialName.substring(data + 1)) == 0) {
            materialName = materialName.substring(0, data);
        }

        me.patothebest.gamecore.itemstack.Material material = me.patothebest.gamecore.itemstack.Material.matchMaterial(materialName).orElse(null);
        //printDebug("PARSING ITEMS", "Original item: " + string, "Parsed material: " + (material == null ? "null" : material.toString()));
        return material != null ? new ItemStackBuilder(material).amount(amount) : null;
    }

    /**
     * Parses an item from config using SkyWarsReloaded format
     * <p>
     * The first value is the item, this should be the material
     * name (as found on http://jd.bukkit.org/rb/apidocs/org/bukkit/Material.html)
     * The second value is the quantity that will show up in the chest.
     * <p>
     * For wood, potions, wool, dirt, etc (Any item that has type data),
     * you can use a : followed by the numerical data for the item you want.
     * For example wood:2 would give you birch wood planks. You can find
     * data values at: http://minecraft-ids.grahamedgecombe.com/
     * <p>
     * Potion data values can be found at: http://minecraft.gamepedia.com/Potion
     * <p>
     * Enchantments can be added using the enchantment name (written with no spaces)
     * followed by a : and then the enchantment level number.
     *
     * @param itemData the item data
     * @return the parsedItem
     */
    public static ItemStack parseItem(String[] itemData) {
        if (itemData.length < 2) {
            return null;
        }

        ItemStackBuilder itemStack;

        if (itemData[0].contains(":")) {
            String[] materialAndData = itemData[0].split(":");
            Material material = Material.getMaterial(materialAndData[0].toUpperCase());
            int amount = Integer.parseInt(itemData[1]);

            if (amount < 1) {
                return null;
            }

            short data = (short) Integer.parseInt(materialAndData[1].toUpperCase());
            itemStack = new ItemStackBuilder(material).amount(amount).data(data);
        } else {
            itemStack = new ItemStackBuilder(Material.getMaterial(itemData[0].toUpperCase())).amount(Integer.parseInt(itemData[1]));
        }

        if (itemData.length > 2) {
            for (int x = 2; x < itemData.length; x++) {
                String[] splitString = itemData[x].split(":");

                if (splitString[0].equalsIgnoreCase("name")) {
                    itemStack.name(splitString[1]);
                } else if (splitString[0].equalsIgnoreCase("color")) {
                    if (itemStack.getType().name().startsWith("LEATHER")) {
                        itemStack.color(getColor(splitString[1]));
                    }
                } else {
                    itemStack.addUnsafeEnchantment(getEnchant(splitString[0]), Integer.parseInt(splitString[1]));
                }
            }

        }

        return itemStack;
    }

    /**
     * Converts the player inventory to a String array of Base64 strings. First string is the content and second string is the armor.
     *
     * @param playerInventory to turn into an array of strings.
     * @return Array of strings: [ main content, armor content ]
     * @throws IllegalStateException
     */
    public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
        //get the main content part, this doesn't return the armor
        String content = toBase64(playerInventory);
        String armor = itemStackArrayToBase64(playerInventory.getArmorContents());

        return new String[] { content, armor };
    }

    /**
     *
     * A method to serialize an {@link ItemStack} array to Base64 String.
     *
     * <p />
     *
     * Based off of {@link #toBase64(Inventory)}.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     * @throws IllegalStateException
     */
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * A method to serialize an inventory to Base64 string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param inventory to serialize
     * @return Base64 string of the provided inventory
     * @throws IllegalStateException
     */
    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     *
     * A method to get an {@link Inventory} from an encoded, Base64, string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param data Base64 string of data containing an inventory.
     * @return Inventory created from the Base64 string.
     * @throws IOException
     */
    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     *
     * <p />
     *
     * Base off of {@link #fromBase64(String)}.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException
     */
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     *
     * A method to serialize an {@link ItemStack} array to Base64 String.
     *
     * <p />
     *
     * Based off of {@link #toBase64(Inventory)}.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     * @throws IllegalStateException
     */
    public static String potionEffectArrayToBase64(WrappedPotionEffect[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (WrappedPotionEffect item : items) {
                dataOutput.writeObject(item);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * Gets an array of wrapped potion effects from Base64 string.
     *
     * <p />
     *
     * Base off of {@link #fromBase64(String)}.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return WrappedPotionEffect array created from the Base64 string.
     * @throws IOException
     */
    public static WrappedPotionEffect[] potionEffectsFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            WrappedPotionEffect[] items = new WrappedPotionEffect[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (WrappedPotionEffect) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * Gets the user id who bought the plugin
     *
     * @return the user who bought the plugin
     */
    public static String getUserId() {
        return USER_ID;
    }

    /**
     * Opens a connection with reflection
     *
     * @param url the url
     *
     * @return the url connection
     */
    public static URLConnection openConnectionReflectively(URL url) {
        return (URLConnection) invokeMethod(new Handler(), setAccessible(getMethodOrNull(Handler.class, "openConnection", URL.class)), url);
    }

    /**
     * Schedules to disable a plugin
     *
     * @param plugin the plugin to disable
     */
    public static void scheduleToDisable(Plugin plugin) {
        new WrappedBukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }.runTask(plugin);
    }

    /**
     * Gets an enum element from its name
     *
     * @param enumClass  the enum class
     * @param enumString the element's name
     * @param <E>        the element type
     *
     * @return the element
     */
    public static <E extends Enum<E>> E getEnumValueFromString(Class<E> enumClass, String enumString) {
        return getEnumValueFromString(enumClass, enumString, null);
    }

    /**
     * Gets an enum element from its name
     *
     * @param enumClass        the enum class
     * @param enumString       the element's name
     * @param defaultEnumValue the value to return when no value is found
     * @param <E>              the element type
     *
     * @return the element
     */
    public static <E extends Enum<E>> E getEnumValueFromString(Class<E> enumClass, String enumString, E defaultEnumValue) {
        for (E enumElement : EnumSet.allOf(enumClass)) {
            if (enumElement.name().equalsIgnoreCase(enumString)) {
                return enumElement;
            }
        }

        return defaultEnumValue;
    }

    /**
     * Gets an enum element from its name
     *
     * @param enumClass  the enum class
     * @param enumString the element's name
     *
     * @return the element
     */
    public static Object getEnumObjectRaw(Class<? extends Enum> enumClass, String enumString) {
        Enum[] enumConstants = enumClass.getEnumConstants();

        for (Enum enumConstant : enumConstants) {
            if (enumConstant.name().equals(enumString)) {
                return enumConstant;
            }
        }

        return null;
    }

    /**
     * Get all the zip files in a directory
     *
     * @param directory    the main directory
     * @param subDirectory the subdirectory
     *
     * @return the list of files
     */
    public static ArrayList<String> getZipFilesFromDirectory(String directory, String subDirectory) {
        final File folder = new File(directory + File.separator + subDirectory);

        // validate if the folder actually exists
        if (!folder.exists()) {
            return null;
        }

        // create the list of files we are going to return
        final ArrayList<String> files = new ArrayList<>();

        // get all the files in the folder and
        // validate if the array is empty
        File[] listFiles = folder.listFiles();
        assert listFiles != null;

        for (int length = listFiles.length, i = 0; i < length; ++i) {
            final File file = listFiles[i];

            // skip directories
            if (!file.isFile()) {
                continue;
            }

            // get the file's name
            String name = file.getName();
            if (name.length() >= 5) {
                // get the file extension
                name = name.substring(name.length() - 4);

                // validate if the file is a zip file
                if (name.equals(".zip")) {
                    // add the file to the list
                    files.add(file.getName().substring(0, file.getName().length() - 4));
                }
            }
        }

        return files;
    }

    /**
     * Concatenate two arrays
     *
     * @param a   first array
     * @param b   second array
     * @param <T> array type
     *
     * @return concatenated array
     */
    public static <T> T[] concatenateArray(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        // create the new array
        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);

        // copy the old arrays into the new array
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    /**
     * Concatenate two arrays
     *
     * @param a   first array
     * @param b   second array
     * @param <T> array type
     *
     * @return concatenated array
     */
    public static <T> T[] concatenateArray(T[] a, T[] b, Class<T> arrayClass) {
        int aLen = a.length;
        int bLen = b.length;

        // create the new array
        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(arrayClass, aLen + bLen);

        // copy the old arrays into the new array
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    /**
     * Combines two lists
     *
     * @param a   first array
     * @param b   second array
     * @param <T> list type
     *
     * @return combined list
     */
    public static <T> List<T> combineLists(List<T> a, List<T> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList());
    }

    /**
     * Sorts a {@link Map} of String, Integer by
     * the highest values
     *
     * @param map the map
     * @return the sorted list
     */
    public static List<Map.Entry<String, Integer>> sortMap(Map<String, Integer> map) {
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
        entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return entries;
    }

    /**
     * Create a directory if it doesn't exist
     *
     * @param dir the directory
     *
     * @return the directory
     */
    public static File createDirectoryIfNotExists(File dir) {
        // validate if the directory exists
        if (!dir.exists()) {
            // attempt to create the directory
            if (dir.mkdirs()) {
                logger.log(Level.INFO, "Successfully created directory " + dir.getAbsolutePath());
            } else {
                logger.log(Level.SEVERE, "Failed to create directory");
            }
        }

        return dir;
    }

    /**
     * Unzip a file
     *
     * @param zipFile      input zip file
     * @param outputFolder zip file output folder
     */
    public static boolean unZip(String zipFile, String outputFolder) {
        File directory = new File(outputFolder);

        // if the output directory doesn't exist, create it
        if (!directory.exists())
            directory.mkdirs();

        // buffer for read and write data to file
        byte[] buffer = new byte[2048];

        try {
            FileInputStream fInput = new FileInputStream(zipFile);
            ZipInputStream zipInput = new ZipInputStream(fInput);

            ZipEntry entry = zipInput.getNextEntry();

            while (entry != null) {
                String entryName = entry.getName();
                File file = new File(outputFolder + File.separator + entryName);

                //System.out.println("Unzip file " + entryName + " to " + file.getAbsolutePath());

                // create the directories of the zip directory
                if (entry.isDirectory()) {
                    File newDir = new File(file.getAbsolutePath());
                    if (!newDir.exists()) {
                        boolean success = newDir.mkdirs();
                        if (!success) {
                            logger.log(Level.SEVERE, "Problem creating folder: " + newDir.getName());
                        }
                    }
                } else {
                    FileOutputStream fOutput = new FileOutputStream(file);
                    int count;
                    while ((count = zipInput.read(buffer)) > 0) {
                        // write 'count' bytes to the file output stream
                        fOutput.write(buffer, 0, count);
                    }
                    fOutput.close();
                }
                // close ZipEntry and take the next one
                zipInput.closeEntry();
                entry = zipInput.getNextEntry();
            }

            // close the last ZipEntry
            zipInput.closeEntry();

            zipInput.close();
            fInput.close();

            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not unzip file " + zipFile + "!", e);
            return false;
        }
    }

    /**
     * Delete a folder with subfolders
     *
     * @param folder the folder
     */
    public static void deleteFolder(final File folder) {
        // validate if the folder exists
        if (!folder.exists()) {
            return;
        }

        final File[] files = folder.listFiles();
        // delete files if any
        if (files != null) {
            File[] array;
            for (int length = (array = files).length, i = 0; i < length; ++i) {
                final File f = array[i];
                // fist validate if it is a subfolder or a file
                if (f.isDirectory()) {
                    // delete all the files of the subfolder
                    deleteFolder(f);
                } else {
                    // delete the file
                    f.delete();
                }
            }
        }

        // delete the folder
        folder.delete();
    }

    /**
     * Copy a file
     *
     * @param source source file
     * @param dest   destination file
     *
     * @throws IOException in case any errors
     */
    public static void copyFileUsingFileStreams(File source, File dest) throws IOException {

        try (InputStream input = new FileInputStream(source); OutputStream output = new FileOutputStream(dest)) {
            // create the streams

            // create the byte buffer
            byte[] buf = new byte[1024];
            int bytesRead;
            // read the bytes from the source file
            while ((bytesRead = input.read(buf)) > 0) {
                // write the bytes from the buffer to the destination file
                output.write(buf, 0, bytesRead);
            }
        }
    }

    /**
     * Writes a file's contents to a file writer
     *
     * @param bufferedWriter the writer to write to
     * @param inputStream    the fileinputstream to read
     */
    public static void writeFileToWriter(BufferedWriter bufferedWriter, InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader input = new BufferedReader(reader);

        String currentLine;
        while ((currentLine = input.readLine()) != null) {
            bufferedWriter.write(currentLine + "\n");
        }

        input.close();
    }

    /**
     * Copies a folder
     *
     * @param src  the source folder
     * @param dest the folder where the output will be
     *
     * @throws IOException if there's any error
     */
    private static void copyFolder(final File src, final File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            final String[] files = src.list();
            if (files.length > 0) {
                for (final String file : files) {
                    final File srcFile = new File(src, file);
                    final File destFile = new File(dest, file);
                    copyFolder(srcFile, destFile);
                }
            }
        } else {
            final InputStream in = new FileInputStream(src);
            final OutputStream out = new FileOutputStream(dest);
            final byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    /**
     * Compresses a directory in zip format
     *
     * @param directoryToZip the directory we are going to zip
     * @param zipLocation    the zip file location
     */
    public static void zipIt(File directoryToZip, File zipLocation) {
        List<File> fileList = new ArrayList<>();
        getAllFiles(directoryToZip, fileList);
        writeZipFile(zipLocation, directoryToZip, fileList);
    }

    /**
     * Adds all the files in a directory to a list
     *
     * @param dir      the directory
     * @param fileList the list of files we are going to add the files in
     */
    private static void getAllFiles(File dir, List<File> fileList) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (!file.getName().endsWith(".lock")) {
                fileList.add(file);
            }

            if (file.isDirectory()) {
                getAllFiles(file, fileList);
            }
        }
    }

    /**
     * Writes the zip file
     *
     * @param directoryToZip the zip file we are going to put all the compressed files in
     * @param fileList       the list of files that we are going to compress
     */
    private static void writeZipFile(File directoryToZip, File mainDirectory, List<File> fileList) {
        try (FileOutputStream fos = new FileOutputStream(directoryToZip);
             ZipOutputStream zos = new ZipOutputStream(fos)){

            for (File file : fileList) {
                if (file.isDirectory()) {
                    addFolderToZip(mainDirectory, file, zos);
                } else {
                    addFileToZip(mainDirectory, file, zos);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create zip file " + directoryToZip + "!", e);
        }
    }

    /**
     * Adds the file to a zip file
     *
     * @param mainDirectory the folder where all the files are located
     * @param file          the file to add to the zip file
     * @param zos           the outputstream we are going to write into
     *
     * @throws IOException if there are any errors
     */
    private static void addFileToZip(File mainDirectory, File file, ZipOutputStream zos) throws IOException {
        FileChannel fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
        InputStream fis = Channels.newInputStream(fileChannel);

        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        String zipFilePath = file.getPath().substring(mainDirectory.getPath().length() + 1);
        //System.out.println(zipFilePath);

        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;

        try {
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading file " + file.getName(), e);
        }


        zos.closeEntry();
        fis.close();
    }

    /**
     * Adds the folder to a zip file
     *
     * @param mainDirectory the folder where all the files are located
     * @param file          the file to add to the zip file
     * @param zos           the outputstream we are going to write into
     *
     * @throws IOException if there are any errors
     */
    private static void addFolderToZip(File mainDirectory, File file, ZipOutputStream zos) throws IOException {
        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        String zipFilePath = file.getPath().substring(mainDirectory.getPath().length() + 1) + "/";
        //System.out.println(zipFilePath);

        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);
        zos.closeEntry();
    }


    /**
     * Gets a random element from a stream
     *
     * @param stream the stream
     * @param <E>  the stream type
     *
     * @return the randomly picked element
     */
    public static <E> E getRandomElementFromStream(Stream<E> stream) {
        return getRandomElementFromList(stream.collect(Collectors.toList()));
    }

    /**
     * Gets a random element from a list
     *
     * @param list the list
     * @param <E>  the list type
     *
     * @return the randomly picked element
     */
    public static <E> E getRandomElementFromList(List<E> list) {
        if(list.isEmpty()) {
            return null;
        }

        // RANDOM.nextDouble() returns a number greater than or equal to 0.0 and less than 1
        return list.get((int) (RANDOM.nextDouble() * list.size()));
    }

    /**
     * Gets a random element from a collection
     *
     * @param collection the list
     * @param <E>        the collection type
     *
     * @return the randomly picked element
     */
    public static <E> E getRandomElementFromCollection(Collection<E> collection) {
        return collection.stream().skip((int) (collection.size() * RANDOM.nextDouble())).findFirst().orElse(null);
    }

    /**
     * Copies a file from inside the jar
     *
     * @param plugin   the plugin
     * @param resource the file inside the jar
     *
     * @return the copied file
     */
    public static File loadResource(Plugin plugin, String resource) {
        return loadResource(plugin, resource, plugin.getDataFolder(), resource);
    }

    /**
     * Copies a file from inside the jar
     *
     * @param plugin   the plugin
     * @param resource the file inside the jar
     *
     * @return the copied file
     */
    public static File loadResource(Plugin plugin, String resource, File folder, String finalName) {
        // create the output folder if it doesn't exist
        if (!folder.exists()) {
            folder.mkdir();
        }

        File resourceFile = new File(folder, finalName);

        try {
            // checks if the file already exists to
            // prevent overriding already written files
            if (!resourceFile.exists()) {
                // actually create a file we can use to write in
                resourceFile.createNewFile();

                // copy all the bytes from the original file
                try (InputStream in = plugin.getResource(resource);
                     OutputStream out = new FileOutputStream(resourceFile)) {
                    ByteStreams.copy(in, out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // returns the copied file object
        return resourceFile;
    }

    /**
     * Reads a file as a string
     *
     * @param file the file to read
     * @return the file as a string
     */
    public static String readFileAsString(File file) {
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String temp;
            while ((temp = reader.readLine()) != null) {
                buffer.append(temp).append('\n');
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * List directory contents for a resource folder. Not recursive.
     * This is basically a brute-force implementation.
     * Works for regular files and also JARs.
     *
     * @param clazz Any java class that lives in the same place as the resources you want.
     * @param path  Should end with "/", but not start with one.
     *
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     * @author Greg Briggs
     */
    public static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            // A file path: easy enough
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
        /*
         * In case of a jar file, we can't actually find a directory.
         * Have to assume the same jar as clazz.
         */
            String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            // A JAR path
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<>(); //avoid duplicates in case it is a subdirectory
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { //filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    /**
     * Returns the url contents as as string
     *
     * @param url the url to query
     * @param charset the charset
     * @return the url contents
     * @throws IOException if anything fails
     */
    public static String urlToString(URL url, Charset charset) throws IOException{
        try (InputStream in = url.openStream()) {
            InputStreamReader inR = new InputStreamReader(in, charset);
            BufferedReader buf = new BufferedReader(inR);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = buf.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            return stringBuilder.toString();
        }
    }

    /**
     * Checks if a string array contains the specified string
     *
     * @param array  the string array
     * @param string the string
     *
     * @return if the array contains the string
     */
    public static boolean containsString(String[] array, String string) {
        for (String anArray : array) {
            if (anArray.equalsIgnoreCase(string)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a string is a number
     *
     * @param number the string to validate
     *
     * @return true if its a number
     */
    public static boolean isNumber(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Transforms a DyeColor into a ChatColor
     *
     * @param color the dye color
     *
     * @return the bukkit chat color
     */
    public static ChatColor getColorFromDye(DyeColor color) {
        switch (color) {
            case WHITE:
                return ChatColor.WHITE;
            case ORANGE:
                return ChatColor.GOLD;
            case MAGENTA:
            case PINK:
            case PURPLE:
                return ChatColor.LIGHT_PURPLE;
            case LIGHT_BLUE:
            case CYAN:
                return ChatColor.AQUA;
            case YELLOW:
                return ChatColor.YELLOW;
            case LIME:
                return ChatColor.GREEN;
            case GRAY:
            case BLACK: // BLACK cannot be easily seen in chat
            case BROWN:
                return ChatColor.DARK_GRAY;
            case BLUE:
                return ChatColor.BLUE; // DARK_BLUE cannot be easily seen in chat
            case GREEN:
                return ChatColor.DARK_GREEN;
            case RED:
                return ChatColor.RED;
            //case LIGHT_GRAY:
            //case SILVER:
            // SILVER was renamed to LIGHT_GRAY
            default:
                return ChatColor.GRAY;
        }
    }

    /**
     * Gets the {@link Color} value from its name
     *
     * @param colorName the color name
     *
     * @return the Color
     */
    public static Color getColor(String colorName) {
        switch (colorName.toLowerCase()) {
            case "aqua":
                return Color.AQUA;
            case "black":
                return Color.BLACK;
            case "blue":
                return Color.BLUE;
            case "fuschia":
                return Color.FUCHSIA;
            case "gray":
                return Color.GRAY;
            case "green":
                return Color.GREEN;
            case "lime":
                return Color.LIME;
            case "maroon":
                return Color.MAROON;
            case "olive":
                return Color.OLIVE;
            case "orange":
                return Color.ORANGE;
            case "purple":
                return Color.PURPLE;
            case "red":
                return Color.RED;
            case "silver":
                return Color.SILVER;
            case "teal":
                return Color.TEAL;
            case "white":
                return Color.WHITE;
            case "yellow":
                return Color.YELLOW;
            case "navy":
            default:
                return Color.NAVY;
        }
    }

    /**
     * Gets the {@link Enchantment} from its name using
     * user-friendly names. Why did bukkit think of using
     * weird names such as WATER_WORKER for Aqua Affinity or
     * DIG_SPEED for efficiency
     *
     * @param enchant the enchantment name
     *
     * @return the enchantment
     */
    public static Enchantment getEnchant(String enchant) {
        switch (enchant.toLowerCase()
                .replace("-", "")
                .replace("_", "")
                .replace(" ", "")) {
            case "protection":
                return Enchantment.PROTECTION_ENVIRONMENTAL;
            case "projectileprotection":
                return Enchantment.PROTECTION_PROJECTILE;
            case "fireprotection":
                return Enchantment.PROTECTION_FIRE;
            case "featherfall":
                return Enchantment.PROTECTION_FALL;
            case "blastprotection":
                return Enchantment.PROTECTION_EXPLOSIONS;
            case "respiration":
                return Enchantment.OXYGEN;
            case "aquaaffinity":
                return Enchantment.WATER_WORKER;
            case "sharpness":
                return Enchantment.DAMAGE_ALL;
            case "smite":
                return Enchantment.DAMAGE_UNDEAD;
            case "baneofarthropods":
                return Enchantment.DAMAGE_ARTHROPODS;
            case "knockback":
                return Enchantment.KNOCKBACK;
            case "fireaspect":
                return Enchantment.FIRE_ASPECT;
            case "depthstrider":
                return Enchantment.DEPTH_STRIDER;
            case "looting":
                return Enchantment.LOOT_BONUS_MOBS;
            case "power":
                return Enchantment.ARROW_DAMAGE;
            case "punch":
                return Enchantment.ARROW_KNOCKBACK;
            case "flame":
                return Enchantment.ARROW_FIRE;
            case "infinity":
                return Enchantment.ARROW_INFINITE;
            case "efficiency":
                return Enchantment.DIG_SPEED;
            case "silktouch":
                return Enchantment.SILK_TOUCH;
            case "unbreaking":
                return Enchantment.DURABILITY;
            case "fortune":
                return Enchantment.LOOT_BONUS_BLOCKS;
            case "luckofthesea":
                return Enchantment.LUCK;
            default:
                return Enchantment.getByName(enchant.toUpperCase());
        }
    }


    /**
     * Sends a centered message to the player, assuming they have the
     * default texture pack or a texture pack which does not modify
     * the font size.
     *
     * @param player  the player the message will be sent to
     * @param message the message to send
     */
    public static void sendCenteredMessage(Player player, String message) {
        if (message == null || message.isEmpty()) {
            player.sendMessage("");
            return;
        }
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == ChatColor.COLOR_CHAR) {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        player.sendMessage(sb.toString() + message);
    }

    /**
     * Displays a progress bar on the action bar of the player
     *
     * @param prefix the prefix of the progress bar
     * @param amount the progress percentage (0.0 to 1.0)
     * @param suffix the suffix of the progress bar
     * @param players the players to send the progress bar to
     */
    public static void displayProgress(String prefix, double amount, String suffix, Player... players) {
        int bars = 24;
        StringBuilder progressBar = new StringBuilder(ChatColor.GREEN + "");
        boolean colorChange = false;
        for (int i = 0; i < bars; i++) {
            if (!colorChange && (float) i / (float) bars >= amount) {
                progressBar.append(ChatColor.WHITE);
                colorChange = true;
            }

            progressBar.append("#");
        }

        for (Player player : players) {
            ActionBar.sendActionBar(player, (prefix == null ? "" : prefix + ChatColor.RESET + " ") + progressBar + (suffix == null ? "" : ChatColor.RESET + " " + suffix));
        }
    }

    /**
     * Formats a decimal to one decimal places
     * 2.34567 will become 2.3
     *
     * @param number the number to round
     * @return the rounded number in string format
     */
    public static String roundToOneDecimal(double number) {
        return ONE_DECIMAL_FORMAT.format(number);
    }

    /**
     * Formats a decimal to two decimal places
     * 2.34567 will become 2.34
     *
     * @param number the number to round
     * @return the rounded number in string format
     */
    public static String roundToTwoDecimals(double number) {
        return TWO_DECIMAL_FORMAT.format(number);
    }

    /**
     * Gets a random firework effect
     *
     * @return the random firework effect
     */
    public static FireworkEffect getRandomEffect() {
        FireworkEffect.Builder fireworkEffect = FireworkEffect.builder();
        double randomType = RANDOM.nextDouble();

        if (randomType > 0.5D) {
            fireworkEffect.with(FireworkEffect.Type.BURST);
        } else if (randomType > 0.25D) {
            fireworkEffect.with(FireworkEffect.Type.STAR);
        } else if (randomType > 0.15D) {
            fireworkEffect.with(FireworkEffect.Type.BALL_LARGE);
        } else {
            fireworkEffect.with(FireworkEffect.Type.BALL);
        }

        boolean hasColour = false;

        if (RANDOM.nextDouble() > 0.75D) {
            fireworkEffect.withColor(Color.RED);
            hasColour = true;
        }

        if (RANDOM.nextDouble() > 0.75D) {
            fireworkEffect.withColor(Color.BLUE);
            hasColour = true;
        }

        if (RANDOM.nextDouble() > 0.75D) {
            fireworkEffect.withColor(Color.YELLOW);
            hasColour = true;
        }

        if (RANDOM.nextDouble() > 0.75D) {
            fireworkEffect.withColor(Color.AQUA);
            hasColour = true;
        }

        if (!hasColour) {
            fireworkEffect.withColor(Color.RED);
        }

        return fireworkEffect.build();
    }

    /**
     * Formats a list of strings to go in a lore of an item
     *
     * @param title the title
     * @param lines the lines
     *
     * @return the formatted lines
     */
    public static List<String> orderListForLore(String title, List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return null;
        }

        List<String> newList = new ArrayList<>();
        newList.add(ChatColor.YELLOW + title);
        lines.forEach(line -> newList.add(ChatColor.GRAY + "- " + ChatColor.WHITE + line));
        return newList;
    }

    /**
     * Transforms a collection size to a multiple of nine for
     * bukkit inventories, since they require the size to be a
     * multiple of nine
     *
     * @param collection the collection
     *
     * @return the transformed slot
     */
    public static int transformToInventorySize(Collection<?> collection) {
        return transformToInventorySize(collection.size());
    }

    /**
     * Transforms a number to a multiple of nine for the use in
     * bukkit inventories, since they require the size to be a
     * multiple of nine
     *
     * @param size the size of the list
     *
     * @return the transformed slot
     */
    public static int transformToInventorySize(long size) {
        return Math.min(((int) Math.ceil((Math.max(1, size)) / 9.0)) * 9, 54);
    }

    /**
     * Gets the offset between two locations
     *
     * @param a Location a
     * @param b Location b
     *
     * @return the offset between these two locations
     */
    public static double offset(final Location a, final Location b) {
        return offset(a.toVector(), b.toVector());
    }

    /**
     * Gets the offset between two vectors
     *
     * @param a Vector a
     * @param b Vector b
     *
     * @return the offset between these two vectors
     */
    public static double offset(final Vector a, final Vector b) {
        return a.subtract(b).length();
    }

    /**
     * Returns if a location is between the radius of another location
     *
     * @param baseLocation the base location
     * @param location     the location to check
     * @param radius       the radius
     *
     * @return true if the location is withing the radius of the base location
     */
    public static boolean isLocationInRadius(Location baseLocation, Location location, int radius) {
        return abs(baseLocation.getX() - location.getX()) < radius &&
                abs(baseLocation.getY() - location.getY()) < radius &&
                abs(baseLocation.getZ() - location.getZ()) < radius;
    }

    /**
     * Makes a player "bounce" to a location by applying velocity
     *
     * @param player   the player to bounce
     * @param location the location to bounce to
     */
    public static void bounceTo(Player player, Location location) {
//        Location a = player.getLocation();
//
//        Vector from = new Vector(a.getX(), a.getY(), a.getZ());
//        Vector to = new Vector(b.getX(), b.getY(), b.getZ());
//
//        Vector velocity = to.subtract(from);
//        player.setVelocity(velocity.normalize());
        Vector a = player.getLocation().toVector().setY(0);
        Vector b = location.toVector().setY(0);
        Vector subtract = b.subtract(a);
        player.setVelocity(subtract);
    }

    /**
     * Rotates a vector with a pitch around the x coordinate
     *
     * @param vector the vector
     * @param pitch the pitch
     */
    public static void rotateX(Vector vector, double pitch) {
        double cos = Math.cos(pitch);
        double sin = Math.sin(pitch);
        double y = vector.getY() * cos + vector.getZ() * sin;
        double z = vector.getY() * -sin + vector.getZ() * cos;
        vector.setY(y).setZ(z);
    }

    /**
     * Rotates a vector with a yaw around the y coordinate
     *
     * @param vector the vector
     * @param yaw the yaw
     */
    public static void rotateY(Vector vector, double yaw) {
        double cos = Math.cos(yaw);
        double sin = Math.sin(yaw);
        double x = vector.getX() * cos + vector.getZ() * sin;
        double z = vector.getX() * -sin + vector.getZ() * cos;
        vector.setX(x).setZ(z);
    }

    /**
     * Converts an angle to radians
     *
     * @param angle the angle in degrees
     * @return the angle in radians
     */
    public static double degToRadians(double angle) {
        return angle * Math.PI / 180;
    }

    /**
     * Gets the middle location from a list of locations
     *
     * @param locs the list of locations
     *
     * @return the middle location
     */
    public static Location getCenterLocation(List<ArenaLocation> locs) {
        if (locs.isEmpty()) {
            return null;
        }

        Vector vec = new Vector(0, 0, 0);
        locs.forEach(location -> vec.add(location.toVector()));
        vec.multiply(1.0 / (double) locs.size());
        return vec.toLocation(locs.get(0).getWorld());
    }

    /**
     * Returns the absolute value of a {@code double} value.
     * If the argument is not negative, the argument minus one is returned.
     * If the argument is negative, the negation of the argument is returned.
     *
     * @param a the argument whose absolute value is to be determined
     *
     * @return the absolute value of the argument.
     */
    private static double abs(double a) {
        return (a <= 0.0D) ? 0.0D - a : a - 1;
    }

    /**
     * Checks whether or not a collection contains an element
     *
     * @param collection the collection
     * @param predicate  the predicate
     * @param <T>        the collection type
     *
     * @return true if collection contains element
     */
    public static <T> boolean collectionContains(Collection<T> collection, Predicate<? super T> predicate) {
        return getFromCollection(collection, predicate) != null;
    }

    /**
     * Checks whether or not a stream contains an element
     *
     * @param stream    the stream
     * @param predicate the predicate
     * @param <T>       the collection type
     *
     * @return true if stream contains element
     */
    public static <T> boolean streamContains(Stream<T> stream, Predicate<? super T> predicate) {
        return getFromStream(stream, predicate) != null;
    }

    /**
     * Gets the element from a collection using a predicate
     *
     * @param collection the collection
     * @param predicate  the predicate
     * @param <T>        the collection type
     *
     * @return true the element if the collection contains it or null
     */
    public static <T> T getFromCollection(Collection<T> collection, Predicate<? super T> predicate) {
        return getFromStream(collection.stream(), predicate);
    }

    /**
     * Gets a nameable object from a collection using its name
     *
     * @param collection the collection
     * @param name       the object's name
     * @param <T>        the stream type
     *
     * @return the nameable object if the collection contains it or null
     */
    public static <T extends NameableObject> T getFromCollection(Collection<T> collection, String name) {
        return getFromCollection(collection, t -> t.getName().equalsIgnoreCase(name));
    }

    /**
     * Gets a nameable object from a stream using its name
     *
     * @param stream the stream
     * @param name   the object's name
     * @param <T>    the stream type
     *
     * @return the nameable object if the stream contains it or null
     */
    public static <T extends NameableObject> T getFromStream(Stream<T> stream, String name) {
        return getFromStream(stream, t -> t.getName().equalsIgnoreCase(name));
    }

    /**
     * Gets the element from a stream using a predicate
     *
     * @param stream    the stream
     * @param predicate the predicate
     * @param <T>       the stream type
     *
     * @return the element if the stream contains it or null
     */
    public static <T> T getFromStream(Stream<T> stream, Predicate<? super T> predicate) {
        return stream.filter(predicate).findAny().orElse(null);
    }

    /**
     * Returns a list of strings from a collection of nameable objects
     *
     * @param collection the collection of nameable objects
     * @param <T>        the nameable object
     *
     * @return the list of the object names
     */
    public static <T extends NameableObject> List<String> toList(Collection<T> collection) {
        return toList(collection.stream());
    }

    /**
     * Returns a list of strings from a list of nameable objects
     *
     * @param stream the stream of nameable objects
     * @param <T>    the nameable object
     *
     * @return the list of the object names
     */
    public static <T extends NameableObject> List<String> toList(Stream<T> stream) {
        return stream.map(NameableObject::getName).collect(Collectors.toList());
    }

    /**
     * Divide the first argument by the second and round the result up to the next integer.
     *
     * @param numerator   Assumed to be >= 0
     * @param denominator Assumed to be > 0
     */
    public static int divideRoundingUp(int numerator, int denominator) {
        return (numerator + denominator - 1) / denominator;
    }

    /**
     * Gets the current timestamp using a simple date format. You
     * can specify what format to use. You can use formats such as
     * - yyyy-MM-dd HH:mm:ss.SSS
     * - dd/MM/yyyy
     *
     * @param format the format
     *
     * @return the current timestamp with the specified date format
     * @see SimpleDateFormat
     */
    public static String getCurrentTimeStamp(String format) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);// - dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }

    /**
     * Returns a formatted string in the format mm:ss from the
     * given time
     *
     * @param pTime the time in seconds
     *
     * @return the mm:ss formatted time
     */
    public static String secondsToString(long pTime) {
        return pTime > 3600 ? String.format("%02d:%02d:%02d", pTime / 3600, pTime / 60 % 60, pTime % 60) : String.format("%02d:%02d", pTime / 60, pTime % 60);
    }

    /**
     * Gets the number of the week of the year using the gregorian calendar
     *
     * @return the week of the year
     */
    public static int getWeekOfTheYear() {
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Gets the month number of the year
     * <p>
     * Since apparently January = 0, we add 1 so that January = 1
     *
     * @return the month number
     */
    public static int getMonthOfTheYear() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * Gets the year we are in
     *
     * @return the year
     */
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }


    /**
     * Formats a location into a nice string
     *
     * @param location      the location to format
     * @param commandSender the sender for the localization
     *
     * @return the formatted string
     */
    public static String locationToString(Location location, CommandSender commandSender) {
        return CoreLang.LOCATION_TO_INFO.replace(commandSender, location.getWorld().getName(), location.getX() + ", " + location.getY() + ", " + location.getZ());
    }

    /**
     * Formats a location into a nice message containing only the coordinates
     *
     * @param location      the location to format
     * @param commandSender the sender for the localization
     *
     * @return the formatted string
     */
    public static String locationToCoords(Location location, CommandSender commandSender) {
        return CoreLang.LOCATION_TO_COORDS.replace(commandSender, location.getX() + ", " + location.getY() + ", " + location.getZ());
    }


    /**
     * Capitalizes only the first letter of a string, really
     * useful when using enums
     *
     * @param originalString the string to capitalize
     *
     * @return the capitalized string
     */
    public static String capitalizeFirstLetter(String originalString) {
        return originalString.toUpperCase().charAt(0) + originalString.toLowerCase().substring(1);
    }

    /**
     * Converts a List of SerializableObject to a List<Map<String, Object>>
     *
     * @param serializableObjects the list of serializable objects
     *
     * @return the list of serialized objects
     */
    public static List<Map<String, Object>> serializeList(List<? extends SerializableObject> serializableObjects) {
        return serializableObjects.stream().map(SerializableObject::serialize).collect(Collectors.toList());
    }

    /**
     * Deserialize a list of Map<String, Object> to a List of elements
     * using a mapper
     *
     * @param list   the list
     * @param mapper the mapper
     * @param <E>    the element type
     *
     * @return the list of deserialized objects
     */
    public static <E extends SerializableObject> List<E> deserializeList(List<Map<String, Object>> list, Function<Map<String, Object>, E> mapper) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * Prints a formatted error message
     *
     * @param lines the error lines
     */
    public static void printError(Object... lines) {
        logger.log(Level.SEVERE, "");
        logger.log(Level.SEVERE, "******** ERROR ********");
        logger.log(Level.SEVERE, PluginConfig.PLUGIN_NAME);
        for (Object line : lines) {
            if(line instanceof Throwable) {
                logger.log(Level.SEVERE, "", (Throwable) line);
                continue;
            }

            if(line instanceof Iterable) {
                for (Object o : ((Iterable) line)) {
                    logger.log(Level.SEVERE, String.valueOf(o));
                }

                continue;
            }

            logger.log(Level.SEVERE, String.valueOf(line));
        }
        logger.log(Level.SEVERE, "***********************");
        logger.log(Level.SEVERE, "");
    }

    /**
     * Prints a formatted debug message
     *
     * @param lines the debug lines
     */
    public static void printDebug(Object... lines) {
        System.out.println();
        System.out.println("******** DEBUG ********");
        System.out.println(PluginConfig.PLUGIN_NAME);
        for (Object line : lines) {
            if(line instanceof Throwable) {
                ((Throwable) line).printStackTrace(System.out);
                continue;
            }

            if(line instanceof Iterable) {
                for (Object o : ((Iterable) line)) {
                    System.out.println(o);
                }

                continue;
            }

            System.out.println(line);
        }
        System.out.println("***********************");
        System.out.println();
    }

    /**
     * Sets the max amount of players the server has
     *
     * @param maxPlayerCount the max amount of players
     *
     * @throws ReflectiveOperationException in case any errors
     */
    public static void setMaxPlayers(int maxPlayerCount) throws ReflectiveOperationException {
        Object playerlist = getCBSClass("CraftServer").getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
        Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
        maxplayers.setAccessible(true);
        maxplayers.set(playerlist, maxPlayerCount);
    }

    // -------------------------------------------- //
    // NO AI - START
    // -------------------------------------------- //
    // Credits to vemacs for this
    // https://gist.github.com/vemacs/6cd43a50950796458984

    private static Method getHandle;
    private static Method getNBTTag;
    private static Class<?> nmsEntityClass;
    private static Class<?> nbtTagClass;
    private static Method c;
    private static Method setInt;
    private static Method f;

    public static void setAiEnabled(Entity entity, boolean enabled) {
        try {
            ((LivingEntity)entity).setAI(enabled);
        } catch (Throwable e) {
            try {
                if (getHandle == null) {
                    Class<?> craftEntity = getCBSClass("entity.CraftEntity");
                    assert craftEntity != null;
                    getHandle = craftEntity.getDeclaredMethod("getHandle");
                    getHandle.setAccessible(true);
                }

                Object nmsEntity = getHandle.invoke(entity);

                if (nmsEntityClass == null) {
                    nmsEntityClass = getNMSClass("Entity");
                }

                if (getNBTTag == null) {
                    assert nmsEntityClass != null;
                    getNBTTag = nmsEntityClass.getDeclaredMethod("getNBTTag");
                    getNBTTag.setAccessible(true);
                }

                Object tag = getNBTTag.invoke(nmsEntity);
                if (nbtTagClass == null) {
                    nbtTagClass = getNMSClass("NBTTagCompound");
                }

                if (tag == null) {
                    assert nbtTagClass != null;
                    tag = nbtTagClass.newInstance();
                }

                if (c == null) {
                    c = nmsEntityClass.getDeclaredMethod("c", nbtTagClass);
                    c.setAccessible(true);
                }

                c.invoke(nmsEntity, tag);
                if (setInt == null) {
                    setInt = nbtTagClass.getDeclaredMethod("setInt", String.class, Integer.TYPE);
                    setInt.setAccessible(true);
                }

                int value = enabled ? 0 : 1;
                setInt.invoke(tag, "NoAI", value);

                if (f == null) {
                    f = nmsEntityClass.getDeclaredMethod("f", nbtTagClass);
                    f.setAccessible(true);
                }

                f.invoke(nmsEntity, tag);
            } catch (Exception e2) {
                logger.log(Level.SEVERE, "Could not set ai state!", e);
            }
        }
    }

    // -------------------------------------------- //
    // NO AI - END
    // -------------------------------------------- //

    /**
     * Gets a GameProfile (for itemstack heads)
     *
     * @return the GameProfile object
     * @throws Exception in case any errors
     */
    public static Object createGameProfile() throws Exception {
        if (gameProfileConstructor == null) {
            Class<?> gameProfileClass;
            try { // 1.7
                gameProfileClass = Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
            } catch (ClassNotFoundException e) { // 1.8
                gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
            }
            gameProfileConstructor = gameProfileClass.getDeclaredConstructor(UUID.class, String.class);
            gameProfileConstructor.setAccessible(true);
        }

        return gameProfileConstructor.newInstance(UUID.randomUUID(), null);
    }

    /**
     * Gets a GameProfile (for itemstack heads)
     *
     * @return the GameProfile object
     * @throws Exception in case any errors
     */
    public static Object createProperty(String propery, String value) throws Exception {
        if (propertyConstructor == null) {
            Class<?> propertyClass;
            try { // 1.7
                propertyClass = Class.forName("net.minecraft.util.com.mojang.authlib.properties.Property");
            } catch (ClassNotFoundException e) { // 1.8
                propertyClass = Class.forName("com.mojang.authlib.properties.Property");
            }
            propertyConstructor = propertyClass.getDeclaredConstructor(String.class, String.class);
            propertyConstructor.setAccessible(true);
        }

        return propertyConstructor.newInstance(propery, value);
    }

    public static SplittableRandom getRandom() {
        return RANDOM;
    }
}