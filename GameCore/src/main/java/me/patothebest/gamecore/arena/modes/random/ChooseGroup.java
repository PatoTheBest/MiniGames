package me.patothebest.gamecore.arena.modes.random;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.page.DynamicPaginatedUI;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import org.bukkit.entity.Player;

public class ChooseGroup extends DynamicPaginatedUI<RandomArenaGroup> {

    private final RandomArenaUIFactory randomArenaUIFactory;

    @Inject private ChooseGroup(CorePlugin plugin, @Assisted Player player, RandomArenaMode randomArenaMode, RandomArenaUIFactory randomArenaUIFactory) {
        super(plugin, player, CoreLang.CHOOSE_GROUP, randomArenaMode::getGroups);
        this.randomArenaUIFactory = randomArenaUIFactory;
        build();
    }

    @Override
    protected GUIButton createButton(RandomArenaGroup randomArenaGroup) {
        return new SimpleButton(new ItemStackBuilder()
                .material(Material.MAP)
                .name(CoreLang.CHOOSE_GROUP_BUTTON_NAME.replace(getPlayer(), randomArenaGroup.getGroupName())),
                () -> randomArenaUIFactory.createChooseMapUI(player, randomArenaGroup));
    }
}
