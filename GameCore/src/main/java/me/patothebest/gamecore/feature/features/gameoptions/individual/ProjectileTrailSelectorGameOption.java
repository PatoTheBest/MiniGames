package me.patothebest.gamecore.feature.features.gameoptions.individual;

import com.google.inject.Inject;
import me.patothebest.gamecore.cosmetics.projectiletrails.ProjectileManager;
import me.patothebest.gamecore.cosmetics.shop.ShopFactory;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.feature.features.gameoptions.GameOption;
import org.bukkit.entity.Player;

public class ProjectileTrailSelectorGameOption extends AbstractFeature implements GameOption {

    private final ShopFactory shopFactory;
    private final ProjectileManager arrowManager;

    @Inject private ProjectileTrailSelectorGameOption(ShopFactory shopFactory, ProjectileManager arrowManager) {
        this.shopFactory = shopFactory;
        this.arrowManager = arrowManager;
    }

    @Override
    public SimpleButton getButton(Player player) {
        return new SimpleButton(new ItemStackBuilder().material(Material.BLAZE_POWDER).name(CoreLang.LOBBY_PROJECTILE_TRAIL_MENU.getMessage(player))).action(() -> {
            shopFactory.createShopMenu(player, arrowManager);
        });
    }
}
