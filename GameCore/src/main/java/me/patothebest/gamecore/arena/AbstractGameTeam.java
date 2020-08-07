package me.patothebest.gamecore.arena;

import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.modifiers.KitModifier;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.ArenaLocation;
import me.patothebest.gamecore.util.NameableObject;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractGameTeam implements NameableObject, ConfigurationSerializable {

    // temporary objects only for the game
    protected final AbstractArena arena;
    protected final List<Player> players = new ArrayList<>();
    protected final String name;
    protected final DyeColor color;
    protected boolean teamChatPrefix = true;

    // things that are going to be saved
    protected ArenaLocation spawn;

    public AbstractGameTeam(AbstractArena arena, String name, DyeColor color) {
        this.arena = arena;
        this.name = name;
        this.color = color;
    }

    @SuppressWarnings("unchecked")
    public AbstractGameTeam(AbstractArena arena, Map<String, Object> data) {
        this.arena = arena;

        name = (String) data.get("name");
        color = Utils.getEnumValueFromString(DyeColor.class, (String) data.get("color"));

        // get the spawn location if present
        if(data.get("spawn") != null) {
            spawn = ArenaLocation.deserialize((Map<String, Object>) data.get("spawn"), arena);
        }
    }

    public void addPlayer(Player player) {
        // add the player to the team
        players.add(player);
        IPlayer corePlayer = arena.getPlayerManager().getPlayer(player);
        corePlayer.setGameTeam(this);
        arena.addPlayerToLastTeam(this, player);
    }

    public void giveStuff(Player player) {
        IPlayer corePlayer = arena.getPlayerManager().getPlayer(player);
        if(corePlayer.getKit() != null && !corePlayer.isPermanentKit(corePlayer.getKit())) {
            if(corePlayer.canUseKit(corePlayer.getKit())) {
                corePlayer.addKitUses(corePlayer.getKit(), -1);
            } else {
                corePlayer.setKit(arena.getKitManager().getDefaultKit());
                corePlayer.notifyObservers(KitModifier.SET_DEFAULT, corePlayer.getKit());
            }
        }

        // give kit and kit potion effects
        arena.getKitManager().applyKit(player, this);
        arena.getKitManager().applyPotionEffects(player);
    }

    public void removePlayer(Player player) {
        // remove the player
        arena.getPlayerManager().getPlayer(player).setGameTeam(null);
        players.remove(player);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("name", name);
        objectMap.put("color", color.name());

        if(spawn != null) {
            objectMap.put("spawn", spawn.serialize());
        }

        return objectMap;
    }


    public DyeColor getColor() {
        return color;
    }

    @Override
    public String getName() {
        return name;
    }

    public ArenaLocation getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = new ArenaLocation(arena, spawn);
    }

    public List<Player> getPlayers() {
        return players;
    }

    boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public void setTeamChatPrefix(boolean teamChatPrefix) {
        this.teamChatPrefix = teamChatPrefix;
    }

    public boolean hasTeamChatPrefix() {
        return teamChatPrefix;
    }
}
