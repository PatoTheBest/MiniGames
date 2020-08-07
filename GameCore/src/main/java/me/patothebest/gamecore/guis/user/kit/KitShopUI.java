package me.patothebest.gamecore.guis.user.kit;

import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.button.ClickTypeButton;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.player.modifiers.KitModifier;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class KitShopUI extends GUIMultiPage {

    private final PlayerManager playerManager;
    private final KitManager kitManager;
    private final UserGUIFactory userGUIFactory;

    @Inject private KitShopUI(CorePlugin plugin, KitManager kitManager, PlayerManager playerManager, UserGUIFactory userGUIFactory, @Assisted Player player) {
        super(plugin, player, CoreLang.GUI_USER_CHOOSE_TITLE.getMessage(player), Math.min(((int) Math.ceil((kitManager.getEnabledKits().size() + 1) / 9.0)) * 9, 54));
        this.kitManager = kitManager;
        this.playerManager = playerManager;
        this.userGUIFactory = userGUIFactory;
        build();
    }

    @Override
    protected void buildContent() {
        final int[] slot = {0};
        List<Kit> enabledKits = new ArrayList<>(kitManager.getEnabledKits());
        IPlayer corePlayer = playerManager.getPlayer(getPlayer());
        boolean showChooseDefault = !corePlayer.isInArena();
        enabledKits.add(0, kitManager.getDefaultKit());
        enabledKits.stream().skip(currentPage*pageSize).limit(pageSize).forEach(kit -> {
            if (kit.getPermissionGroup().hasPermission(getPlayer())) {
                addButton(new ClickTypeButton(kit.finalDisplayItem(corePlayer, true, showChooseDefault)).action((clickType) -> {
                    if ((clickType.name().contains("LEFT") && corePlayer.canUseKit(kit)) || kit.isFree() || (!kit.isOneTimeKit() && corePlayer.isPermanentKit(kit))) {
                        if (corePlayer.getKit() == kit) {
                            if(showChooseDefault) {
                                corePlayer.sendMessage(CoreLang.GUI_KIT_SHOP_ALREADY_SELECTED);
                            } else {
                                corePlayer.sendMessage(CoreLang.GUI_KIT_SHOP_ALREADY_SELECTED_KIT);
                            }
                            return;
                        }
                        corePlayer.setKit(kit);

                        if(showChooseDefault) {
                            corePlayer.notifyObservers(KitModifier.SET_DEFAULT, kit);
                            refresh();
                            CoreLang.GUI_KIT_SHOP_YOU_CHOSE_KIT_DEFAULT.replaceAndSend(player, kit.getKitName());
                        } else {
                            refresh();
                            CoreLang.GUI_KIT_SHOP_YOU_CHOSE_KIT.replaceAndSend(player, kit.getKitName());
                        }
                    } else {
                        userGUIFactory.createBuyKitUsesUI(corePlayer, kit, showChooseDefault);
                    }
                }), slot[0]);
            } else {
                addButton(new SimpleButton(kit.finalDisplayItem(corePlayer, true)), slot[0]);
            }
            slot[0]++;
        });
    }

    @Override
    protected int getListCount() {
        if (kitManager.getEnabledKits().size() <= 45) {
            return -1;
        }

        return kitManager.getEnabledKits().size();
    }

}
