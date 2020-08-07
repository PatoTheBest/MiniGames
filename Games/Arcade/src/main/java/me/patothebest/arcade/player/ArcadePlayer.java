package me.patothebest.arcade.player;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.lang.Locale;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.scoreboard.ScoreboardFile;

public class ArcadePlayer extends CorePlayer {

    @Inject private ArcadePlayer(ScoreboardFile scoreboardFile, @Assisted Locale defaultLocale) {
        super(scoreboardFile, defaultLocale);
    }
}
