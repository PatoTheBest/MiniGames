package me.patothebest.thetowers.animation.animations;

import com.google.inject.Inject;
import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.GameTeam;
import me.patothebest.gamecore.animation.Animation;
import me.patothebest.gamecore.command.ChatColor;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.scoreboard.ScoreboardEntry;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.thetowers.arena.PointList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TeamInfoAnimation extends Animation {

    private final PlayerManager playerManager;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private TeamInfoAnimation(PlayerManager playerManager) {
        super("team_info");
        this.playerManager = playerManager;
        this.condition = true;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public List<String> convert(String animation, Map<String, String> arguments, ScoreboardEntry scoreboardEntry) {
        Arena currentArena = (Arena) playerManager.getPlayer(scoreboardEntry.getPlayer()).getCurrentArena();
        PointList gameTeamScoredPoints = currentArena.getGameTeamScoredPoints();
        GameTeam gameTeam = (GameTeam) currentArena.getTeam(animation);
        scoreboardEntry.getScoreboardTeam().setScore(gameTeamScoredPoints.size() - 1 - gameTeamScoredPoints.getPosition(gameTeam.getName()));
        return Collections.singletonList(Utils.getColorFromDye(gameTeam.getColor()).toString() + gameTeam.getName() + ": " + ChatColor.WHITE + gameTeam.getPoints());
    }
}
