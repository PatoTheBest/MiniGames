package me.patothebest.gamecore.cosmetics.shop;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.button.ClickTypeButton;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

public class ShopMenu<ShopItemType extends ShopItem, PlayerType extends IPlayer> extends GUIMultiPage {

    private final ShopManager<ShopItemType> shopManager;
    private final PlayerManager playerManager;
    private final ShopFactory shopFactory;

    @Inject protected ShopMenu(CorePlugin plugin, @Assisted Player player, @Assisted ShopManager<ShopItemType> shopManager, PlayerManager playerManager, ShopFactory shopFactory) {
        super(plugin, player, shopManager.getTitle(), Utils.transformToInventorySize(shopManager.getShopItems()));
        this.shopManager = shopManager;
        this.playerManager = playerManager;
        this.shopFactory = shopFactory;
        build();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void buildContent() {
        final PlayerType iPlayer = (PlayerType) playerManager.getPlayer(getPlayer());
        shopManager.getShopItems().stream().skip(pageSize * currentPage).limit(pageSize).forEachOrdered(shopItem -> {
            ItemStackBuilder itemStackShopItem = new ItemStackBuilder(shopItem.getDisplayItem().clone())
                    .name(shopItem.getDisplayName());

            if(!shopItem.getDescription().isEmpty() && !shopItem.getDescription().get(0).isEmpty()) {
                itemStackShopItem.addLore(shopItem.getDescription());
            }

            if (!iPlayer.canUse(shopItem) && !shopItem.isFree()) {
                itemStackShopItem.blankLine()
                        .addLore(CoreLang.GUI_SHOP_PRICE.replace(getPlayer(), (shopItem.isFree() ? CoreLang.GUI_SHOP_FREE.getMessage(getPlayer()) : shopItem.getPrice() + "")));
            }

            ItemStackBuilder itemStack = itemStackShopItem.clone().blankLine();
            boolean canBuyUses = false;
            ShopAction action = ShopAction.DO_NOTHING;

            if (shopItem.getPermission() == null || shopItem.getPermission().isEmpty() || getPlayer().hasPermission(shopItem.getPermission())) {
                if (iPlayer.isSelected(shopItem)) {
                    if(shopItem.isPermanent()) {
                        itemStack.addLore(CoreLang.GUI_SHOP_SELECTED.getMessage(getPlayer())).glowing(true);
                    } else {
                        canBuyUses = true;
                        itemStack.addLore(CoreLang.GUI_SHOP_USES_LORE.replace(iPlayer, iPlayer.getRemainingUses(shopItem)));
                        itemStack.blankLine();
                        itemStack.addLore(CoreLang.GUI_SHOP_RIGHT_CLICK.getMessage(getPlayer()));
                        itemStack.addLore(CoreLang.GUI_SHOP_SELECTED.getMessage(getPlayer())).glowing(true);
                    }
                } else if (shopItem.isFree()) {
                    itemStack.addLore(CoreLang.GUI_SHOP_CLICK_TO_SELECT.getMessage(getPlayer()));
                    action = ShopAction.SELECT;
                } else if (iPlayer.canUse(shopItem)) {
                    if(shopItem.isPermanent() || iPlayer.isPermanent(shopItem)) {
                        itemStack.addLore(CoreLang.GUI_SHOP_LEFT_CLICK_SELECT.getMessage(getPlayer()));
                        action = ShopAction.SELECT;
                    } else {
                        canBuyUses = true;
                        itemStack.addLore(CoreLang.GUI_SHOP_USES_LORE.replace(iPlayer, iPlayer.getRemainingUses(shopItem)));
                        itemStack.blankLine();
                        itemStack.addLore(CoreLang.GUI_SHOP_RIGHT_CLICK.getMessage(getPlayer()));
                        itemStack.addLore(CoreLang.GUI_SHOP_LEFT_CLICK_SELECT.getMessage(getPlayer()));
                        action = ShopAction.SELECT;
                    }
                } else {
                    if(shopItem.isPermanent()) {
                        itemStack.addLore(CoreLang.GUI_SHOP_CLICK_TO_BUY.getMessage(getPlayer()));
                        action = ShopAction.BUY;
                    } else {
                        canBuyUses = true;
                        itemStack.addLore(CoreLang.GUI_SHOP_USES_LORE.replace(iPlayer, 0));
                        itemStack.blankLine();
                        itemStack.addLore(CoreLang.GUI_SHOP_CLICK_TO_BUY.getMessage(getPlayer()));
                        action = ShopAction.BUY;
                    }
                }
            } else {
                itemStack.addLore(CoreLang.GUI_SHOP_NO_PERMISSION_STRING.replace(getPlayer()));
            }

            ShopAction finalAction = action;
            boolean finalCanBuyUses = canBuyUses;
            addButton(new ClickTypeButton(itemStack).action((clickType) -> {
                switch (finalAction) {
                    case DO_NOTHING:
                    case SELECT:
                        if(!clickType.name().contains("LEFT") && finalCanBuyUses) {
                            if (iPlayer.getMoney() < shopItem.getPrice()) {
                                CoreLang.GUI_SHOP_NOT_ENOUGH_MONEY.sendMessage(player);
                                break;
                            }

                            ItemStackBuilder item2 = new ItemStackBuilder(itemStackShopItem);
                            shopFactory.createUsesShopMenu(player, item2, shopManager, shopItem);
                            refresh();
                        } else if(finalAction == ShopAction.SELECT) {
                            iPlayer.selectItem(shopItem);
                            CoreLang.GUI_SHOP_YOU_SELECTED.replaceAndSend(player, shopItem.getDisplayName());
                            refresh();
                        }
                        break;
                    case BUY:
                        if (iPlayer.getMoney() < shopItem.getPrice()) {
                            CoreLang.GUI_SHOP_NOT_ENOUGH_MONEY.sendMessage(player);
                            break;
                        }

                        ItemStackBuilder item2 = new ItemStackBuilder(itemStackShopItem);
                        shopFactory.createUsesShopMenu(player, item2, shopManager, shopItem);
                        break;
                }
            }));
        });
    }

    @Override
    protected int getListCount() {
        return shopManager.getShopItems().size();
    }
}
