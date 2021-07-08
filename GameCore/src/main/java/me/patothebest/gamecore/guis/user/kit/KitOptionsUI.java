package me.patothebest.gamecore.guis.user.kit;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.guis.kit.KitUIFactory;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.modifiers.KitModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class KitOptionsUI extends GUIPage {

    private final KitUIFactory kitUIFactory;
    private final UserGUIFactory guiFactory;
    private final IPlayer player;
    private final Kit kit;
    private final Runnable mainBack;

    @AssistedInject
    private KitOptionsUI(Plugin plugin, KitUIFactory kitUIFactory, UserGUIFactory guiFactory, @Assisted IPlayer player, @Assisted Kit kit, @Assisted Runnable mainBack) {
        super(plugin, player.getPlayer(), CoreLang.GUI_KIT_SHOP_OPTIONS_TITLE.replace(player, kit.getKitName()), 27);
        this.kitUIFactory = kitUIFactory;
        this.guiFactory = guiFactory;
        this.player = player;
        this.kit = kit;
        this.mainBack = mainBack;
        build();
    }

    @Override
    protected void buildPage() {
        boolean isSelected = player.getKit() == kit;
        boolean permanentKit = !kit.isOneTimeKit() || player.isPermanentKit(kit);
        Runnable onBack = () -> guiFactory.openKitOptions(player, kit, mainBack);

        ItemStackBuilder select = new ItemStackBuilder();
        ItemStack display = kit.finalDisplayItem(player, true, true, false);

        if (player.canUseKit(kit)) {
            select.material(Material.SLIME_BALL);
            if (isSelected) {
                select.glowing(true).name(CoreLang.GUI_SHOP_SELECTED.getMessage(player));
            } else {
                select.name(CoreLang.GUI_KIT_SHOP_CLICK_DEFAULT.getMessage(player));
            }
        } else {
            select.material(Material.DIAMOND)
                    .name((permanentKit ? CoreLang.GUI_KIT_SHOP_CLICK_BUY_PERMANENT : CoreLang.GUI_KIT_SHOP_CLICK_BUY).getMessage(player));
        }

        ItemStack preview = new ItemStackBuilder(Material.ITEM_FRAME)
                .name(CoreLang.GUI_KIT_OPTIONS_PREVIEW.getMessage(player));
        ItemStack layout = new ItemStackBuilder(Material.WRITABLE_BOOK)
                .name(CoreLang.GUI_KIT_OPTIONS_LAYOUT.getMessage(player));
        ItemStack buy = new ItemStackBuilder(Material.EMERALD)
                .name(CoreLang.GUI_KIT_SHOP_CLICK_BUY.getMessage(player));
        ItemStack back = new ItemStackBuilder().createBackItem(player.getPlayer());

        addButton(new PlaceHolder(display), 4);
        addButton(new SimpleButton(select).action(() -> {
            if (isSelected) {
                CoreLang.GUI_KIT_SHOP_ALREADY_SELECTED.sendMessage(player);
                return;
            }
            if (player.canUseKit(kit)) {
                player.setKit(kit);
                player.notifyObservers(KitModifier.SET_DEFAULT, kit);
                refresh();
                CoreLang.GUI_KIT_SHOP_YOU_CHOSE_KIT_DEFAULT.replaceAndSend(player, kit.getKitName());
            } else {
                guiFactory.createBuyKitUsesUI(player, kit, onBack);
            }
        }), permanentKit ? 11 : 10);
        addButton(new SimpleButton(preview).action(() -> {
            kitUIFactory.createKitPreview(player.getPlayer(), kit, false, onBack);
        }), permanentKit ? 13 : 12);
        addButton(new SimpleButton(layout).action(() -> {
            guiFactory.openKitLayoutEditor(player, kit, onBack);
        }), permanentKit ? 15 : 14);

        if (!permanentKit) {
            addButton(new SimpleButton(buy).action(() -> {
                guiFactory.createBuyKitUsesUI(player, kit, onBack);
            }), 16);
        }

        addButton(new SimpleButton(back).action(mainBack::run), 26);
    }
}
