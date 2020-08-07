package me.patothebest.gamecore.itemstack;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.EnchantGlow;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * The ItemStackBuilder.
 */
public class ItemStackBuilder extends ItemStack {

    private static final boolean ISFLAT = Material.isNewVersion();

    /**
     * Instantiates a new item stack builder with no material.
     */
    public ItemStackBuilder() {
        this(org.bukkit.Material.QUARTZ);
    }

    /**
     * Instantiates a new item stack builder.
     *
     * @param material the material
     */
    public ItemStackBuilder(org.bukkit.Material material) {
        super(material);
    }

    public ItemStackBuilder(Material material) {
        super(material.parseMaterial(true));
        material(material);
    }


    /**
     * Instantiates a new item stack builder copying all the
     * attributes from another itemstack.
     *
     * @param itemStack the item stack
     */
    public ItemStackBuilder(ItemStack itemStack) {
        setData(itemStack.getData());
        data(itemStack.getDurability());
        setAmount(itemStack.getAmount());
        setType(itemStack.getType());
        setItemMeta(itemStack.getItemMeta());
    }

    /**
     * Instantiates a new item stack builder copying attributes from
     * another itemstack, everything except the item meta.
     *
     * @param itemStack the item stack
     */
    public ItemStackBuilder(ItemStack itemStack, Object n) {
        setData(itemStack.getData());
        data(itemStack.getDurability());
        setAmount(itemStack.getAmount());
        setType(itemStack.getType());
    }

    /**
     * Sets the itemstack material.
     *
     * @param material the material
     * @return the item stack builder
     */
    public ItemStackBuilder material(org.bukkit.Material material) {
        setType(material);
        return this;
    }


    /**
     * Sets the itemstack material.
     *
     * @param material the material
     * @return the item stack builder
     */
    public ItemStackBuilder material(Material material) {
        setType(material.parseMaterial(true));
        if (!Material.isNewVersion()) {
            data(material.getData());
        }
        return this;
    }

    /**
     * Adds the specified amount to the itemstack amount.
     *
     * @param change the amount to add
     * @return the item stack builder
     */
    public ItemStackBuilder addAmount(int change) {
        setAmount(getAmount() + change);
        return this;
    }

    /**
     * Sets the itemstack amount
     *
     * @param amount the amount
     * @return the item stack builder
     */
    public ItemStackBuilder amount(int amount) {
        setAmount(amount);
        return this;
    }

    /**
     * Data.
     *
     * @param data the data
     * @return the item stack builder
     */
    @Deprecated
    public ItemStackBuilder data(short data) {
        setDurability(data);
        return this;
    }

    /**
     * Data.
     *
     * @param data the data
     * @return the item stack builder
     */
    @Deprecated
    public ItemStackBuilder data(MaterialData data) {
        setData(data);
        return this;
    }

    @SuppressWarnings("ConstantConditions")
    public ItemStackBuilder color(DyeColor color) {
        if (ISFLAT) {
            String type = getType().name();
            if (type.endsWith("WOOL")) setType(org.bukkit.Material.getMaterial(color.name() + "_WOOL"));
            else if (type.endsWith("BED")) setType(org.bukkit.Material.getMaterial(color.name() + "_BED"));
            else if (type.endsWith("STAINED_GLASS")) setType(org.bukkit.Material.getMaterial(color.name() + "_STAINED_GLASS"));
            else if (type.endsWith("STAINED_GLASS_PANE")) setType(org.bukkit.Material.getMaterial(color.name() + "_STAINED_GLASS_PANE"));
            else if (type.endsWith("TERRACOTTA")) setType(org.bukkit.Material.getMaterial(color.name() + "_TERRACOTTA"));
            else if (type.endsWith("GLAZED_TERRACOTTA")) setType(org.bukkit.Material.getMaterial(color.name() + "_GLAZED_TERRACOTTA"));
            else if (type.endsWith("BANNER")) setType(org.bukkit.Material.getMaterial(color.name() + "_BANNER"));
            else if (type.endsWith("WALL_BANNER")) setType(org.bukkit.Material.getMaterial(color.name() + "_WALL_BANNER"));
            else if (type.endsWith("CARPET")) setType(org.bukkit.Material.getMaterial(color.name() + "_CARPET"));
            else if (type.endsWith("SHULKER_BOX")) setType(org.bukkit.Material.getMaterial(color.name() + "_SHULKERBOX"));
            else if (type.endsWith("CONCRETE")) setType(org.bukkit.Material.getMaterial(color.name() + "_CONCRETE"));
            else if (type.endsWith("CONCRETE_POWDER")) setType(org.bukkit.Material.getMaterial(color.name() + "_CONCRETE_POWDER"));
            return this;
        }

        data(color.getWoolData());
        return this;
    }

    /* (non-Javadoc)
     * @see org.bukkit.inventory.ItemStack#setAmount(int)
     */
    @Override
    public void setAmount(int amount) {
        super.setAmount(Math.max(1, amount));
    }

