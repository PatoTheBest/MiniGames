package me.patothebest.gamecore.feature.features.gameoptions.individual;

import com.google.inject.Inject;
import me.patothebest.gamecore.cosmetics.cage.CageManager;
import me.patothebest.gamecore.cosmetics.shop.ShopFactory;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.feature.features.gameoptions.GameOption;
import org.bukkit.entity.Player;

public class CageSelectorGameOption extends AbstractFeature implements GameOption {

    private final ShopFactory shopFactory;
    private final CageManager cageManager;

    @Inject private CageSelectorGameOption(ShopFactory shopFactory, CageManager cageManager) {
        this.shopFactory = shopFactory;
        this.cageManager = cageManager;
    }

    @Override
    public SimpleButton getButton(Player player) {
        return new SimpleButton(new ItemStackBuilder().material(Material.GLASS).name(CoreLang.LOBBY_CAGE_MENU.getMessage(player))).action(() -> {
            shopFactory.createShopMenu(player, cageManager);
        });
    }
}
