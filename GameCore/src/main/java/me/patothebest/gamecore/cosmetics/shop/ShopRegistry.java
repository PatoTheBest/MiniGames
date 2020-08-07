package me.patothebest.gamecore.cosmetics.shop;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.Module;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
@ModuleName("Shop Registry")
public class ShopRegistry implements ActivableModule {

    private final Map<String, ShopManager> shopManagersName = new HashMap<>();
    private final Map<Class<? extends ShopItem>, ShopManager> shopItemsManagers = new HashMap<>();
    private final CorePlugin corePlugin;
    @InjectLogger private Logger logger;

    @Inject private ShopRegistry(CorePlugin corePlugin) {
        this.corePlugin = corePlugin;
    }

    @Override
    public void onPreEnable() {
        corePlugin.getModuleInstances().forEach(entry -> {
            Module module = entry.getValue();
            if (module instanceof ShopManager) {
                registerShop((ShopManager) module);
                logger.info("Registered shop " + module.getClass().getSimpleName());
            }
        });
    }

    public Map<String, ShopManager> getShopManagersNamesMap() {
        return shopManagersName;
    }

    public Map<Class<? extends ShopItem>, ShopManager> getShopItemsManagers() {
        return shopItemsManagers;
    }

    @SuppressWarnings("unchecked")
    private void registerShop(ShopManager shopManager) {
        shopManagersName.put(shopManager.getShopName(), shopManager);
        shopItemsManagers.put(shopManager.getShopItemClass(), shopManager);
    }
}
