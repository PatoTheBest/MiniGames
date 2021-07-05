package me.patothebest.gamecore.guis.user.kit;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.NullButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitLayout;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class KitLayoutUI extends GUIPage {

    private ItemStack[] playerInvItems;
    private final PluginScheduler pluginScheduler;
    private final IPlayer player;
    private final Kit kit;

    @Inject private KitLayoutUI(CorePlugin plugin, @Assisted IPlayer player, PluginScheduler pluginScheduler, @Assisted Kit kit) {
        super(plugin, player.getPlayer(), CoreLang.GUI_USER_EDIT_KIT_LAYOUT_TITLE.replace(player, kit.getKitName()), 27);
        this.pluginScheduler = pluginScheduler;
        this.kit = kit;
        this.player = player;
        this.blockInventoryMovement = false;
        build();
    }

    @Override
    protected void buildPage() {
        playerInvItems = getPlayer().getInventory().getContents();
        getPlayer().getInventory().clear();
        ItemStack[] inventoryItemsCopy = kit.getInventoryItems().clone();

        for (int i = 0; i < inventoryItemsCopy.length; ++i) {
            ItemStack item = inventoryItemsCopy[i];

            if (item != null) {
                inventoryItemsCopy[i] = new ItemStackBuilder(item).lore(hideTag(String.valueOf(i))).amount(1);
            }
        }

        getPlayer().getInventory().setContents(inventoryItemsCopy);
        addButton(new SimpleButton(new ItemStackBuilder(Material.BOOK)
                .name(CoreLang.GUI_USER_EDIT_KIT_LAYOUT_SAVE.getMessage(player)))
                .action(() -> {
                    int[] slotsRemapped = new int[36];
                    Arrays.fill(slotsRemapped, -1);
                    Queue<Integer> freeSlots = new ArrayBlockingQueue<>(37);
                    List<Integer> takenSlots = new ArrayList<>();
                    for (int i = 0; i < 36; i++) {
                        ItemStack itemStack = getPlayer().getInventory().getItem(i);

                        if (itemStack == null ||
                                itemStack.getItemMeta() == null ||
                                itemStack.getItemMeta().getLore() == null ||
                                itemStack.getItemMeta().getLore().isEmpty()) {
                            freeSlots.add(i);
                            continue;
                        }
                        String oldInd = itemStack.getItemMeta().getLore().get(0);
                        int oldIndex = Integer.parseInt(oldInd.replace(ChatColor.COLOR_CHAR + "", ""));
                        slotsRemapped[i] = oldIndex;
                        takenSlots.add(oldIndex);
                    }

                    int newIndex = 0;
                    while (!freeSlots.isEmpty()) {
                        Integer poll = freeSlots.poll();

                        while (takenSlots.contains(newIndex)) {
                            newIndex++;
                        }

                        slotsRemapped[poll] = newIndex++;
                    }
                    KitLayout kitLayout = new KitLayout(slotsRemapped);
                    player.modifyKitLayout(kit, kitLayout);
                    getPlayer().closeInventory();
                    CoreLang.GUI_USER_EDIT_KIT_LAYOUT_SAVED.replaceAndSend(player, kit.getKitName());
        }), 15);

        addButton(new SimpleButton(new ItemStackBuilder().createCancelItem()).action(super.player::closeInventory), 13);

        for(int i = 0; i < 27; i++) {
            if (!isFree(i)) {
                continue;
            }
            addButton(new NullButton(), i);
        }
    }

    private static String hideTag(String s) {
        StringBuilder hidden = new StringBuilder();
        for (char c : s.toCharArray()) hidden.append(ChatColor.COLOR_CHAR + "").append(c);
        return hidden.toString();
    }

    @Override
    public void destroy() {
        getPlayer().setItemOnCursor(null);
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setContents(playerInvItems);
        pluginScheduler.runTaskLater(()-> getPlayer().updateInventory(), 3L);
    }
}
