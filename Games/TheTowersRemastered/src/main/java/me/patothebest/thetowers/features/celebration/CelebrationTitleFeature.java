package me.patothebest.thetowers.features.celebration;

import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.title.Title;
import me.patothebest.gamecore.title.TitleManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.entity.Player;

public class CelebrationTitleFeature extends AbstractFeature {

    @Override
    public void initializeFeature() {
        super.initializeFeature();
        Arena arena = (Arena) this.arena;
        for (Player player : arena.getWinners()) {
            Title title = TitleManager.newInstance(CoreLang.WINNER_TITLE.getMessage(player));
            title.setSubtitle(CoreLang.WINNER_SUBTITLE.getMessage(player));
            title.setFadeInTime(0);
            title.setFadeOutTime(1);
            title.setStayTime(3);
            title.send(player);
        }

        for (Player player : arena.getLosers()) {
            Title title = TitleManager.newInstance(CoreLang.LOSER_TITLE.getMessage(player));
            title.setSubtitle(CoreLang.LOSER_SUBTITLE.getMessage(player));
            title.setFadeInTime(0);
            title.setFadeOutTime(1);
            title.setStayTime(3);
            title.send(player);
        }

        for (Player player : this.arena.getSpectators()) {
            Title title = TitleManager.newInstance(Lang.SPECTATOR_TITLE.getMessage(player));
            title.setSubtitle(Lang.SPECTATOR_SUBTITLE.replace(player, Utils.getColorFromDye(arena.getWinner().getColor()), arena.getWinner().getName()));
            title.setFadeInTime(0);
            title.setFadeOutTime(1);
            title.setStayTime(3);
            title.send(player);
        }
    }
}
