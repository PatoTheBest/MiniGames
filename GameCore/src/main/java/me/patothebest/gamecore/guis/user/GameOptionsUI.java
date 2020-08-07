package me.patothebest.gamecore.guis.user;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.feature.features.gameoptions.GameOptionsFeature;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.entity.Player;

public class GameOptionsUI extends GUIPage {

    private final GameOptionsFeature gameOptionsFeature;

    @Inject private GameOptionsUI(CorePlugin plugin, @Assisted Player player, @Assisted GameOptionsFeature gameOptionsFeature) {
        super(plugin, player, CoreLang.GUI_GAME_OPTIONS_TITLE.getMessage(player), 27);
        this.gameOptionsFeature = gameOptionsFeature;
        build();
    }

    @Override
    protected void buildPage() {
        gameOptionsFeature.getButtonsInMenu().forEach((slot, gameOption) -> addButton(gameOption.getButton(getPlayer()), slot));
    }
}
