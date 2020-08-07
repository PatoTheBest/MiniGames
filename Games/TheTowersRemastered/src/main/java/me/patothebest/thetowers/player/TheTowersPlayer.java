package me.patothebest.thetowers.player;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.lang.Locale;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.scoreboard.ScoreboardFile;

public class TheTowersPlayer extends CorePlayer {

    @Inject private TheTowersPlayer(ScoreboardFile scoreboardFile, @Assisted Locale defaultLocale) {
        super(scoreboardFile, defaultLocale);
    }

}
