package me.patothebest.arcade.game.components.floor;

import me.patothebest.arcade.game.Game;
import me.patothebest.arcade.game.components.GameComponent;
import me.patothebest.gamecore.vector.Cuboid;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class FloorComponent implements GameComponent {

    private Cuboid floor;

    @Override
    public void parse(Game game, Map<String, Object> data) {
        if (data.containsKey("floor")) {
            floor = new Cuboid((Map<String, Object>) data.get("floor"), game.getArena());
        }
    }

    @Override
    public boolean canBeEnabled(CommandSender commandSender) {
        return floor != null;
    }

    @Override
    public void serialize(Map<String, Object> data) {
        if (floor != null) {
            data.put("floor", floor.serialize());
        }
    }

    public Cuboid getFloor() {
        return floor;
    }

    public void setFloor(Cuboid floor) {
        this.floor = floor;
    }
}
