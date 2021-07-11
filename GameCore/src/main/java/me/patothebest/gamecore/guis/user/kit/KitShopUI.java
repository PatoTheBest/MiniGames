package me.patothebest.gamecore.guis.user.kit;

import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.button.ClickTypeButton;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import me.patothebest.gamecore.guis.UserGUIFactory;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class KitShopUI extends GUIMultiPage {

    private final static EnumSet<ClickType> LEFT_CLICK_TYPES = EnumSet.copyOf(
            Arrays.stream(ClickType.values())
                    .filter(clickType -> clickType.name().contains("LEFT"))
                    .collect(Collectors.toSet())
    );
    private final PlayerManager playerManager;
    private final KitManager kitManager;
    private final UserGUIFactory userGUIFactory;

    @Inject
    private KitShopUI(CorePlugin plugin, KitManager kitManager, PlayerManager playerManager, UserGUIFactory userGUIFactory, @Assisted Player player) {
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
        enabledKits.add(0, kitManager.getDefaultKit());
        enabledKits.stream().skip(currentPage * pageSize).limit(pageSize).forEach(kit -> {
            if (kit.getPermissionGroup().hasPermission(getPlayer())) {
                addButton(new ClickTypeButton(kit.finalDisplayItem(corePlayer, true, true)).action((clickType) -> {
                    if (LEFT_CLICK_TYPES.contains(clickType) && corePlayer.canUseKit(kit)) {
                        if (corePlayer.getKit() == kit) {
                            corePlayer.sendMessage(CoreLang.GUI_KIT_SHOP_ALREADY_SELECTED_KIT);
                            return;
                        }
                        corePlayer.setKit(kit);

                        refresh();
                        CoreLang.GUI_KIT_SHOP_YOU_CHOSE_KIT.replaceAndSend(player, kit.getKitName());
                    } else {
                        userGUIFactory.openKitOptions(corePlayer, kit, () -> {
                            userGUIFactory.openKitShop(player);
                        });
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
