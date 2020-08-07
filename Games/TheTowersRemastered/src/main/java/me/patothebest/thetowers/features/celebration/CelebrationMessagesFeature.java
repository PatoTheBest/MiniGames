package me.patothebest.thetowers.features.celebration;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.GameTeam;
import me.patothebest.thetowers.arena.PointList;
import me.patothebest.thetowers.language.Lang;

public class CelebrationMessagesFeature extends AbstractFeature {

    private final PluginScheduler pluginScheduler;

    @Inject private CelebrationMessagesFeature(PluginScheduler pluginScheduler) {
        this.pluginScheduler = pluginScheduler;
    }

    @Override
    public void initializeFeature() {
        super.initializeFeature();
        Arena arena = (Arena) this.arena;
        GameTeam winner = arena.getWinner();

        // send the team won message to the players
        arena.sendMessageToArena(locale -> "");
        arena.sendMessageToArena(locale -> Lang.TEAM_WON.getMessage(locale).replace("%teamcolor%", Utils.getColorFromDye(winner.getColor()).toString()).replace("%teamname%", winner.getName()));
        arena.sendMessageToArena(locale -> "");

        pluginScheduler.runTaskLater(() -> {
            arena.sendMessageToArena(locale -> "");
            arena.sendMessageToArena(locale -> Lang.SUMMARY_TIME.replace(locale, arena.getElapsedTime() / 60000L));
            arena.sendMessageToArena(player -> Lang.SUMMARY_KILLS.replace(player, Math.max(arena.getKills().getPoints(player.getName()), 0)));
            arena.sendMessageToArena(player -> Lang.SUMMARY_POINTS.replace(player, Math.max(arena.getScoredPoints().getPoints(player.getName()), 0)));
        }, 50L);

        pluginScheduler.runTaskLater(() -> {
            arena.sendMessageToArena(locale -> "");
            arena.sendMessageToArena(Lang.SUMMARY_TEAMS_HEADER::getMessage);
            PointList teamPoints = new PointList();
            for (AbstractGameTeam team : arena.getTeams().values()) {
                teamPoints.addPlayer((GameTeam) team, team.getName());
                teamPoints.addPoints(team.getName(), ((GameTeam)team).getPoints());
            }

            teamPoints.sort();
            for (int i = 0; i < teamPoints.size(); i++) {
                int place = i + 1;
                GameTeam gameTeam = teamPoints.get(i).getGameTeam();
                arena.sendMessageToArena(locale -> Lang.SUMMARY_TEAMS.replace(locale, place, Utils.getColorFromDye(gameTeam.getColor()), gameTeam.getName(), gameTeam.getPoints()));
            }
        }, 100L);

        pluginScheduler.runTaskLater(() -> {
            arena.sendMessageToArena(locale -> "");
            arena.sendMessageToArena(Lang.SUMMARY_MOST_POINTS_HEADER::getMessage);
            PointList scoredPoints = arena.getScoredPoints();
            scoredPoints.sort();
            for (int i = 0; i < Math.min(scoredPoints.size(), 5); i++) {
                int place = i + 1;
                PointList.Entry entry = scoredPoints.get(i);
                GameTeam gameTeam = entry.getGameTeam();
                arena.sendMessageToArena(locale -> Lang.SUMMARY_MOST_POINTS.replace(locale, place, Utils.getColorFromDye(gameTeam.getColor()), entry.getPlayer(), entry.getPoints()));
            }
        }, 150L);

        pluginScheduler.runTaskLater(() -> {
            arena.sendMessageToArena(locale -> "");
            arena.sendMessageToArena(Lang.SUMMARY_MOST_KILLS_HEADER::getMessage);
            PointList kills = arena.getKills();
            kills.sort();
            for (int i = 0; i < Math.min(kills.size(), 5); i++) {
                int place = i + 1;
                PointList.Entry entry = kills.get(i);
                GameTeam gameTeam = entry.getGameTeam();
                arena.sendMessageToArena(locale -> Lang.SUMMARY_MOST_KILLS.replace(locale, place, Utils.getColorFromDye(gameTeam.getColor()), entry.getPlayer(), entry.getPoints()));
            }
        }, 200L);
    }
}
