package me.patothebest.arcade.game.goal;

import me.patothebest.arcade.game.Game;
import me.patothebest.arcade.arena.Arena;

public abstract class AbstractGoal implements Goal {

    protected Arena arena;
    protected Game game;

    @Override
    public void setGame(Game game) {
        this.game = game;
        this.arena = game.getArena();
    }
}
