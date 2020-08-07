package me.patothebest.thetowers.animation.animations;

import com.google.inject.Inject;
import me.patothebest.gamecore.animation.Animation;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.scoreboard.ScoreboardEntry;
import me.patothebest.gamecore.scoreboard.ScoreboardTeam;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForTeamAnimation extends Animation {

    private final PlayerManager playerManager;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private ForTeamAnimation(PlayerManager playerManager) {
        super("forteam");
        this.playerManager = playerManager;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public List<String> convert(String animation, Map<String, String> arguments, ScoreboardEntry scoreboardEntry) {
        Collection<AbstractGameTeam> gameTeams = playerManager.getPlayer(scoreboardEntry.getPlayer()).getCurrentArena().getTeams().values();
        scoreboardEntry.getPlayerScoreboard().remove(scoreboardEntry.getScoreboardTeam());
        scoreboardEntry.getMasterBoard().getScoreboardEntryMap().values().remove(scoreboardEntry);

        for(AbstractGameTeam gameTeam : gameTeams) {
            Map<String, Object> map = new HashMap<>();
            map.put("interval", 20);
            map.put("text", Collections.singletonList("<team_info>" + gameTeam.getName() + "</team_info>"));

            ScoreboardTeam scoreboardTeam = scoreboardEntry.getPlayerScoreboard().getOrCrateTeam();
            ScoreboardEntry scoreboardEntry1 = new ScoreboardEntry(scoreboardEntry.getMasterBoard(), scoreboardEntry.getPlayer(), scoreboardTeam, map);
            scoreboardEntry1.prepare();
            scoreboardEntry.getMasterBoard().getScoreboardEntryMap().put(gameTeam.getName() + "-team", scoreboardEntry1);
        }

        return Collections.emptyList();
    }
}
