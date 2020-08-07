package me.patothebest.hungergames.phase.phases;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.feature.features.protection.DeathmatchFeature;
import me.patothebest.gamecore.vector.ArenaLocation;
import me.patothebest.hungergames.HungerGames;
import me.patothebest.hungergames.phase.AbstractHungerGamesPhase;
import me.patothebest.hungergames.phase.PhaseType;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeathmatchPhase extends AbstractHungerGamesPhase {

    @Inject private DeathmatchPhase(HungerGames plugin) {
        super(plugin);
    }

    @Override
    public void configure() {
        setPreviousPhaseFeatures(true);
        registerFeature(DeathmatchFeature.class);
    }

    @Override
    public void start() {
        List<ArenaLocation> spawnLocations;

        if (arena.getTeams().size() > 1) {
            spawnLocations = new ArrayList<>();
            for (AbstractGameTeam value : arena.getTeams().values()) {
                spawnLocations.add(value.getSpawn());
            }
        } else {
            spawnLocations = arena.getSpawns();
        }
        Location centerLocation = arena.getCenterLocation();

        if (arena.getTeams().size() > 1) {
            for (AbstractGameTeam value : arena.getTeams().values()) {
                for (Player player : value.getPlayers()) {
                    player.teleport(value.getSpawn().clone().add(0.5, 1, 0.5));
                }
            }
        } else {
            int spawnIndex = 0;
            for (Player player : arena.getPlayers()) {
                player.teleport(spawnLocations.get(spawnIndex++).clone().add(0.5, 1, 0.5));
            }
        }

        ArenaLocation singleSpawn;
        if (arena.getTeams().size() > 1) {
            singleSpawn = arena.getTeams().values().iterator().next().getSpawn();
        } else {
            singleSpawn = arena.getSpawns().get(0);
        }
        double distance = centerLocation.distance(singleSpawn) + 3;
        WorldBorder worldBorder = arena.getWorld().getWorldBorder();
        worldBorder.setCenter(centerLocation);
        worldBorder.setSize(distance*2);
        worldBorder.setDamageAmount(2);
        super.start();
    }

    @Override
    public PhaseType getPhaseType() {
        return PhaseType.DEATHMATCH;
    }
}