package me.patothebest.gamecore.gui.anvil;

import me.patothebest.gamecore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class AnvilGUI implements Listener{

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final Player player;
    private final HashMap<AnvilSlot, ItemStack> items;
    private final AnvilClickEventHandler handler;
    private Inventory inv;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public AnvilGUI(final Plugin plugin, final Player player, final AnvilClickEventHandler handler) {
        super();
        this.items = new HashMap<>();
        this.player = player;
        this.handler = handler;

        // register the events since when someone clicks
        // the anvil, the bukkit event is triggered
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // -------------------------------------------- //
    // EVENTS
    // -------------------------------------------- //

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // check for the inventory
        if (!event.getInventory().equals(inv)) {
            return;
        }

        // cancel the event to prevent items from being taken
        event.setCancelled(true);

        // get the clicked item
        ItemStack item = event.getCurrentItem();

        // get the slot the item
        int slot = event.getRawSlot();
        String name = "";

        // if the item has a custom name...
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                // ...get it and save it
                name = meta.getDisplayName();
            }
        }

        // create the AnvilClickEvent
        AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name, player);

        // call it on the handler
        handler.onAnvilClick(clickEvent);

        // if the event is set to close on click...
        if (clickEvent.getWillClose()) {
            // ...close the inventory
            event.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();

        // check for the inventory
        if (!inv.equals(this.inv)) {
            return;
        }

        // clear the inventory and destroy
        inv.clear();
        destroy();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // check for the player
        if (!event.getPlayer().equals(getPlayer())) {
            return;
        }

        // destroy
        destroy();
    }

    // -------------------------------------------- //
    // CLASS METHODS
    // -------------------------------------------- //

    public void open() {
        try {
            open0();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void open0() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        // gets the player handle
        Object entityPlayer = Utils.getHandle(player);

        // gets the nextContainerCounter
        int containerId = (int) Utils.invokeMethod(entityPlayer, "nextContainerCounter", null);

        // create a new minecraft ContainerAnvil
        Object newAnvilContainer = AnvilHandler.getNewAnvilContainer(entityPlayer, containerId);

        // creates the bukkit container using the minecraft container
        // by calling the callInventoryOpenEvent method on CraftEventFactory
        Object container =  Utils.invokeStaticMethod(Utils.getCBSClass("event.CraftEventFactory"), "callInventoryOpenEvent", new Class[] {Utils.getNMSClass("EntityPlayer"), Utils.getNMSClass("Container")}, Utils.invokeMethod(player, "getHandle", new Class[] {}, null), newAnvilContainer);

        if(container == null) {
            return;
        }

        // gets the bukkit inventory we can use and access
        // its methods without invoking a bunch of reflection
        this.inv = (Inventory) Utils.invokeMethod(Utils.invokeMethod(container, "getBukkitView", null), "getTopInventory", null);

        // for each item set...
        for (final AnvilSlot slot : this.items.keySet()) {
            // ...set the items in the inventory
            this.inv.setItem(slot.getSlot(), this.items.get(slot));
        }

        if(Utils.SERVER_VERSION.contains("1_7")){
            Utils.sendPacket(player, AnvilHandler.PACKET_PLAY_OUT_OPEN_WINDOW_CLASS.getConstructors()[1].newInstance(containerId, 8, "Repairing", 0, true));
        } else if (Utils.SERVER_VERSION.contains("1_8") || Utils.SERVER_VERSION.contains("1_9") || Utils.SERVER_VERSION.contains("1_10") || Utils.SERVER_VERSION.contains("1_11") || Utils.SERVER_VERSION.contains("1_12") || Utils.SERVER_VERSION.contains("1_13")){
            Object chatMessage = AnvilHandler.CHAT_MESSAGE_CLASS.getConstructor(String.class, Object[].class).newInstance("Repairing", new Object[0]);
            Utils.sendPacket(player, AnvilHandler.PACKET_PLAY_OUT_OPEN_WINDOW_CLASS.getConstructor(int.class, String.class, AnvilHandler.ICHAT_BASE_COMPONENT_CLASS, int.class).newInstance(containerId, "minecraft:anvil", chatMessage, 0));
        } else {
            Object chatMessage = AnvilHandler.CHAT_MESSAGE_CLASS.getConstructor(String.class, Object[].class).newInstance("Repair & Name", new Object[0]);
            Utils.sendPacket(player, AnvilHandler.PACKET_PLAY_OUT_OPEN_WINDOW_CLASS.getConstructor(int.class, AnvilHandler.CONTAINERS_CLASS, AnvilHandler.ICHAT_BASE_COMPONENT_CLASS).newInstance(containerId, AnvilHandler.ANVIL_CONTAINER, chatMessage));
        }

        // set the active container on the player to the anvil inventory
        Utils.setFieldValueNotDeclared(entityPlayer.getClass(), "activeContainer", entityPlayer, container);

        // set the windowId to the nextContainerCounter
        Utils.setFieldValueNotDeclared(Utils.getFieldValueNotDeclared(entityPlayer.getClass(), "activeContainer", entityPlayer).getClass(), "windowId", Utils.getFieldValueNotDeclared(entityPlayer.getClass(), "activeContainer", entityPlayer), containerId);

        // add the ICrafting slot listener to the container
        Method m = Utils.getMethodNotDeclaredValue(container.getClass(), "addSlotListener", AnvilHandler.ICRAFTING_CLASS);
        Utils.invokeMethod(container, m, entityPlayer);
    }

    private void destroy() {
        HandlerList.unregisterAll(this);
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    private Player getPlayer() {
        return this.player;
    }

    public AnvilGUI setSlot(final AnvilSlot slot, final ItemStack item) {
        this.items.put(slot, item);
        return this;
    }

    public HashMap<AnvilSlot, ItemStack> getItems() {
        return items;
    }
}
