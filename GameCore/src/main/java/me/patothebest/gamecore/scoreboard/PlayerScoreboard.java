package me.patothebest.gamecore.scoreboard;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class PlayerScoreboard {

    private final static String[] scoreTeams = new String[16];
    private static int SCOREBOARD_INDEX;

    private final Player player;
    private final Scoreboard scoreboard;
    private boolean update;
    private final Objective obj;
    private final Map<Integer, ScoreboardTeam> teams;

    public PlayerScoreboard(Player player) {
        this.player = player;
        this.teams = new HashMap<>();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.obj = scoreboard.registerNewObjective("TT-" + SCOREBOARD_INDEX++, "dummy");
        this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void tryChange(String text, int score) {
        Validate.notNull(text, "Scoreboard text cannot be null");

        getOrCreateTeam(score).setText(text);
    }

    public void remove(int score) {
        ScoreboardTeam team = getTeam(score);

        if(team == null) {
            throw new IllegalArgumentException("Team cannot be found");
        }

        team.getTeam().unregister();
        scoreboard.resetScores(team.getScore().getEntry());
        teams.remove(score);
    }

    public void update() {
        if(update) {
            forceUpdate();
            update = false;
        }
    }

    public ScoreboardTeam getOrCrateTeam() {
        for(int i = 0; i < 15; i++) {
            if(teams.keySet().contains(i)) {
               continue;
            }

            return getOrCreateTeam(i);
        }

        return null;
    }

    public void destroy() {
        scoreboard.getTeams().forEach(Team::unregister);
        teams.clear();
    }

    public void remove(ScoreboardTeam scoreboardTeam) {
        remove(scoreboardTeam.getOriginalScore());
    }

    public void forceUpdate() {
        teams.values().forEach(ScoreboardTeam::updateTeam);
    }

    public void queueUpdate() {
        update = true;
    }

    public void setTitle(String title) {
        obj.setDisplayName(title);
    }

    public void show() {
        player.setScoreboard(scoreboard);
    }

    public void show(Player player) {
        player.setScoreboard(scoreboard);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    /* PRIVATE METHODS */

    private ScoreboardTeam getTeam(int score) {
        return teams.get(score);
    }

    ScoreboardTeam getOrCreateTeam(int score) {
        if(getTeam(score) != null) {
            return getTeam(score);
        }

        Score score1 = obj.getScore(scoreTeams[score]);
        score1.setScore(score);
        ScoreboardTeam scoreboardTeam = new ScoreboardTeam(this, scoreboard.registerNewTeam(scoreTeams[score]), scoreTeams[score], score, score1);
        scoreboardTeam.getTeam().addEntry(scoreTeams[score]);
        teams.put(score, scoreboardTeam);
        return scoreboardTeam;
    }

    static {
        for(int i = 0; i < 15; i++) {
            scoreTeams[i] = ChatColor.values()[i].toString() + ChatColor.RESET;
        }
    }
}
