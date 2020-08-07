package me.patothebest.gamecore.treasure.ui;

import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.itemstack.StainedGlassPane;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButton;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.player.IPlayer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;

import javax.inject.Inject;
import javax.inject.Provider;

public class BuyTreasureChestsUI extends GUIPage {

    private final IPlayer iPlayer;
    private final Provider<Economy> economy;
    private final UserGUIFactory userGUIFactory;
    private final TreasureType treasureType;
    private int treasureChestAmount = 1;

    private final static int[] GRAY_STAINED_PANES = new int[]{3, 5, 28, 29, 30, 32, 33, 34, 37, 39, 41, 43, 46, 47, 48, 50, 51, 52};

    @Inject private BuyTreasureChestsUI(CorePlugin plugin, Provider<Economy> economy, UserGUIFactory userGUIFactory, @Assisted IPlayer iPlayer, @Assisted TreasureType treasureType) {
        super(plugin, iPlayer.getPlayer(), CoreLang.GUI_TREASURE_CHEST_TITLE.getMessage(iPlayer), 54);
        this.iPlayer = iPlayer;
        this.economy = economy;
        this.userGUIFactory = userGUIFactory;
        this.treasureType = treasureType;
        build();
    }

    @Override
    public void buildPage() {
        if (economy.get() == null) {
            CoreLang.GUI_BUY_TREASURE_CHEST_ECONOMY_PLUGIN_ERROR.sendMessage(iPlayer);
            getPlayer().closeInventory();
            return;
        }

        IncrementingButtonAction onUpdateItemSize = (amount) -> {
            if (treasureChestAmount + amount <= 0) {
                treasureChestAmount = 1;
            } else {
                treasureChestAmount += amount;
            }

            refresh();
        };

        addButton(new IncrementingButton(-10, onUpdateItemSize), 19);
        addButton(new IncrementingButton(-5, onUpdateItemSize), 20);
        addButton(new IncrementingButton(-1, onUpdateItemSize), 21);
        addButton(new IncrementingButton(1, onUpdateItemSize), 23);
        addButton(new IncrementingButton(5, onUpdateItemSize), 24);
        addButton(new IncrementingButton(10, onUpdateItemSize), 25);

        addButton(new SimpleButton(new ItemStackBuilder(Material.EMERALD).name(ChatColor.GREEN + (economy.get() != null ? economy.get().currencyNamePlural() : "")).lore(ChatColor.GRAY.toString() + (treasureType.getPrice() * treasureChestAmount) + (economy.get() != null ? " " + economy.get().currencyNamePlural() : "") + " will be deducted from your account")), 4);
        addButton(new PlaceHolder(new ItemStackBuilder().customSkull(treasureType.getUrl()).name(ChatColor.GREEN + treasureType.getName()).amount(treasureChestAmount)), 22);
        addButton(new SimpleButton(new ItemStackBuilder().createCancelItem()).action(() -> userGUIFactory.openKitShop(player)), 38);

        if (iPlayer.getMoney() >= treasureType.getPrice() * treasureChestAmount) {
            addButton(new SimpleButton(new ItemStackBuilder().createConfirmItem()).action(() -> {
                EconomyResponse economyResponse = economy.get().withdrawPlayer(player, treasureType.getPrice() * treasureChestAmount);
                if (!economyResponse.transactionSuccess()) {
                    player.sendMessage(CoreLang.GUI_BUY_TREASURE_CHEST_ERROR_OCCURRED.getMessage(player));
                    player.sendMessage(ChatColor.RED + "ERROR: " + economyResponse.errorMessage);
                    return;
                }

                iPlayer.setKeys(treasureType, iPlayer.getKeys(treasureType) + treasureChestAmount);
                player.sendMessage(CoreLang.GUI_BUY_TREASURE_CHEST_YOU_PURCHASED.replace(player, treasureChestAmount, treasureType.getName()));
                player.closeInventory();
            }), 42);
        } else {
            addPlaceholder(new ItemStackBuilder().material(Material.BARRIER).name(iPlayer, CoreLang.GUI_BUY_TREASURE_CHEST_NOT_ENOUGH_MONEY), 42);
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
