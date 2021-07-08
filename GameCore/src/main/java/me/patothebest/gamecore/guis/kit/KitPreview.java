package me.patothebest.gamecore.guis.kit;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitPreview extends GUIPage {

    private final Kit kit;
    private final PlayerManager playerManager;
    private final boolean addReceiveItemsButton;
    private final Runnable onBack;

    @Inject private KitPreview(CorePlugin plugin, @Assisted Player player, @Assisted Kit kit, PlayerManager playerManager, @Assisted boolean addReceiveItemsButton, @Assisted Runnable onBack) {
        super(plugin, player, CoreLang.GUI_PREVIEW_KIT_TITLE.replace(player, kit.getKitName()), 54);
        this.kit = kit;
        this.playerManager = playerManager;
        this.addReceiveItemsButton = addReceiveItemsButton;
        this.onBack = onBack;
        build();
    }

    @Override
    protected void buildPage() {
        for (int i = 0; i < kit.getArmorItems().length + kit.getInventoryItems().length; i++) {
            setItem(i);
        }

        addButton(new BackButton(getPlayer()).action(onBack::run), 45);
        if (addReceiveItemsButton) {
            addButton(new SimpleButton(new ItemStackBuilder(kit.finalDisplayItem(playerManager.getPlayer(getPlayer()), false)).name(getPlayer(), CoreLang.GUI_PREVIEW_KIT_COPY)).action(() -> kit.applyKit(playerManager.getPlayer(player))), 53);
        }
    }

    private void setItem(int i) {
        int slot = i;
        ItemStack[] is = kit.getInventoryItems();

        if (i >= is.length) {
            i -= is.length;
            is = kit.getArmorItems();
        } else {
            i = this.getReversedItemSlotNum(i);
        }

        if (i >= is.length) {
            i -= is.length;
            is = null;
        } else if (is == kit.getArmorItems()) {
            i = this.getReversedArmorSlotNum(i);
        }

        if (is == null) {
            return;
        }

        addButton(new PlaceHolder(is[i]), slot);
    }

    private int getReversedItemSlotNum(final int i) {
        return (i >= 27) ? (i - 27) : (i + 9);
    }

    private int getReversedArmorSlotNum(final int i) {
        if (i == 0) {
            return 3;
        }

        if (i == 1) {
            return 2;
        }

        if (i == 2) {
            return 1;
        }

        if (i == 3) {
            return 0;
        }

        return i;
    }

}
