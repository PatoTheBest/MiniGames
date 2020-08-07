package me.patothebest.gamecore.menu;

import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ReloadPriority;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.util.Priority;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@ReloadPriority(priority = Priority.HIGHEST)
@ModuleName("Menu Manager")
public class MenuManager implements ActivableModule, ReloadableModule {

    public final static File MENUS_DIRECTORY = new File(Utils.PLUGIN_DIR, "menus");
    private final Map<String, Menu> menus = new HashMap<>();
    private final MenuFactory menuFactory;
    @InjectLogger private Logger logger;

    @Inject private MenuManager(MenuFactory menuFactory) {
        this.menuFactory = menuFactory;

        if(!MENUS_DIRECTORY.exists()) {
            MENUS_DIRECTORY.mkdirs();
        }
    }

    @Override
    public void onPostEnable() {
        logger.fine(ChatColor.YELLOW + "Loading menus...");

        if (MENUS_DIRECTORY.listFiles() == null) {
            return;
        }

        for(File file : MENUS_DIRECTORY.listFiles()) {
            String name = file.getName().replace(".yml", "");
            loadMenu(name);
        }
        logger.log(Level.INFO, "Loaded " + menus.size() + " menus!");
    }

    private Menu loadMenu(String name) {
        try {
            logger.fine(ChatColor.YELLOW + "Loading menu " + name);

            Menu menu = menuFactory.createMenu(name);
            menu.load();
            menus.put(name, menu);

            logger.config("Loaded menu " + menu.getName());
            return menu;
        } catch(Throwable t) {
            logger.log(Level.SEVERE, ChatColor.RED + "Could not load menu " + name + "!", t);
        }

        return null;
    }

    @Override
    public void onDisable() {
        menus.clear();
    }

    @Override
    public void onReload() {
        onDisable();
        onPostEnable();
    }

    @Override
    public String getReloadName() {
        return "menus";
    }

    public Map<String, Menu> getMenus() {
        return menus;
    }
}
