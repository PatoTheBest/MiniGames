package me.patothebest.gamecore.feature.features.celebration;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.title.Title;
import me.patothebest.gamecore.title.TitleManager;
import org.bukkit.entity.Player;

public class CelebrationTitleFeature extends AbstractFeature {

    @Override
    public void initializeFeature() {
        super.initializeFeature();

        for (Player player : arena.getPlayers()) {
            Title title = TitleManager.newInstance(CoreLang.WINNER_TITLE.getMessage(player));
            title.setSubtitle(CoreLang.WINNER_SUBTITLE.getMessage(player));
            title.setFadeInTime(0);
            title.setFadeOutTime(1);
            title.setStayTime(3);
            title.send(player);
        }

        for (Player player : arena.getSpectators()) {
            Title title = TitleManager.newInstance(CoreLang.LOSER_TITLE.getMessage(player));
            title.setSubtitle(CoreLang.LOSER_SUBTITLE.getMessage(player));
            title.setFadeInTime(0);
            title.setFadeOutTime(1);
            title.setStayTime(3);
            title.send(player);
        }
    }
}
