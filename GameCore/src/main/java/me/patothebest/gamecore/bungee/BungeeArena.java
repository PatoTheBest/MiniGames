package me.patothebest.gamecore.bungee;

import com.google.inject.Injector;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import org.bukkit.DyeColor;

import java.util.Map;

public class BungeeArena extends AbstractArena {

    public BungeeArena(String name, String worldName, Injector injector) {
        super(name, worldName, injector);
    }




    @Override
    public void initializePhase() {
        throw new UnsupportedOperationException("Not supported for bungee arena");
    }

    @Override
    public void checkWin() {
        throw new UnsupportedOperationException("Not supported for bungee arena");
    }

    @Override
    public AbstractGameTeam createTeam(String name, DyeColor color) {
        throw new UnsupportedOperationException("Not supported for bungee arena");
    }

    @Override
    public AbstractGameTeam createTeam(Map<String, Object> data) {
        throw new UnsupportedOperationException("Not supported for bungee arena");
    }

    @Override
    public int getMinimumRequiredPlayers() {
        return 0;
    }
}
