package me.patothebest.skywars.arena;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.arena.ArenaGroup;
import me.patothebest.gamecore.arena.types.CentrableArena;
import me.patothebest.gamecore.arena.types.ChestArena;
import me.patothebest.gamecore.arena.types.SpawneableArena;
import me.patothebest.gamecore.cosmetics.cage.CageStructure;
import me.patothebest.gamecore.event.arena.GameEndEvent;
import me.patothebest.gamecore.feature.features.chests.refill.ChestLocation;
import me.patothebest.gamecore.feature.features.gameoptions.GameOptionsFeature;
import me.patothebest.gamecore.feature.features.gameoptions.individual.CageSelectorGameOption;
import me.patothebest.gamecore.feature.features.gameoptions.individual.ProjectileTrailSelectorGameOption;
import me.patothebest.gamecore.feature.features.gameoptions.individual.WalkTrailSelectorGameOption;
import me.patothebest.gamecore.feature.features.gameoptions.time.TimeSelectorGameOption;
import me.patothebest.gamecore.feature.features.gameoptions.weather.WeatherSelectorGameOption;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.phase.phases.CagePhase;
import me.patothebest.gamecore.phase.phases.CelebrationPhase;
import me.patothebest.gamecore.phase.phases.EndPhase;
import me.patothebest.gamecore.phase.phases.LobbyPhase;
import me.patothebest.gamecore.phase.phases.TeamAssignPhase;
import me.patothebest.gamecore.util.PlayerList;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.ArenaLocation;
import me.patothebest.skywars.lang.Lang;
import me.patothebest.skywars.phase.PhaseType;
import me.patothebest.skywars.phase.SkyWarsGameBasePhase;
import me.patothebest.skywars.phase.SkyWarsPhase;
import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Arena extends AbstractArena implements SpawneableArena, ChestArena, CentrableArena {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private ArenaMode arenaMode;
    private final List<ArenaLocation> spawns = new ArrayList<>();
    private final List<ArenaLocation> chestLocations = new ArrayList<>();
    private final List<ArenaLocation> midChestLocations = new ArrayList<>();
    private Location centerLocation;
    private int spawnHeight;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private Arena(@Assisted("name") String name, @Assisted("worldName") String worldName, Provider<Injector> injector) {
        super(name, worldName, injector.get());

        arenaMode = ArenaMode.NORMAL;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void parseData(Map<String, Object> map) {
        super.parseData(map);

        if(map.containsKey("arenamode")) {
            this.arenaMode = Utils.getEnumValueFromString(ArenaMode.class, (String) map.get("arenamode"));
        }

        if(map.containsKey("spawn-height")) {
            this.spawnHeight = (int) map.get("spawn-height");
        } else {
            this.spawnHeight = 15;
        }

        if(map.containsKey("chests")) {
            List<Map<String, Object>> chestList = (List<Map<String, Object>>) map.get("chests");
            chestList.forEach(stringObjectMap -> {
                chestLocations.add(ArenaLocation.deserialize(stringObjectMap, this));
            });
        }

        if(map.containsKey("mid-chests")) {
            List<Map<String, Object>> chestList = (List<Map<String, Object>>) map.get("mid-chests");
            chestList.forEach(stringObjectMap -> {
                midChestLocations.add(ArenaLocation.deserialize(stringObjectMap, this));
            });
        }

        if(arenaGroup == ArenaType.SOLO) {
            if(map.containsKey("spawns")) {
                List<Map<String, Object>> spawnList = (List<Map<String, Object>>) map.get("spawns");
                spawnList.forEach(stringObjectMap -> {
                    spawns.add(ArenaLocation.deserialize(stringObjectMap, this));
                });
            }

            needsLobbyLocation = false;
        }
    }

    @Override
    public void initializePhase() {
        if(arenaGroup == null) {
            return;
        }

        if(arenaGroup == ArenaType.TEAM) {
            LobbyPhase lobbyPhase = addPhase(LobbyPhase.class);
            lobbyPhase.setTeamItemSlot(3);

            GameOptionsFeature gameOptionsFeature = lobbyPhase.registerFeature(GameOptionsFeature.class);
            gameOptionsFeature.registerGameOption(lobbyPhase, CageSelectorGameOption.class, 9);
            gameOptionsFeature.registerGameOption(lobbyPhase, TimeSelectorGameOption.class, 11);
            gameOptionsFeature.registerGameOption(lobbyPhase, WeatherSelectorGameOption.class, 13);
            gameOptionsFeature.registerGameOption(lobbyPhase, ProjectileTrailSelectorGameOption.class, 15);
            gameOptionsFeature.registerGameOption(lobbyPhase, WalkTrailSelectorGameOption.class, 17);
            gameOptionsFeature.setSlot(5);

            addPhase(TeamAssignPhase.class);
        }

        CagePhase cagePhase = addPhase(CagePhase.class);

        GameOptionsFeature gameOptionsFeature = (GameOptionsFeature) cagePhase.registerFeature(GameOptionsFeature.class);
        gameOptionsFeature.registerGameOption(cagePhase, CageSelectorGameOption.class, 9);
        gameOptionsFeature.registerGameOption(cagePhase, TimeSelectorGameOption.class, 11);
        gameOptionsFeature.registerGameOption(cagePhase, WeatherSelectorGameOption.class, 13);
        gameOptionsFeature.registerGameOption(cagePhase, ProjectileTrailSelectorGameOption.class, 15);
        gameOptionsFeature.registerGameOption(cagePhase, WalkTrailSelectorGameOption.class, 17);

        if(arenaGroup == ArenaType.TEAM) {
            cagePhase.setAutoStart(true);
            cagePhase.setCageType(CageStructure.TEAM);
        } else {
            cagePhase.setCanJoin(true);
            addPhase(TeamAssignPhase.class);
        }

        addPhase(SkyWarsGameBasePhase.class);

        ConfigurationSection phases = config.getConfigurationSection(getArenaType().getName().toLowerCase() + "-phases");
        for (String phaseString : phases.getKeys(false)) {
            ConfigurationSection phasesConfigurationSection = phases.getConfigurationSection(phaseString);
            PhaseType phaseType = PhaseType.getPhaseType(phasesConfigurationSection.getString("type"));

            if(phaseType == null) {
                Utils.printError("Unknown phase type " + phasesConfigurationSection.getString("type") + " in " + phasesConfigurationSection.getCurrentPath());
                continue;
            }

            SkyWarsPhase phase = addPhase(phaseType.getPhaseClass());
            phase.parseExtraData(phasesConfigurationSection);
            ((SkyWarsPhase)getPreviousPhase(phase)).setTimeTilNextPhase(phasesConfigurationSection.getInt("time"));
        }

        addPhase(CelebrationPhase.class);
        addPhase(EndPhase.class);

        if(arenaGroup == ArenaType.SOLO) {
            createTeam("Players", DyeColor.YELLOW).setTeamChatPrefix(false);
        }
    }

    @Override
    public void checkWin() {
        if(arenaGroup == ArenaType.SOLO) {
            if(getPlayers().size() == 1) {
                // call the ArenaEndEvent with the winners and losers
                plugin.callEvent(new GameEndEvent(this, getPlayers(), new PlayerList()));
                setPhase(CelebrationPhase.class);
            }
        } else {
            int teamsAlive = 0;

            for (AbstractGameTeam gameTeam : teams.values()) {
                if(gameTeam.getPlayers().size() > 0) {
                    teamsAlive++;
                }
            }

            if(teamsAlive <= 1) {
                // call the ArenaEndEvent with the winners and losers
                plugin.callEvent(new GameEndEvent(this, getPlayers(), new PlayerList()));
                setPhase(CelebrationPhase.class);
            }
        }
    }

    @Override
    public List<ArenaLocation> getArenaChests(ChestLocation chestLocation) {
        if (chestLocation == ChestLocations.ISLAND) {
            return chestLocations;
        } else if (chestLocation == ChestLocations.MID) {
            return midChestLocations;
        }

        throw new IllegalArgumentException("Unknown chest location " + chestLocation + "!");
    }

    // -------------------------------------------- //
    // SAVING
    // -------------------------------------------- //

    @Override
    public boolean canArenaBeEnabled(CommandSender commandSender) {

        if (!super.canArenaBeEnabled(commandSender)) return false;

        if(arenaGroup == ArenaType.SOLO) {
            if (spawns.size() < maxPlayers){
                commandSender.sendMessage(Lang.ARENA_NOT_ENOUGH_SPAWNS.getMessage(commandSender));
                return false;
            }
        }

        return true;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> dataMap = super.serialize();

        dataMap.put("arenamode", arenaMode.name());

        if(arenaGroup == ArenaType.SOLO) {
            dataMap.put("spawns", serializeSpawns());
        }

        dataMap.put("chests", serializeChests());
        dataMap.put("mid-chests", serializeMidChests());
        dataMap.put("spawn-height", spawnHeight);

        return dataMap;
    }

    private List<Map<String, Object>> serializeSpawns() {
        List<Map<String, Object>> serializedSpawns = new ArrayList<>();
        spawns.forEach(spawn -> serializedSpawns.add(spawn.serialize()));
        return serializedSpawns;
    }

    private List<Map<String, Object>> serializeChests() {
        List<Map<String, Object>> serializedChests = new ArrayList<>();
        chestLocations.forEach(chestLocation -> serializedChests.add(chestLocation.serialize()));
        return serializedChests;
    }

    private List<Map<String, Object>> serializeMidChests() {
        List<Map<String, Object>> serializedChests = new ArrayList<>();
        midChestLocations.forEach(chestLocation -> serializedChests.add(chestLocation.serialize()));
        return serializedChests;
    }


    @Override
    protected List<Map<String, Object>> serializeTeams() {
        return arenaGroup == ArenaType.SOLO ? null : super.serializeTeams();
    }

    // -------------------------------------------- //
    // TEAMS
    // -------------------------------------------- //

    @Override
    public GameTeam createTeam(String name, DyeColor color) {
        Validate.isTrue(!teams.containsKey(name), "Team already exists!");
        GameTeam gameTeam = new GameTeam(this, name, color);
        addTeam(gameTeam);
        return gameTeam;
    }

    @Override
    public GameTeam createTeam(Map<String, Object> data) {
        return new GameTeam(this, data);
    }

    @Override
    public int getMinimumRequiredPlayers() {
        return 2;
    }

    @Override
    public void enableArena() {
        chestLocations.clear();
        for (Chunk chunk : area.getChunks()) {
            chunk.load();

            int cx = chunk.getX() << 4;
            int cz = chunk.getZ() << 4;
            for (int x = cx; x < cx + 16; x++) {
                for (int z = cz; z < cz + 16; z++) {
                    for (int y = 0; y < 256; y++) {
                        if (getWorld().getBlockAt(x, y, z).getType() == Material.CHEST.parseMaterial() || getWorld().getBlockAt(x, y, z).getType() == Material.TRAPPED_CHEST.parseMaterial()) {
                            Location location = getWorld().getBlockAt(x, y, z).getLocation();
                            if(!midChestLocations.contains(new ArenaLocation(this, location))) {
                                chestLocations.add(new ArenaLocation(this, location));
                            }
                        }
                    }
                }
            }
        }

        super.enableArena();
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //

    @Override
    public Location getCenterLocation() {
        if(centerLocation == null) {
            if(arenaGroup == ArenaType.SOLO) {
                centerLocation = Utils.getCenterLocation(spawns);
            } else {
                List<ArenaLocation> locations = new ArrayList<>();
                for (AbstractGameTeam gameTeam : teams.values()) {
                    locations.add(gameTeam.getSpawn());
                }

                centerLocation = Utils.getCenterLocation(locations);
            }
        }

        return centerLocation;
    }

    public ArenaGroup getArenaType() {
        return arenaGroup;
    }

    public List<ArenaLocation> getSpawns() {
        return spawns;
    }

    public List<ArenaLocation> getMidChestLocations() {
        return midChestLocations;
    }

    public ArenaMode getArenaMode() {
        return arenaMode;
    }

    public void setArenaMode(ArenaMode arenaMode) {
        this.arenaMode = arenaMode;
    }

    public int getSpawnHeight() {
        return spawnHeight;
    }

    public void setSpawnHeight(int spawnHeight) {
        this.spawnHeight = spawnHeight;
    }

    @Override
    public int getMaxPlayers() {
        return arenaGroup == ArenaType.SOLO ? spawns.size() : super.getMaxPlayers();
    }
}
