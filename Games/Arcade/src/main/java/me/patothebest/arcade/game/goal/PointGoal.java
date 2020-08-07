package me.patothebest.arcade.game.goal;

import me.patothebest.arcade.arena.PointList;

public interface PointGoal extends Goal {

    int getPointsToWin();

    String getPointGoalName();

    PointList getPointList();

}
