package me.patothebest.gamecore.gui.inventory;

import me.patothebest.gamecore.gui.inventory.button.NullButton;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.gui.inventory.page.FailedPage;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.timings.TimingsData;
import me.patothebest.gamecore.timings.TimingsManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public abstract class GUIPage implements Listener {

    private final HashMap<Integer, GUIButton> buttons = new HashMap<>();
    private final int size;
    protected final Player player;
    protected final Plugin plugin;

    private boolean overrideClose = false;
    private String name;
    private Inventory menu;
    protected boolean blockInventoryMovement = true;

    protected GUIPage(Plugin plugin, Player player, ILang locale, int size, boolean override) {
        this(plugin, player, locale.getMessage(player), size);
        this.overrideClose = override;
    }

    protected GUIPage(Plugin plugin, Player player, ILang locale, int size) {
        this(plugin, player, locale.getMessage(player), size);
    }

    protected GUIPage(Plugin plugin, Player player, String rawName, int size, boolean override) {
        this(plugin, player, rawName, size);
        this.overrideClose = override;
    }

    protected GUIPage(Plugin plugin, Player player, String rawName, int size) {
        this.player = player;
        Utils.invokeStaticMethod(Utils.getCBSClass("event.CraftEventFactory"), "handleInventoryCloseEvent", new Class[] {Utils.getNMSClass("EntityHuman")}, Utils.invokeMethod(player, "getHandle", new Class[] {}, null));

        this.plugin = plugin;
        this.size = size;
        this.name = (rawName.length() > 32 ? rawName.substring(0, 32) : rawName);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.menu = Bukkit.getServer().createInventory(null, size, name);

        Utils.setFieldValue(Utils.getNMSClass("EntityHuman"), "activeContainer", Utils.invokeMethod(player, "getHandle", new Class[] {}, null), Utils.getFieldValue(Utils.getNMSClass("EntityHuman"), "defaultContainer", Utils.invokeMethod(player, "getHandle", new Class[] {}, null)));
        this.player.openInventory(menu);
    }

    public final void build() {
        if(!getPlayer().isOnline()) {
            destroy();
            return;
        }

        TimingsData timing = TimingsManager.create("Menu '" + name + "' (" + getClass().getSimpleName() + ") for " + getPlayer().getName());
        try {
            buildPage();
            getPlayer().updateInventory();
        } catch (Throwable t) {
            t.printStackTrace();
            new FailedPage(plugin, getPlayer(), CoreLang.GUI_ERROR_INIT.getMessage(player), ChatColor.RED + t.getMessage());
        }
        timing.stop(50);
    }

    protected abstract void buildPage();

    public Plugin getPlugin() {
        return plugin;
    }

    public Player getPlayer() {
        return player;
    }

    public void addPlaceholder(ItemStack itemStack) {
        addButton(new PlaceHolder(itemStack));
    }

    public void addPlaceholder(ItemStack itemStack, int slot) {
        addButton(new PlaceHolder(itemStack), slot);
    }

    public void addButton(GUIButton button, int slot) {
        if(slot >= size) {
            return;
        }

        if(!(button instanceof NullButton)) {
            menu.setItem(slot, button.getItem());
        }

        buttons.put(slot, button);
    }

    public void addButton(GUIButton button) {
        int slot = 0;
        while(!isFree(slot) && slot < size) {
            slot++;
        }

        if(slot > size || !isFree(slot)) {
            Utils.printError("Could not find empty slot", "Button= " + button.toString(), "Slot= " + slot, "Menu= " + toString());
            return;
        }

        if(!(button instanceof NullButton)) {
            menu.setItem(slot, button.getItem());
        }

        buttons.put(slot, button);
    }

    public void removeButton(int slot) {
        menu.setItem(slot, null);

        if (buttons.get(slot) != null) {
            buttons.get(slot).destroy();
        }

        buttons.remove(slot);
    }

    private void removeAll() {
        for (int i = 0; i <= size - 1; i++) {
            removeButton(i);
        }

        buttons.clear();
    }

    public void refresh() {
        removeAll();
        build();
    }

    public void setTitle(String title) {
        removeAll();
        name = (title.length() > 32 ? title.substring(0, 32) : title);
        this.menu = Bukkit.getServer().createInventory(null, size, name);
        Utils.setFieldValue(Utils.getNMSClass("EntityHuman"), "activeContainer", Utils.invokeMethod(player, "getHandle", new Class[] {}, null), Utils.getFieldValue(Utils.getNMSClass("EntityHuman"), "defaultContainer", Utils.invokeMethod(player, "getHandle", new Class[] {}, null)));
        player.openInventory(menu);
        build();
    }

    public boolean isFree(int slot) {
        return !buttons.containsKey(slot);
    }

    protected void onInventoryCloseOverride() {

    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent event) {
        if (overrideClose) {
            onInventoryCloseOverride();
            return;
        }

        Player player = (Player) event.getPlayer();

        if (!this.player.getOpenInventory().getTitle().equalsIgnoreCase(name)) {
            return;
        }

        if (this.player.getName().equalsIgnoreCase(player.getName())) {
            destroy();
            destroyInternal();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!this.player.getName().equalsIgnoreCase(player.getName())) {
            return;
        }

        if (!this.player.getOpenInventory().getTitle().equalsIgnoreCase(name)) {
            return;
        }

        event.setCancelled(blockInventoryMovement);

        if (!buttons.containsKey(event.getRawSlot())) {
            return;
        }

        event.setCancelled(true);
        GUIButton guiButton = buttons.get(event.getRawSlot());

        TimingsData timing = TimingsManager.create(null);
        try {
            guiButton.click(event.getClick(), this);
        } catch(Throwable t) {
            t.printStackTrace();
            player.sendMessage(ChatColor.RED + t.getMessage());
        }
        if (timing.end(50)) {
            timing.setTiming("Menu '" + name + "' click (" + getClass().getSimpleName() + ") Button ("  + guiButton.getItem().toString() + ")" + "for " + getPlayer().getName());
            timing.print();
        }
    }

    public void destroy() {}

    public void destroyInternal() {
        HandlerList.unregisterAll(this);
        this.buttons.values().forEach(GUIButton::destroy);
        this.buttons.clear();
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "GUIPage{" +
                "buttonsSize=" + buttons.size() +
                ", size=" + size +
                ", user=" + player +
                ", plugin=" + plugin +
                ", overrideClose=" + overrideClose +
                ", name='" + name + '\'' +
                ", blockInventoryMovement=" + blockInventoryMovement +
                '}';
    }
}