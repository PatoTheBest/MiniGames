package me.patothebest.gamecore.cosmetics.shop;

import me.patothebest.gamecore.logger.InjectImplementationLogger;
import me.patothebest.gamecore.phase.phases.GamePhase;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.event.arena.ArenaPhaseChangeEvent;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.event.player.PlayerSelectItemEvent;
import me.patothebest.gamecore.file.FlatFile;
import me.patothebest.gamecore.file.ReadOnlyFile;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.ObjectProvider;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractShopManager<ShopItemType extends ShopItem> implements ShopManager<ShopItemType>, ActivableModule, ReloadableModule, ListenerModule {

    private final Class<ShopItemType> shopItemTypeClass;
    protected final ReadOnlyFile readOnlyFile;
    protected final CorePlugin plugin;
    protected final Map<String, ShopItemType> shopItems = new LinkedHashMap<>();
    protected final PlayerManager playerManager;
    protected ObjectProvider<ShopItemType> shopItemTypeObjectProvider;
    protected ShopItemType defaultItem = null;
    protected boolean deselectOnDeplete = false;
    @InjectImplementationLogger private Logger logger;

    @SuppressWarnings("unchecked")
    public AbstractShopManager(CorePlugin plugin, PlayerManager playerManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;

        this.readOnlyFile = new ReadOnlyFile(getShopName());

        try {
            Type genericSuperclass = getClass().getGenericSuperclass();
            Type type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            shopItemTypeClass = (Class<ShopItemType>) Class.forName(type.getTypeName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPhaseChange(ArenaPhaseChangeEvent event) {
        if (!(event.getNewPhase() instanceof GamePhase)) {
            return;
        }

        for (Player player : event.getArena().getPlayers()) {
            IPlayer player1 = playerManager.getPlayer(player);
            ShopItemType selectedItem = player1.getSelectedItem(getShopItemClass());

            if (selectedItem == null) {
                continue;
            }

            if (!player1.useItem(selectedItem)) {
                player1.deSelectItem(selectedItem);
            } else if (deselectOnDeplete) {
                if (!player1.canUse(selectedItem)) {
                    player1.deSelectItem(selectedItem);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(ArenaLeaveEvent event) {
        if (deselectOnDeplete) {
            return;
        }

        IPlayer player1 = playerManager.getPlayer(event.getPlayer());
        ShopItemType selectedItem = player1.getSelectedItem(getShopItemClass());

        if (selectedItem == null) {
            return;
        }

        if (!player1.canUse(selectedItem)) {
            player1.deSelectItem(selectedItem);
        }
    }

    @Override
    public void onPreEnable() {
        boolean newFile = false;
        FlatFile file = readOnlyFile;
        if (!readOnlyFile.getFile().exists()) {
            newFile = true;
            file = new FlatFile(getShopName()) {
                @Override
                protected void writeFile(BufferedWriter writer) throws IOException {
                    super.writeFile(writer);
                    Utils.writeFileToWriter(writer, plugin.getResource(getShopName() + ".yml"));
                }
            };
            file.load();
        }

        readOnlyFile.load();

        if (shopItemTypeObjectProvider == null) {
            throw new IllegalStateException("Shop item provider is not initialized for " + getShopName() + "!");
        }

        logger.log(Level.CONFIG, ChatColor.YELLOW + "Loading items");
        ConfigurationSection shopConfigurationSection = readOnlyFile.getConfigurationSection(getShopName());
        for (String itemName : shopConfigurationSection.getKeys(false)) {
            ShopItemType shopItem;
            try {
                shopItem = shopItemTypeObjectProvider.loadObject(itemName, shopConfigurationSection.getConfigurationSection(itemName).getValues(true));
            } catch (Throwable throwable) {
                if (newFile) {
                    file.set(getShopName() + "." + itemName, null);
                } else {
                    Utils.printError("Could not load item " + itemName, throwable.getMessage());
                }
                continue;
            }

            if (shopConfigurationSection.getBoolean(itemName + ".default")) {
                defaultItem = shopItem;
            }

            shopItems.put(itemName, shopItem);
            logger.log(Level.CONFIG, "Loaded {0}", itemName);
        }

        if (newFile) {
            file.save();
            readOnlyFile.load();
        }

        logger.log(Level.INFO, "Loaded {0} items!", shopItems.size());
    }

    @Override
    public void onReload() {
        Map<CorePlayer, ShopItem> selectedItems = new HashMap<>();
        Map<CorePlayer, Map<ShopItem, Integer>> ownedItems = new HashMap<>();
        for (CorePlayer player : playerManager.getPlayers()) {
            ShopItem remove = player.getSelectedShopItems().remove(shopItemTypeClass);
            if (remove != null) {
                selectedItems.put(player, remove);
            }

            Map<ShopItem, Integer> removeOwned = player.getOwnedItems().remove(shopItemTypeClass);
            if (removeOwned != null) {
                ownedItems.put(player, removeOwned);
            }
        }

        onDisable();
        shopItems.clear();
        defaultItem = null;
        readOnlyFile.load();
        onPreEnable();
        onEnable();
        onPostEnable();

        ownedItems.forEach((player, ownedItemMap) -> {
            HashMap<ShopItem, Integer> playerItems = new HashMap<>();
            ownedItemMap.forEach((shopItem, amount) -> {
                playerItems.put(shopItems.get(shopItem.getName()), amount);
            });

            player.getOwnedItems().put(shopItemTypeClass, playerItems);
        });

        selectedItems.forEach((player, shopItem) -> {
            player.getSelectedShopItems().put(shopItemTypeClass, shopItems.get(shopItem.getName()));
            plugin.callEvent(new PlayerSelectItemEvent(player, shopItem));
        });
    }

    @Override
    public Class<ShopItemType> getShopItemClass() {
        return shopItemTypeClass;
    }

    @Override
    public String getReloadName() {
        return getShopName();
    }

    public ShopItemType getDefaultItem() {
        return defaultItem;
    }

    @Override
    public Collection<ShopItemType> getShopItems() {
        return shopItems.values();
    }

    @Override
    public Map<String, ShopItemType> getShopItemsMap() {
        return shopItems;
    }
}
