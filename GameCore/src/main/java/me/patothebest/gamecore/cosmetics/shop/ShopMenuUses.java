package me.patothebest.gamecore.cosmetics.shop;

import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButton;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.StainedGlassPane;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Provider;

public class ShopMenuUses<ShopItemType extends ShopItem, PlayerType extends IPlayer> extends GUIPage {

    private int itemUses = 1;
    private final ItemStack displayItem;
    private final ShopManager<ShopItemType> shopManager;
    private final ShopItemType shopItem;
    private final PlayerManager playerManager;
    private final ShopFactory shopFactory;
    private final Provider<Economy> economyProvider;

    private final static int[] GRAY_STAINED_PANES = new int[]{3, 5, 28, 29, 30, 32, 33, 34, 37, 39, 41, 43, 46, 47, 48, 50, 51, 52};

    @Inject public ShopMenuUses(CorePlugin plugin, @Assisted Player player, @Assisted ItemStack displayItem, @Assisted ShopManager<ShopItemType> shopManager, @Assisted ShopItemType shopItem, PlayerManager playerManager, ShopFactory shopFactory, Provider<Economy> economyProvider) {
        super(plugin, player, shopManager.getTitle(), 54);
        this.displayItem = displayItem;
        this.shopManager = shopManager;
        this.shopItem = shopItem;
        this.playerManager = playerManager;
        this.shopFactory = shopFactory;
        this.economyProvider = economyProvider;
        build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void buildPage() {
        final PlayerType iPlayer = (PlayerType) playerManager.getPlayer(getPlayer());

        if (economyProvider.get() == null) {
            CoreLang.GUI_USER_CHOOSE_ECONOMY_PLUGIN_ERROR.sendMessage(iPlayer);
            getPlayer().closeInventory();
            return;
        }

        if (!shopItem.isPermanent()) {
            IncrementingButtonAction onUpdateUses = (amount) -> {
                itemUses = Math.max(Math.min(itemUses + amount, 64), 1);
                refresh();
            };

            addButton(new IncrementingButton(-10, onUpdateUses), 19);
            addButton(new IncrementingButton(-5, onUpdateUses), 20);
            addButton(new IncrementingButton(-1, onUpdateUses), 21);
            addButton(new IncrementingButton(1, onUpdateUses), 23);
            addButton(new IncrementingButton(5, onUpdateUses), 24);
            addButton(new IncrementingButton(10, onUpdateUses), 25);
        }

        displayItem.setAmount(itemUses);
        addButton(new SimpleButton(new ItemStackBuilder(Material.EMERALD).name(ChatColor.GREEN + (economyProvider.get() != null ? economyProvider.get().currencyNamePlural() : "")).lore(ChatColor.GRAY.toString() + (shopItem.getPrice() * itemUses) + (economyProvider.get() != null ? " " + economyProvider.get().currencyNamePlural() : "") + " will be deducted from your account")), 4);
        addButton(new PlaceHolder(displayItem), 22);
        addButton(new SimpleButton(new ItemStackBuilder().createCancelItem()).action(() -> shopFactory.createShopMenu(player, shopManager)), 38);

        if (iPlayer.getMoney() >= shopItem.getPrice() * itemUses) {
            addButton(new SimpleButton(new ItemStackBuilder().createConfirmItem()).action(() -> {
                EconomyResponse economyResponse = economyProvider.get().withdrawPlayer(player, shopItem.getPrice() * itemUses);
                if (!economyResponse.transactionSuccess()) {
                    player.sendMessage(CoreLang.GUI_USER_CHOOSE_ERROR_OCCURRED.getMessage(player));
                    player.sendMessage(ChatColor.RED + "ERROR: " + economyResponse.errorMessage);
                    return;
                }

                if (shopItem.isPermanent()) {
                    iPlayer.buyItemPermanently(shopItem);
                    player.sendMessage(CoreLang.GUI_SHOP_YOU_BOUGHT.replace(player, shopItem.getDisplayName()));
                } else {
                    iPlayer.buyItemUses(shopItem, itemUses);
                    player.sendMessage(CoreLang.GUI_SHOP_PURCHASED_USES.replace(player, itemUses, shopItem.getDisplayName()));
                }

                iPlayer.selectItem(shopItem);
                shopFactory.createShopMenu(player, shopManager);
            }), 42);
        } else {
            addPlaceholder(new ItemStackBuilder().material(Material.BARRIER).name(iPlayer, CoreLang.GUI_KIT_SHOP_NOT_ENOUGH_MONEY), 42);
        }

        for (int grayStainedPane : GRAY_STAINED_PANES) {
            addPlaceholder(new ItemStackBuilder(StainedGlassPane.GRAY).name(""), grayStainedPane);
        }

        for (int i = 0; i < 54; i++) {
            if (isFree(i)) {
                addPlaceholder(new ItemStackBuilder(StainedGlassPane.WHITE).name(""), i);
            }
        }
    }

}
