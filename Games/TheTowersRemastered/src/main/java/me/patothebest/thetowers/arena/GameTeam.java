package me.patothebest.thetowers.arena;

import me.patothebest.thetowers.stats.statistics.PointsScoredStatistic;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.title.Title;
import me.patothebest.gamecore.title.TitleManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.Cuboid;
import net.megaplanet.thetowers.api.IGameTeam;
import net.megaplanet.thetowers.api.events.PlayerScoreEvent;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.DyeColor;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class GameTeam extends AbstractGameTeam implements IGameTeam {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final Map<String, Cuboid> pointAreas = new HashMap<>();
    private int points;
    private Arena arena;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public GameTeam(Arena arena, String name, DyeColor dyeColor) {
        super(arena, name, dyeColor);
    }

    public GameTeam(Arena arena, Map<String, Object> data) {
        super(arena, data);
        this.arena = arena;

        // get the item point areas list
        List<Map<String, Object>> areas = (List<Map<String, Object>>) data.get("pointareas");
        areas.forEach(area -> {
            Cuboid cuboid = new Cuboid(area, arena);
            pointAreas.put(cuboid.getName(), cuboid);
        });
    }

    // -------------------------------------------- //
    // PUBLIC METHODS
    // -------------------------------------------- //

    public void scorePoint(Player player) {
        // add a point to the total team's score
        points++;
        arena.getGameTeamScoredPoints().addPoints(name, 1);
        arena.getGameTeamScoredPoints().sort();

        if (!arena.getScoredPoints().containsPlayer(player.getName())) {
            arena.getScoredPoints().addPlayer(this, player.getName());
        }
        arena.getScoredPoints().addPoints(player.getName(), 1);

        // add a point to the player statistics
        arena.getStatsManager().getStatisticByClass(PointsScoredStatistic.class).updateStat(player, 1);

        // call the score event
        arena.getPlugin().callEvent(new PlayerScoreEvent(player, arena));

        // teleport the player back to spawn
        player.teleport(spawn);

        // remove pearls thrown by player when they score
        for (EnderPearl enderPearl : arena.getWorld().getEntitiesByClass(EnderPearl.class)) {
            if (enderPearl.getShooter() == player) {
                enderPearl.remove();
            }
        }

        // send the message to the arena
        arena.sendMessageToArena(locale -> Lang.PLAYER_SCORED.getMessage(locale).replace("%player%", player.getName()).replace("%teamcolor%", Utils.getColorFromDye(color).toString()));
        arena.sendActionBarToArena(locale -> Lang.PLAYER_SCORED.getMessage(locale).replace("%player%", player.getName()).replace("%teamcolor%", Utils.getColorFromDye(color).toString()));

        // check if the score limit has been reached
        arena.checkWin();
    }

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);

        // teleport the player to the spawn
        player.teleport(spawn);

        // add all the default potion effects
        player.addPotionEffects(arena.getPlugin().getFileManager().getConfig().getPotionEffects());

        // tell which team the player is in
        player.sendMessage(Lang.YOU_ARE_IN_TEAM.getMessage(player).replace("%teamcolor%", Utils.getColorFromDye(color).toString()).replace("%teamname%", name));
        Title title = TitleManager.newInstance(CoreLang.GAME_STARTING_TITLE.getMessage(player));
        title.setSubtitle(Lang.YOU_ARE_IN_TEAM.getMessage(player).replace("%teamcolor%", Utils.getColorFromDye(color).toString()).replace("%teamname%", name));
        title.setFadeInTime(0);
        title.setFadeOutTime(1);
        title.setStayTime(2);
        title.send(player);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> objectMap = super.serialize();

        objectMap.put("pointareas", serializeAreas());
        return objectMap;
    }

    private List<Map<String, Object>> serializeAreas() {
        List<Map<String, Object>> areasList = new ArrayList<>();
        pointAreas.forEach((key, value) -> areasList.add(value.serialize()));
        return areasList;
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    public void addArea(Cuboid cuboid) {
        pointAreas.put(cuboid.getName(), cuboid);
    }

    public Map<String, Cuboid> getPointAreas() {
        return pointAreas;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "GameTeam{" +
                "arena=" + arena.getName() +
                ", players=" + players +
                ", points=" + points +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", spawn=" + spawn +
                ", pointAreas=" + pointAreas +
                '}';
    }
}
