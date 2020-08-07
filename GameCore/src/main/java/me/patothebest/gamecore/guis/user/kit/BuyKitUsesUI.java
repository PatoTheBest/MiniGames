package me.patothebest.gamecore.guis.user.kit;

import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButton;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.StainedGlassPane;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.player.IPlayer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;

import javax.inject.Inject;
import javax.inject.Provider;

public class BuyKitUsesUI extends GUIPage {

    private final Kit kit;
    private final IPlayer iPlayer;
    private final Provider<Economy> economy;
    private final UserGUIFactory userGUIFactory;
    private final boolean setKit;
    private int kitUses = 1;

    private final static int[] GRAY_STAINED_PANES = new int[]{3, 5, 28, 29, 30, 32, 33, 34, 37, 39, 41, 43, 46, 47, 48, 50, 51, 52};

    @Inject
    private BuyKitUsesUI(CorePlugin plugin, Provider<Economy> economy, UserGUIFactory userGUIFactory, @Assisted IPlayer iPlayer, @Assisted Kit kit, @Assisted boolean setKit) {
        super(plugin, iPlayer.getPlayer(), CoreLang.GUI_CHANGE_AMOUNT_TITLE.getMessage(iPlayer), 54);
        this.iPlayer = iPlayer;
        this.kit = kit;
        this.economy = economy;
        this.userGUIFactory = userGUIFactory;
        this.setKit = setKit;
        build();
    }

    @Override
    public void buildPage() {
        if (economy.get() == null) {
            CoreLang.GUI_USER_CHOOSE_ECONOMY_PLUGIN_ERROR.sendMessage(iPlayer);
            getPlayer().closeInventory();
            return;
        }

        if (kit.isOneTimeKit()) {
            IncrementingButtonAction onUpdateItemSize = (amount) -> {
                kitUses = Math.max(Math.min(kitUses + amount, 64), 1);
                refresh();
            };

            addButton(new IncrementingButton(-10, onUpdateItemSize), 19);
            addButton(new IncrementingButton(-5, onUpdateItemSize), 20);
            addButton(new IncrementingButton(-1, onUpdateItemSize), 21);
            addButton(new IncrementingButton(1, onUpdateItemSize), 23);
            addButton(new IncrementingButton(5, onUpdateItemSize), 24);
            addButton(new IncrementingButton(10, onUpdateItemSize), 25);
        }

        addButton(new SimpleButton(new ItemStackBuilder(Material.EMERALD).name(ChatColor.GREEN + (economy.get() != null ? economy.get().currencyNamePlural() : "")).lore(ChatColor.GRAY.toString() + (kit.getCost() * kitUses) + (economy.get() != null ? " " + economy.get().currencyNamePlural() : "") + " will be deducted from your account")), 4);
        addButton(new PlaceHolder(kit.finalDisplayItem(iPlayer, true).amount(kitUses)), 22);
        addButton(new SimpleButton(new ItemStackBuilder().createCancelItem()).action(() -> userGUIFactory.openKitShop(player)), 38);

        if (iPlayer.getMoney() >= kit.getCost() * kitUses) {
            addButton(new SimpleButton(new ItemStackBuilder().createConfirmItem()).action(() -> {
                EconomyResponse economyResponse = economy.get().withdrawPlayer(player, kit.getCost() * kitUses);
                if (!economyResponse.transactionSuccess()) {
                    player.sendMessage(CoreLang.GUI_USER_CHOOSE_ERROR_OCCURRED.getMessage(player));
                    player.sendMessage(ChatColor.RED + "ERROR: " + economyResponse.errorMessage);
                    return;
                }

                if (!kit.isOneTimeKit()) {
                    iPlayer.buyPermanentKit(kit);
                    player.sendMessage(CoreLang.GUI_USER_CHOOSE_YOU_PURCHASED_PERMANENT_KIT.replace(player, kit.getKitName()));
                } else {
                    iPlayer.addKitUses(kit, kitUses);
                    player.sendMessage(CoreLang.GUI_USER_CHOOSE_YOU_PURCHASED_KIT_USES.replace(player, kitUses, kit.getKitName()));
                }

                if (setKit) {
                    iPlayer.setKit(kit);
                }

                userGUIFactory.openKitShop(player);
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
