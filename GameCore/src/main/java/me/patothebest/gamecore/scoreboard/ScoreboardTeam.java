package me.patothebest.gamecore.scoreboard;

import com.google.common.base.Splitter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScoreboardTeam {

    private final PlayerScoreboard playerScoreboard;
    private final Team team;
    private final String originalText;
    private final int originalScore;
    private final Score score;
    private String text;
    private String lastText = null;

    public ScoreboardTeam(PlayerScoreboard playerScoreboard, Team team, String originalText, int originalScore, Score score) {
        this.playerScoreboard = playerScoreboard;
        this.team = team;
        this.originalText = originalText;
        this.originalScore = originalScore;
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Team getTeam() {
        return team;
    }

    public String getOriginalText() {
        return originalText;
    }

    public int getOriginalScore() {
        return originalScore;
    }

    public PlayerScoreboard getPlayerScoreboard() {
        return playerScoreboard;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score.setScore(score);
    }

    void updateTeam() {
        if(text == null) {
            team.setPrefix("ERROR");
            return;
        }

        if (text.equalsIgnoreCase(lastText)) {
            return;
        }
        lastText = text;

        if (text.contains("#N/A")) {
            team.setPrefix("");
            team.setSuffix("");
            return;
        }

        List<String> strings = split(text);
        team.setPrefix(strings.get(0));

        if(strings.size() > 1) {
            team.setSuffix(strings.get(1));
        } else {
            team.setSuffix("");
        }
    }

    @Override
    public String toString() {
        return "ScoreboardTeam{" + "team=" + team + ", text='" + text + '\'' + '}';
    }

    private List<String> split(final String text) {
        List<String> list = new ArrayList<>();
        if (text.length() <= 16) {
            list.add(text);
            return list;
        }

        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        list.add(iterator.next());
        if (text.length() > 16) {
            String line = iterator.next();
            char lastChar = list.get(0).charAt(15);
            char firstChar = line.charAt(0);

            if ((lastChar == '&' || lastChar == ChatColor.COLOR_CHAR) && ChatColor.getByChar(firstChar) != null) {
                list.set(0, list.get(0).substring(0, 15));
                line = lastChar + line;
            }

            line = ChatColor.getLastColors(list.get(0)) + line;
            if (line.length() > 16) {
                line = line.substring(0, 15);
            }

            list.add(line);
        }
        return list;
    }
}
