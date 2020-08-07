package me.patothebest.gamecore.menu;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.util.NameableObject;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Menu implements NameableObject {

    private final CorePlugin plugin;
    private final String name;
    private final MenuFile menuFile;
    private String title;
    private int size;
    private final List<MenuItem> menuItems = new ArrayList<>();
    @InjectLogger(name = "MenuManager") private Logger logger;

    @Inject private Menu(CorePlugin plugin, @Assisted String name) {
        this.plugin = plugin;
        this.name = name;
        this.menuFile = new MenuFile(name);
    }

    public void load() {
        this.title = menuFile.getString("Title", "&cnull");
        this.size = menuFile.getInt("Size", 54);

        if (menuFile.isSet("Items")) {
            for (String key : menuFile.getConfigurationSection("Items").getKeys(false)) {
                try {
                    menuItems.add(new MenuItem(menuFile.getConfigurationSection("Items." + key).getValues(true)));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Could not parse menu item", e);
                }
            }
        }
    }

    public void open(Player player) {
        new GUIPage(plugin, player, title, size) {
            @Override
            protected void buildPage() {
                for (MenuItem menuItem : menuItems) {
                    addButton(menuItem.makeButton(player), menuItem.getSlot());
                }
            }
        }.build();
    }

    public String getName() {
        return name;
    }
}
