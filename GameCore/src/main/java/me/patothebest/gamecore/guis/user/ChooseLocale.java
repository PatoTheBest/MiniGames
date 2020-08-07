package me.patothebest.gamecore.guis.user;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.CoreLocaleManager;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChooseLocale extends GUIPage {

    private final PlayerManager playerManager;

    public ChooseLocale(CorePlugin plugin, PlayerManager playerManager, Player player) {
        super(plugin, player, CoreLang.GUI_CHOOSE_LOCALE_TITLE.getMessage(player), 9);
        this.playerManager = playerManager;
        build();
    }

    @Override
    protected void buildPage() {
        final int[] slot = {0};
        CoreLocaleManager.getLocales().values().forEach(locale -> {
            ItemStackBuilder itemStackBuilder = new ItemStackBuilder().customSkull(CoreLang.SKIN.getMessage(locale)).name(ChatColor.GREEN + CoreLang.NAME.getMessage(locale));
            if(playerManager.getPlayer(getPlayer()).getLocale().getName().equalsIgnoreCase(locale.getName())) {
                itemStackBuilder.lore(CoreLang.GUI_CHOOSE_LOCALE_SELECTED.getMessage(locale));
                itemStackBuilder.glowing(true);
            }
            addButton(new SimpleButton(itemStackBuilder).action(() -> {
                if(playerManager.getPlayer(player).getLocale().getName().equalsIgnoreCase(locale.getName())) {
                    player.sendMessage(CoreLang.GUI_CHOOSE_LOCALE_NOT_CHANGED.getMessage(player));
                    return;
                }

                playerManager.getPlayer(player).setLocale(locale);
                player.sendMessage(CoreLang.GUI_CHOOSE_LOCALE_CHANGED.getMessage(player));
                setTitle(CoreLang.GUI_CHOOSE_LOCALE_TITLE.getMessage(getPlayer()));
            }), slot[0]);
            slot[0]++;
        });
    }

}
