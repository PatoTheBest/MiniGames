package me.patothebest.gamecore.scoreboard;

import me.patothebest.gamecore.placeholder.PlaceHolderManager;
import me.patothebest.gamecore.util.Tickable;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.animation.AnimationManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ScoreboardEntry implements Tickable {

    private final CustomScoreboard masterBoard;
    private final Player player;
    private final int interval;
    private final List<String> text;
    private final List<String> parsed;

    private ScoreboardTeam scoreboardTeam;
    private final PlayerScoreboard playerScoreboard;

    private boolean condition;

    private int tick = 0;
    private int index = 1;

    public ScoreboardEntry(CustomScoreboard masterBoard, Player player, ScoreboardTeam scoreboardTeam, Map<String, Object> map) {
        this(masterBoard, player, (PlayerScoreboard) null, map);
        this.scoreboardTeam = scoreboardTeam;
    }

    @SuppressWarnings("unchecked")
    public ScoreboardEntry(CustomScoreboard masterBoard, Player player, PlayerScoreboard playerScoreboard, Map<String, Object> map) {
        this.masterBoard = masterBoard;
        this.playerScoreboard = playerScoreboard;
        this.player = player;
        text = new ArrayList<>();
        parsed = new ArrayList<>();
        interval = (int) map.get("interval");
        text.addAll((Collection<? extends String>) map.get("text"));
    }

    public void prepare() {
        parsed.clear();
        text.forEach(s -> parsed.addAll(AnimationManager.parseAnimation(this, PlaceHolderManager.replace(player, s))));

        if(parsed.isEmpty()) {
            return;
        }

        if (!condition) {
            parsed.clear();
            text.forEach(s -> parsed.addAll(AnimationManager.parseAnimation(this, s)));
        }

        setText(parsed.get(0), !condition);
    }

    private void parseText() {
        parsed.clear();
        text.forEach(s -> parsed.addAll(AnimationManager.parseAnimation(this, PlaceHolderManager.replace(player, s))));
    }

    @Override
    public void tick() {
        if(parsed.isEmpty()) {
            return;
        }

        tick++;

        if(tick >= interval) {
            tick = 0;

            if(condition) {
                parseText();
            }

            setText(parsed.get(index++ % parsed.size()), !condition);
        }
    }

    private void setText(String text, boolean replacePlaceholders) {
        if(playerScoreboard == null) {
            if(text.contains("|")) {
                String[] split = text.split("\\|");
                if(Utils.isNumber(split[0])) {
                    scoreboardTeam.setScore(Integer.parseInt(split[0]));
                }

                text = split[1];
            }

            scoreboardTeam.setText((replacePlaceholders ? PlaceHolderManager.replace(player, text) : text));
            scoreboardTeam.getPlayerScoreboard().queueUpdate();
        } else {
            playerScoreboard.setTitle((replacePlaceholders ? PlaceHolderManager.replace(player, text) : text));
        }
    }

    public ScoreboardTeam getScoreboardTeam() {
        return scoreboardTeam;
    }

    public PlayerScoreboard getPlayerScoreboard() {
        if(playerScoreboard != null) {
            return playerScoreboard;
        }

        return scoreboardTeam.getPlayerScoreboard();
    }

    public CustomScoreboard getMasterBoard() {
        return masterBoard;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }
}
