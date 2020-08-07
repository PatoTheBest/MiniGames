package me.patothebest.gamecore.treasure.ui;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.treasure.chest.TreasureChestLocation;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.treasure.TreasureConfigFile;
import me.patothebest.gamecore.treasure.TreasureFactory;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class OpenChestButton implements GUIButton {

    private final TreasureType chestType;
    private final IPlayer player;
    private final TreasureChestLocation location;
    private final TreasureConfigFile treasureConfigFile;
    private final TreasureFactory treasureFactory;

    @Inject private OpenChestButton(@Assisted TreasureType chestType, @Assisted IPlayer player, @Assisted TreasureChestLocation location, TreasureConfigFile treasureConfigFile, TreasureFactory treasureFactory) {
        this.chestType = chestType;
        this.player = player;
        this.location = location;
        this.treasureConfigFile = treasureConfigFile;
        this.treasureFactory = treasureFactory;
    }

    public ItemStack getItem() {
        ItemStackBuilder item = new ItemStackBuilder()
                .customSkull(chestType.getUrl())
                .name(ChatColor.GREEN + chestType.getName());

        for (String s : chestType.getDescription()) {
            if(s == null || s.isEmpty()) {
                item.blankLine();
            } else {
                item.addLore(s.replace("%owned%", player.getKeys(chestType) + "").replace("%open_message%", treasureConfigFile.getMessage(player, chestType)));
            }
        }

        return item;
    }

    public void click(ClickType clickType, GUIPage page) {
        if (player.getKeys(chestType) <= 0) {
            if(chestType.canBeBought()) {
                treasureFactory.createBuyMenu(player, chestType);
            }
            return;
        }

        if (!location.openChest(player, chestType)) {
            return;
        }

        player.setKeys(chestType, player.getKeys(chestType) - 1);
        player.sendMessage("Abriendo un " + ChatColor.GOLD + "Cofre " + chestType.getName());
        player.getPlayer().closeInventory();
    }

    @Override
    public void destroy() {

    }
}