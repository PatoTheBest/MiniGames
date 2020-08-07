package me.patothebest.arcade.game;

import me.patothebest.arcade.Arcade;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.game.components.GameComponent;
import me.patothebest.arcade.game.goal.Goal;
import me.patothebest.arcade.game.helpers.GamePhase;
import me.patothebest.arcade.lang.Lang;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.phase.AbstractPhase;
import me.patothebest.gamecore.util.NameableObject;
import me.patothebest.gamecore.vector.ArenaLocation;
import me.patothebest.gamecore.vector.Cuboid;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Game extends AbstractPhase<Arcade, Arena> implements NameableObject, ConfigurationSerializable, GamePhase {

    private String name;

    protected final List<GameComponent> gameComponents = new ArrayList<>();
    private final List<ArenaLocation> spawns = new ArrayList<>();
    private ArenaLocation spectatorLocation;
    private Cuboid area;
    private boolean enabled;

    public Game(Arcade plugin) {
        super(plugin);
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    @SuppressWarnings("unchecked")
    public void parse(Map<String, Object> data) {
        name = (String) data.get("name");
        enabled = (boolean) data.get("enabled");

        // get the spawn location if present
        if(data.get("spectator-location") != null) {
            spectatorLocation = ArenaLocation.deserialize((Map<String, Object>) data.get("spectator-location"), arena);
        }

        if (data.get("area") != null) {
            area = new Cuboid((Map<String, Object>) data.get("area"), arena);
        }

        if(data.containsKey("spawns")) {
            List<Map<String, Object>> spawnList = (List<Map<String, Object>>) data.get("spawns");
            spawnList.forEach(stringObjectMap -> {
                spawns.add(ArenaLocation.deserialize(stringObjectMap, arena));
            });
        }

        for (GameComponent gameComponent : gameComponents) {
            gameComponent.parse(this, data);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("name", name);
        objectMap.put("enabled", enabled);

        if(spectatorLocation != null) {
            objectMap.put("spectator-location", spectatorLocation.serialize());
        }

        if (area != null) {
            objectMap.put("area", area.serialize());
        }

        objectMap.put("spawns", serializeSpawns());
        objectMap.put("type", GameType.getFromClass(getClass()).getConfigName());
        for (GameComponent gameComponent : gameComponents) {
            gameComponent.serialize(objectMap);
        }
        return objectMap;
    }

    private List<Map<String, Object>> serializeSpawns() {
        List<Map<String, Object>> serializedSpawns = new ArrayList<>();
        spawns.forEach(spawn -> serializedSpawns.add(spawn.serialize()));
        return serializedSpawns;
    }

    public boolean canArenaBeEnabled(CommandSender sender) {
        if (!enabled){
            return false;
        }

        boolean canBeEnabled = true;

        // check if the arena area is set
        if (area == null) {
            canBeEnabled = false;
            sender.sendMessage(Lang.SETUP_ERROR_SET_AREA.getMessage(sender));
        }

        // check if the spectator location is set
        if (spectatorLocation == null) {
            canBeEnabled = false;
            sender.sendMessage(Lang.SETUP_ERROR_SPECTATOR_LOCATION.getMessage(sender));
        }

        if (spawns.isEmpty()) {
            canBeEnabled = false;
        }

        for (GameComponent gameComponent : gameComponents) {
            if (!gameComponent.canBeEnabled(sender)) {
                canBeEnabled = false;
            }
        }

        return canBeEnabled;
    }

    @Override
    public void start() {
        getGoal().setGame(this);
        arena.getEventRegistry().registerListener(getGoal());
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        arena.getEventRegistry().unRegisterListener(getGoal());
        getGoal().reset();
    }

    public List<ArenaLocation> getSpawns() {
        return spawns;
    }

    @Override
    public ArenaState getArenaState() {
        return ArenaState.IN_GAME;
    }

    public Arena getArena() {
        return arena;
    }

    public ArenaLocation getSpectatorLocation() {
        return spectatorLocation;
    }

    public abstract Goal getGoal();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setSpectatorLocation(ArenaLocation spectatorLocation) {
        this.spectatorLocation = spectatorLocation;
    }

    public void setArea(Cuboid area) {
        this.area = area;
    }

    @Override
    public Game getGame() {
        return this;
    }

    public Cuboid getArea() {
        return area;
    }

    public abstract GameType getGameType();

    public <T extends GameComponent> T getComponent(Class<? extends T> gameComponentClass) {
        for (GameComponent gameComponent : gameComponents) {
            if (gameComponent.getClass() == gameComponentClass) {
                return (T) gameComponent;
            }
        }

        return null;
    }
}
