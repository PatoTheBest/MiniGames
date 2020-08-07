package me.patothebest.skywars.player;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.lang.Locale;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.scoreboard.ScoreboardFile;

public class SkyWarsPlayer extends CorePlayer {

    @Inject private SkyWarsPlayer(ScoreboardFile scoreboardFile, @Assisted Locale defaultLocale) {
        super(scoreboardFile, defaultLocale);
    }
}
