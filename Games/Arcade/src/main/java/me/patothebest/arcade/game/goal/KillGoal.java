package me.patothebest.arcade.game.goal;

import me.patothebest.arcade.arena.PointList;
import me.patothebest.gamecore.combat.CombatDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.LinkedList;

public class KillGoal extends AbstractGoal implements PointGoal {

    private final PointList kills = new PointList();
    private final int maxKills;

    public KillGoal(int maxKills) {
        this.maxKills = maxKills;
    }

    @EventHandler
    public void onCombatDeath(CombatDeathEvent event) {
        if (event.getKillerPlayer() == null) {
            return;
        }

        if (!kills.containsPlayer(event.getKillerPlayer())) {
            kills.addPlayer(event.getKillerPlayer());
        }

        kills.addPoints(event.getKillerPlayer(), 1);

        if (kills.getPoints(event.getKillerPlayer()) >= maxKills) {
            arena.getGameStarCount().clear();
            for (int i = 0; i < kills.size(); i++) {
                Player player = kills.get(i).getPlayer();
                arena.getStarCount().addPoints(player, (arena.getMaxPointsPerGame() - i));
                arena.getGameStarCount().addPlayer(player);
                arena.getGameStarCount().addPoints(player, (arena.getMaxPointsPerGame() - i));
            }

            arena.nextPhase();
        }
    }

    @Override
    public LinkedList<Player> getPlacement() {
        return kills.getPlayersOrdered();
    }

    @Override
    public void reset() {
        kills.clear();
    }

    @Override
    public int getPointsToWin() {
        return maxKills;
    }

    @Override
    public String getPointGoalName() {
        return "Kills";
    }

    @Override
    public PointList getPointList() {
        return kills;
    }
}
