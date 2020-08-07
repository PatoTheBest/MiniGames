package me.patothebest.gamecore.treasure.ui;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.StainedGlassPane;
import me.patothebest.gamecore.treasure.chest.TreasureChestLocation;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.treasure.TreasureFactory;
import org.bukkit.ChatColor;

public class OpenChestVoteUI extends GUIPage {

    private final TreasureChestLocation location;
    private final TreasureFactory treasureFactory;
    private final IPlayer player;

    @Inject private OpenChestVoteUI(CorePlugin plugin, @Assisted IPlayer player, @Assisted TreasureChestLocation location, TreasureFactory treasureFactory) {
        super(plugin, player.getPlayer(), ChatColor.GOLD + "Abrir un cofre", 45);
        this.location = location;
        this.player = player;
        this.treasureFactory = treasureFactory;
        build();
    }

    public void buildPage() {
        for(int i = 10; i <= 16; i++) {
            if(i == 13) {
                continue;
            }

            addButton(new PlaceHolder(new ItemStackBuilder(StainedGlassPane.BLACK).name("")), i);
        }

        int slot = 28;
        for(TreasureType type : TreasureType.values()) {
            if(type == TreasureType.VOTE) {
                addButton(treasureFactory.createMenuButton(type, player, location), 13);
                continue;
            }

            addButton(treasureFactory.createMenuButton(type, player, location), slot);
            slot++;

            if(slot == 35) {
                continue;
            }

            addButton(new PlaceHolder(new ItemStackBuilder(StainedGlassPane.BLACK).name("")), slot);
            slot++;
        }

        for (int i = 0; i < 45; i++) {
            if(isFree(i)) {
                addButton(new PlaceHolder(new ItemStackBuilder(StainedGlassPane.WHITE).name("")), i);
            }
        }
    }
}