package me.patothebest.gamecore.cosmetics.cage;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.cosmetics.shop.AbstractShopManager;
import me.patothebest.gamecore.player.PlayerManager;

@Singleton
@ModuleName("Cage Manager")
public class CageManager extends AbstractShopManager<Cage> {

    public static final Cage NULL_CAGE = new Cage(Material.AIR, Material.AIR);
    private static final Cage DEFAULT_CAGE = new Cage(Material.GLASS, Material.GLASS);

    @Inject private CageManager(CorePlugin plugin, PlayerManager playerManager) {
        super(plugin, playerManager);

        this.shopItemTypeObjectProvider = Cage::new;
        this.deselectOnDeplete = true;
    }

    @Override
    public void onPreEnable() {
        super.onPreEnable();
        if (defaultItem == null) {
            shopItems.put("default", DEFAULT_CAGE);
        }
    }

    @Override
    public String getShopName() {
        return "cages";
    }

    @Override
    public Cage getDefaultItem() {
        return defaultItem == null ? DEFAULT_CAGE : defaultItem;
    }

    @Override
    public ILang getTitle() {
        return CoreLang.SHOP_CAGE_TITLE;
    }

    @Override
    public ILang getName() {
        return CoreLang.SHOP_CAGE_NAME;
    }
}