    /**
     * Add a map of enchantments and enchantmnent level to the itemstack.
     *
     * @param enchantments the enchantments to add
     * @return the item stack builder
     */
    public ItemStackBuilder enchantments(HashMap<Enchantment, Integer> enchantments) {
        getEnchantments().keySet().forEach(this::removeEnchantment);
        addUnsafeEnchantments(enchantments);
        return this;
    }

    /**
     * Enchants the itemstack.
     *
     * @param enchantment the enchantment
     * @param level the level
     * @return the item stack builder
     */
    public ItemStackBuilder enchant(Enchantment enchantment, int level) {
        addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Sets the item name.
     *
     * @param player the player
     * @param lang the lang
     * @return the item stack builder
     */
    public ItemStackBuilder name(Player player, ILang lang) {
        return name(lang.getMessage(player));
    }

    /**
     * Sets the item name.
     *
     * @param player the player
     * @param lang the lang
     * @return the item stack builder
     */
    public ItemStackBuilder name(IPlayer player, ILang lang) {
        return name(lang.getMessage(player));
    }

    /**
     * Sets the item name.
     *
     * @param name the name
     * @return the item stack builder
     */
    public ItemStackBuilder name(String name) {
        if (name == null) {
            return this;
        }

        ItemMeta itemMeta = getItemMeta();
        itemMeta.setDisplayName(name.equals("") ? " " : format(name));
        setItemMeta(itemMeta);
        return this;
    }

    /**
     * Sets the MaterialData for this item stack
     *
     * @param data New MaterialData for this item
     * @see ItemStack#setData(MaterialData)
     */
    @Override
    public void setData(MaterialData data) {
        Field dataField;
        try {
            dataField = ItemStack.class.getDeclaredField("data");
            dataField.setAccessible(true);
            dataField.set(this, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a glow effect to the item.
     *
     * @param glowing the glowing
     * @return the item stack builder
     */
    public ItemStackBuilder glowing(boolean glowing) {
        if (glowing) {
            this.addEnchantment(EnchantGlow.getGlow(), 4);
        }
        
        return this;
    }

    /**
     * Adds an enchantment to the book
     *
     * @param ench the enchantment
     * @param level the level
     * @return the item stack builder
     */
    public ItemStackBuilder enchantedBook(Enchantment ench, int level) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta)getItemMeta();
        meta.addEnchant(ench, level, true);
        return this;
    }

    /**
     * Sets the leather armor color.
     *
     * @param red the red
     * @param green the green
     * @param blue the blue
     * @return the item stack builder
     */
    public ItemStackBuilder color(int red, int green, int blue) {
        return color(Color.fromRGB(red, green, blue));
    }

    /**
     * Sets the leather armor color.
     *
     * @param color the color
     * @return the item stack builder
     */
    public ItemStackBuilder color(Color color) {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) getItemMeta();
        leatherArmorMeta.setColor(color);
        setItemMeta(leatherArmorMeta);
        return this;
    }

    /**
     * Creates a confirm item.
     *
     * @return the item stack builder
     */
    public ItemStackBuilder createConfirmItem() {
        material(org.bukkit.Material.EMERALD_BLOCK);
        name(ChatColor.GREEN + "CONFIRM");
        return this;
    }

    /**
     * Creates a cancel item.
     *
     * @return the item stack builder
     */
    public ItemStackBuilder createCancelItem() {
        material(org.bukkit.Material.REDSTONE_BLOCK);
        name(ChatColor.GREEN + "CANCEL");
        return this;
    }

    /**
     * Creates a back item.
     *
     * @param player the player
     * @return the item stack builder
     */
    public ItemStackBuilder createBackItem(Player player) {
        material(org.bukkit.Material.ARROW);
        name(CoreLang.GUI_BACK.getMessage(player));
        return this;
    }

    /**
     * Creates a togglable item.
     *
     * @param player the player
     * @param enabled the enabled
     * @return the item stack builder
     */
    public ItemStackBuilder createTogglableItem(Player player, boolean enabled) {
        if(enabled) {
            return createEnabledItem(player);
        }

        return createDisabledItem(player);
    }

    /**
     * Creates the enabled item.
     *
     * @param player the player
     * @return the item stack builder
     */
    public ItemStackBuilder createEnabledItem(Player player) {
        material(Material.LIME_DYE);
        name(CoreLang.GUI_ENABLED.getMessage(player));
        return this;
    }

    /**
     * Creates a disabled item.
     *
     * @param player the player
     * @return the item stack builder
     */
    public ItemStackBuilder createDisabledItem(Player player) {
        material(Material.GRAY_DYE);
        name(CoreLang.GUI_DISABLED.getMessage(player));
        return this;
    }

    /**
     * Creates a togglable slimball.
     *
     * @param player the player
     * @param enabled the enabled
     * @return the item stack builder
     */
    public ItemStackBuilder createTogglableSlimball(Player player, boolean enabled) {
        if(enabled) {
            return createEnabledSlimeball(player);
        }

        return createDisableSlimeball(player);
    }

    /**
     * Creates the enabled slimeball.
     *
     * @param player the player
     * @return the item stack builder
     */
    public ItemStackBuilder createEnabledSlimeball(Player player) {
        material(org.bukkit.Material.MAGMA_CREAM);
        name(CoreLang.GUI_ENABLED.getMessage(player));
        return this;
    }

    /**
     * Creates the disable slimeball.
     *
     * @param player the player
     * @return the item stack builder
     */
    public ItemStackBuilder createDisableSlimeball(Player player) {
        material(org.bukkit.Material.SLIME_BALL);
        name(CoreLang.GUI_DISABLED.getMessage(player));
        return this;
    }

    /**
     * Adds a blank line to the lore.
     *
     * @return the item stack builder
     */
    public ItemStackBuilder blankLine() {
        addLore(" ");
        return this;
    }

    /**
     * Adds a line separator to the lore.
     *
     * @return the item stack builder
     */
    public ItemStackBuilder lineLore() {
        return lineLore(20);
    }

    /**
     * Adds a separator line to the lore.
     *
     * @param length the length
     * @return the item stack builder
     */
    public ItemStackBuilder lineLore(int length) {
        addLore("&8&m&l" + Strings.repeat("-", length));
        return this;
    }

    /**
     * Creates and sets the skull owner.
     *
     * @param owner the owner
     * @return the item stack builder
     */
    public ItemStackBuilder skullOwner(String owner) {
        material(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD.parseMaterial());
        meta.setOwner(owner);
        setItemMeta(meta);
        return this;
    }

    /**
     * Creates a custom skull based on a URL.
     *
     * @param url the url
     * @return the item stack builder
     */
    public ItemStackBuilder customSkull(String url) {
        createPlayerHead();
        SkullMeta headMeta = (SkullMeta) getItemMeta();
        try {
            Object profile = Utils.createGameProfile();
            Object profileProperties = Utils.invokeMethod(profile, "getProperties", null);
            Object property = Utils.createProperty("textures", url);
            Utils.invokeMethod(profileProperties, Utils.getMethodNotDeclaredValue(profileProperties.getClass(), "put", Object.class, Object.class), "textures", property);
            Field profileField;
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        setItemMeta(headMeta);
        return this;
    }

    @Override
    public ItemStackBuilder clone() {
        return (ItemStackBuilder) super.clone();
    }

    /**
     * Creates a player head.
     *
     * @return the item stack builder
     */
    public ItemStackBuilder createPlayerHead() {
        material(Material.PLAYER_HEAD);
        return this;
    }

    /**
     * Adds the lore.
     *
     * @param lore the lore
     * @return the item stack builder
     */
    public ItemStackBuilder addLore(String... lore) {
        if (lore == null) {
            return this;
        }
        ItemMeta itemMeta = getItemMeta();
        List<String> original = itemMeta.getLore();
        if (original == null)
            original = new ArrayList<>();
        Collections.addAll(original, format(lore));
        itemMeta.setLore(original);
        setItemMeta(itemMeta);
        return this;
    }

    /**
     * Adds the lore.
     *
     * @param lore the lore
     * @return the item stack builder
     */
    public ItemStackBuilder addLore(List<String> lore) {
        if (lore == null) {
            return this;
        }
        
        ItemMeta itemMeta = getItemMeta();
        List<String> original = itemMeta.getLore();
        if (original == null)
            original = new ArrayList<>();
        original.addAll(format(lore));
        itemMeta.setLore(original);
        setItemMeta(itemMeta);
        return this;
    }

    /**
     * Lore.
     *
     * @param lore the lore
     * @return the item stack builder
     */
    public ItemStackBuilder lore(String lore) {
        if (lore == null) {
            return this;
        }

        if(!lore.contains("\n")) {
            ItemMeta itemMeta = getItemMeta();
            itemMeta.setLore(format(Collections.singletonList(lore)));
            setItemMeta(itemMeta);
            return this;
        }

        return lore(lore.split("\n"));
    }

    /**
     * Set the lore.
     *
     * @param lore the lore
     * @return the item stack builder
     */
    public ItemStackBuilder lore(String... lore) {
        if (lore == null) {
            return this;
        }

        ItemMeta itemMeta = getItemMeta();
        itemMeta.setLore(format(Lists.newArrayList(lore)));
        setItemMeta(itemMeta);
        return this;
    }

    /**
     * Set the lore.
     *
     * @param lore the lore
     * @return the item stack builder
     */
    public ItemStackBuilder lore(List<String> lore) {
        if (lore == null) {
            return this;
        }
        ItemMeta itemMeta = getItemMeta();
        itemMeta.setLore(format(lore));
        setItemMeta(itemMeta);
        return this;
    }

    /**
     * Format a string.
     *
     * @param string the string
     * @return the string
     */
    public String format(String string) {
        return (string == null ? null : string.replace("&", "§"));
    }

    /**
     * Format a string.
     *
     * @param strings the strings
     * @return the string[]
     */
    public String[] format(String[] strings) {
        return format(Arrays.asList(strings)).toArray(new String[strings.length]);
    }

    /**
     * Format a string.
     *
     * @param strings the strings
     * @return the list
     */
    public List<String> format(List<String> strings) {
        List<String> collection = new ArrayList<>();
        for (String string : strings) {
            if (string == null || string.isEmpty()) {
                continue;
            }
            collection.add(format(string));
        }
        return collection;
    }
}