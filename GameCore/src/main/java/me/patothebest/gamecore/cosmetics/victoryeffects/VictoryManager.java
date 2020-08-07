package me.patothebest.gamecore.cosmetics.victoryeffects;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.patothebest.gamecore.cosmetics.shop.AbstractShopManager;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.player.PlayerManager;

@Singleton
@ModuleName("Victory Effects")
public class VictoryManager extends AbstractShopManager<VictoryEffectItem> {

    private final EventRegistry eventRegistry;
    private final Provider<Injector> injectorProvider;

    @Inject private VictoryManager(CorePlugin plugin, PlayerManager playerManager, EventRegistry eventRegistry, Provider<Injector> injectorProvider) {
        super(plugin, playerManager);
        this.eventRegistry = eventRegistry;
        this.injectorProvider = injectorProvider;
        shopItemTypeObjectProvider = VictoryEffectItem::new;
    }

    @Override
    public void onPreEnable() {
        VictoryEffectType.init(injectorProvider.get());
        super.onPreEnable();
    }

    @Override
    public void onEnable() {
        for (VictoryEffectType value : VictoryEffectType.values()) {
            eventRegistry.registerListener(value.getVictoryEffect());
        }
    }

    @Override
    public void onDisable() {
        for (VictoryEffectType value : VictoryEffectType.values()) {
            eventRegistry.unRegisterListener(value.getVictoryEffect());
        }
    }

    @Override
    public ILang getTitle() {
        return CoreLang.SHOP_WIN_EFFECTS_TITLE;
    }

    @Override
    public ILang getName() {
        return CoreLang.SHOP_WIN_EFFECTS_TRAIL_NAME;
    }

    @Override
    public String getShopName() {
        return "victory-effects";
    }
}
