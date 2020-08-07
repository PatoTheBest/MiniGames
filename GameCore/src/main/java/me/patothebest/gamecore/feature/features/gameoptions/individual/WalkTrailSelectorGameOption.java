package me.patothebest.gamecore.feature.features.gameoptions.individual;

import com.google.inject.Inject;
import me.patothebest.gamecore.cosmetics.shop.ShopFactory;
import me.patothebest.gamecore.cosmetics.walkparticles.WalkTrailsManager;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.feature.features.gameoptions.GameOption;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import org.bukkit.entity.Player;

public class WalkTrailSelectorGameOption extends AbstractFeature implements GameOption {

    private final ShopFactory shopFactory;
    private final WalkTrailsManager walkTrailsManager;

    @Inject private WalkTrailSelectorGameOption(ShopFactory shopFactory, WalkTrailsManager walkTrailsManager) {
        this.shopFactory = shopFactory;
        this.walkTrailsManager = walkTrailsManager;
    }

    @Override
    public SimpleButton getButton(Player player) {
        return new SimpleButton(new ItemStackBuilder().material(Material.REDSTONE).name(CoreLang.LOBBY_WALK_TRAIL_MENU.getMessage(player))).action(() -> {
            shopFactory.createShopMenu(player, walkTrailsManager);
        });
    }
}
