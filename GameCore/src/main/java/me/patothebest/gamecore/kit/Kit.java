package me.patothebest.gamecore.kit;

import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.GroupPermissible;
import me.patothebest.gamecore.permission.PermissionGroup;
import me.patothebest.gamecore.permission.PermissionGroupManager;
import me.patothebest.gamecore.storage.Storage;
import me.patothebest.gamecore.util.ThrowableRunnable;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.NameableObject;
import me.patothebest.gamecore.util.ThrowableConsumer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Kit implements ConfigurationSerializable, GroupPermissible, NameableObject {

    private final CorePlugin plugin;
    private final List<WrappedPotionEffect> potionEffects;
    private final List<String> description;

    private KitFile kitFile;
    private Provider<Storage> storageProvider;

    private String kitName;
    private PermissionGroup permissionGroup;
    private ItemStack[] armorItems;
    private ItemStack[] inventoryItems;
    private ItemStack displayItem;

    private boolean oneTimeKit;
    private boolean enabled;
    private double cost;
    private final static int[] DEFAULT_LAYOUT = {
             0,  1,  2,  3,  4,  5,  6,  7,  8,
             9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    public Kit(CorePlugin plugin, PermissionGroupManager permissionGroupManager, String kitName, ItemStack[] armorItems, ItemStack[] inventoryItems) {
        this.plugin = plugin;
        this.kitName = kitName;
        this.armorItems = armorItems;
        this.inventoryItems = inventoryItems;
        this.description = new ArrayList<>();
        this.potionEffects = new ArrayList<>();
        this.displayItem = inventoryItems[0] == null ? new ItemStackBuilder().material(Material.DIRT) : inventoryItems[0];
        this.enabled = false;
        this.permissionGroup = permissionGroupManager.getDefaultPermissionGroup();
        this.oneTimeKit = false;
        this.cost = 0;

        this.kitFile = new KitFile(this);
        kitFile.save();
    }

    public Kit(CorePlugin plugin, PermissionGroupManager permissionGroupManager, String kitName) {
        this.plugin = plugin;
        this.kitName = kitName;
        this.kitFile = new KitFile(this);
        Map<String, Object> data = kitFile.getConfigurationSection("data").getValues(true);

        this.description = (List<String>) data.get("description");
        this.displayItem = Utils.itemStackFromString((String) data.get("display-item"));
        this.inventoryItems = (((ArrayList<ItemStack>) data.get("items")).toArray(new ItemStack[((ArrayList<ItemStack>) data.get("items")).size()]));
        this.armorItems = (((ArrayList<ItemStack>) data.get("armor")).toArray(new ItemStack[((ArrayList<ItemStack>) data.get("armor")).size()]));
        this.potionEffects = (List<WrappedPotionEffect>) data.get("potion-effects");
        this.oneTimeKit = (boolean) data.get("one-time-kit");
        this.cost = (double) data.get("cost");
        this.enabled = (boolean) data.get("enabled");

        if(data.get("permission-group") != null) {
            permissionGroup = permissionGroupManager.getOrCreatePermissionGroup((String) data.get("permission-group"));
        } else {
            permissionGroup = permissionGroupManager.getDefaultPermissionGroup();
        }
    }

    @AssistedInject private Kit(CorePlugin plugin, PermissionGroupManager permissionGroupManager, Provider<Storage> storageProvider, @Assisted ResultSet resultSet) {
        this.plugin = plugin;
        this.storageProvider = storageProvider;
        this.description = new ArrayList<>();
        this.potionEffects = new ArrayList<>();


        tryLoad(() -> {
            this.kitName = resultSet.getString("name");
            this.permissionGroup = permissionGroupManager.getOrCreatePermissionGroup(resultSet.getString("permission_group"));
            this.oneTimeKit = resultSet.getInt("one_time_kit") == 1;
            this.cost = resultSet.getDouble("price");
            this.enabled = resultSet.getInt("enabled") == 1;
        }, e -> {
            System.out.println("Error loading kit " + kitName);
            e.printStackTrace();
        });

        tryLoad(() -> {
            this.description.addAll(Arrays.asList(resultSet.getString("description").split("%delimiter%")));
        }, (e) -> {
            System.err.println("Could not load kit " + kitName + " description");
            e.printStackTrace();
        });

        tryLoad(() -> {
            this.displayItem = Utils.itemStackFromString(resultSet.getString("display_item"));
        }, (e) -> {
            String display_item = resultSet.getString("display_item");
            System.err.println("Could not load kit " + kitName + " display_item." + display_item + " not found.");
            e.printStackTrace();
        });

        tryLoad(() -> {
            this.inventoryItems = Utils.itemStackArrayFromBase64(resultSet.getString("items"));
        }, (e) -> {
            System.err.println("Could not load kit " + kitName + " inventory items.");
            e.printStackTrace();
        });

        tryLoad(() -> {
            this.armorItems = Utils.itemStackArrayFromBase64(resultSet.getString("armor"));
        }, (e) -> {
            System.err.println("Could not load kit " + kitName + " armor items.");
            e.printStackTrace();
        });

        tryLoad(() -> {
            this.potionEffects.addAll(Arrays.asList(Utils.potionEffectsFromBase64(resultSet.getString("potion_effects"))));
        }, (e) -> {
            System.err.println("Could not load kit " + kitName + " potion effects.");
            e.printStackTrace();
        });

        if (displayItem != null) {
            this.displayItem.setItemMeta(null);
        }
    }

    private void tryLoad(ThrowableRunnable<Exception> runnable, ThrowableConsumer<Exception> defaultRunnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            try {
                defaultRunnable.acceptThrows(e);
            } catch (Exception ex) {
                System.out.println("Exception handing exception");
                ex.printStackTrace();
            }
        }
    }

    @AssistedInject private Kit(CorePlugin plugin, PermissionGroupManager permissionGroupManager, Provider<Storage> storageProvider, @Assisted String kitName, @Assisted PlayerInventory playerInventory) {
        this.plugin = plugin;
        this.kitName = kitName;
        this.storageProvider = storageProvider;
        this.armorItems = playerInventory.getArmorContents();
        this.inventoryItems = playerInventory.getContents();
        this.description = new ArrayList<>();
        this.potionEffects = new ArrayList<>();
        this.displayItem = inventoryItems[0] == null ? new ItemStackBuilder().material(Material.DIRT) : inventoryItems[0];
        this.enabled = false;
        this.permissionGroup = permissionGroupManager.getDefaultPermissionGroup();
        this.oneTimeKit = false;
        this.cost = 0;
        this.displayItem.setItemMeta(null);
    }

    public void applyKit(IPlayer player) {
        ItemStack[] inventoryItemsCopy = new ItemStack[inventoryItems.length];
        ItemStack[] armorItemsCopy = armorItems.clone();
        int[] layout = player.getLayout(this) == null ? DEFAULT_LAYOUT : player.getLayout(this).getRemap();

        for (int i = 0; i < inventoryItems.length; ++i) {
            ItemStack item = inventoryItems[i] != null ? inventoryItems[i].clone() : null;

            if (item != null && item.getType() != org.bukkit.Material.AIR) {
                if (i >= 36) {
                    inventoryItemsCopy[i] = new ItemStackBuilder(item).lore(ChatColor.YELLOW + "Kit " + kitName);
                } else {
                    inventoryItemsCopy[layout[i]] = new ItemStackBuilder(item).lore(ChatColor.YELLOW + "Kit " + kitName);
                }
            }
        }

        for (int i = 0; i < armorItemsCopy.length; ++i) {
            final ItemStack item = armorItemsCopy[i];

            if (item != null && item.getType() != org.bukkit.Material.AIR) {
                armorItemsCopy[i] = new ItemStackBuilder(item).lore(ChatColor.YELLOW + "Kit " + kitName);
            }
        }

        player.getPlayerInventory().clearPlayer();
        for (int i = 0; i < player.getPlayer().getInventory().getContents().length && i < inventoryItemsCopy.length; i++) {
            player.getPlayer().getInventory().setItem(i, inventoryItemsCopy[i]);
        }

        player.getPlayer().getInventory().setArmorContents(armorItemsCopy);
    }

    public void applyPotionEffects(Player player) {
        potionEffects.forEach(player::addPotionEffect);
    }

    public void delete() {
        if(kitFile != null) {
            kitFile.delete();
        } else if(storageProvider != null) {
            storageProvider.get().deleteKit(this);
        }
    }

    public void save() {
        if(kitFile != null) {
            kitFile.save();
        } else if(storageProvider != null) {
            storageProvider.get().saveKit(this);
        }
    }

    public void saveToFile() {
        if(kitFile == null) {
            this.kitFile = new KitFile(this);
        }

        kitFile.save();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", kitName);
        result.put("description", description);

        result.put("display-item", Utils.itemStackToString(displayItem));
        result.put("items", inventoryItems);
        result.put("armor", armorItems);
        result.put("potion-effects", potionEffects.toArray(new WrappedPotionEffect[potionEffects.size()]));

        result.put("permission-group", permissionGroup.getName());
        result.put("one-time-kit", oneTimeKit);
        result.put("enabled", enabled);

        result.put("cost", cost);
        return result;
    }

    public String getKitName() {
        return kitName;
    }

    @Override
    public String getName() {
        return kitName;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }

    public List<String> getDescription() {
        return description;
    }

    public ItemStack[] getArmorItems() {
        return armorItems;
    }

    public void setArmorItems(ItemStack[] armorItems) {
        this.armorItems = armorItems;
    }

    public ItemStack[] getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(ItemStack[] inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public ItemStackBuilder finalDisplayItem(IPlayer player, boolean extraLore) {
        return finalDisplayItem(player, extraLore, false);
    }

    public ItemStackBuilder finalDisplayItem(IPlayer player, boolean extraLore, boolean showChooseDefaultKit) {
        if (displayItem == null) {
            return new ItemStackBuilder(Material.REDSTONE_BLOCK).name(ChatColor.RED + "ERROR KIT" + kitName);
        }

        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(displayItem).name(ChatColor.GREEN + kitName);

        if(extraLore) {
            description.forEach(s -> itemStackBuilder.addLore(ChatColor.GRAY + s));

            itemStackBuilder.blankLine();
            itemStackBuilder.addLore(CoreLang.GUI_SHOP_PRICE.replace(player, (isFree() ? CoreLang.GUI_SHOP_FREE.getMessage(player) : cost + "")));

            if (permissionGroup.hasPermission(player)) {
                if (player.getKit() == this) {
                    if (!isFree() && oneTimeKit) {
                        itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_KIT_USES.replace(player, player.getKitUses().getOrDefault(this, 0)));
                    }

                    itemStackBuilder.blankLine();
                    itemStackBuilder.glowing(true);

                    if (!isFree() && oneTimeKit) {
                        itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_RIGHT_CLICK.getMessage(player));
                    }

                    itemStackBuilder.addLore(CoreLang.GUI_SHOP_SELECTED.getMessage(player));
                } else if (isFree()) {
                    itemStackBuilder.blankLine();
                    if(showChooseDefaultKit) {
                        itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_CLICK_DEFAULT.getMessage(player));
                    } else {
                        itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_CLICK_SELECT.getMessage(player));
                    }
                } else if (oneTimeKit) {
                    if (player.canUseKit(this)) {
                        itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_KIT_USES.replace(player, player.getKitUses().get(this)));
                        itemStackBuilder.blankLine();
                        itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_RIGHT_CLICK.getMessage(player));

                        if (showChooseDefaultKit) {
                            itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_LEFT_CLICK.getMessage(player));
                        } else {
                            itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_LEFT_CLICK_SELECT.getMessage(player));
                        }
                    } else {
                        itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_KIT_USES.replace(player, 0));
                        itemStackBuilder.blankLine();
                        itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_CLICK_BUY.getMessage(player));
                    }

                } else {
                    itemStackBuilder.blankLine();

                    if (player.canUseKit(this)) {
                        if(showChooseDefaultKit) {
                            itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_CLICK_DEFAULT.getMessage(player));
                        } else {
                            itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_CLICK_SELECT.getMessage(player));
                        }
                    } else {
                        itemStackBuilder.addLore(CoreLang.GUI_KIT_SHOP_CLICK_BUY_PERMANENT.replace(player));
                    }
                }
            } else {
                itemStackBuilder.addLore(CoreLang.GUI_SHOP_NO_PERMISSION.replace(player, permissionGroup.getName()));
            }

        }

        return itemStackBuilder;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        this.displayItem.setItemMeta(null);
    }

    public boolean isOneTimeKit() {
        return oneTimeKit;
    }

    public void setOneTimeKit(boolean oneTimeKit) {
        this.oneTimeKit = oneTimeKit;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFree() {
        return cost < 1;
    }

    public List<WrappedPotionEffect> getPotionEffects() {
        return potionEffects;
    }

    @Override
    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }

    @Override
    public void setPermissionGroup(PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    @Override
    public String toString() {
        return "Kit{" + "kitFile=" + kitFile + ", potionEffects=" + potionEffects + ", description=" + description + ", kitName='" + kitName + '\'' + ", armorItems=" + Arrays.toString(armorItems) + ", inventoryItems=" + Arrays.toString(inventoryItems) + ", displayItem=" + displayItem + ", oneTimeKit=" + oneTimeKit + ", enabled=" + enabled + ", cost=" + cost + '}';
    }
}