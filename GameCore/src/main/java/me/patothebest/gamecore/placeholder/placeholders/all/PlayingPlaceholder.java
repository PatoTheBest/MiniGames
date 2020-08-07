package me.patothebest.gamecore.placeholder.placeholders.all;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaGroup;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.arena.ArenaManager;
import org.bukkit.entity.Player;

public class PlayingPlaceholder implements PlaceHolder {

    private final ArenaManager arenaManager;

    @Inject private PlayingPlaceholder(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @Override
    public String getPlaceholderName() {
        return "playing";
    }

    @Override
    public String replace(Player player, String args) {
        ArenaGroup arenaGroup = null;
        if (args != null) {
            arenaGroup = arenaManager.getGroup(args);
        }

        int count = 0;
        for (AbstractArena value : arenaManager.getArenas().values()) {
            if (arenaGroup == null || value.getArenaGroup() == arenaGroup) {
                count += value.getPlayers().size() + value.getSpectators().size();
            }
        }

        return String.valueOf(count);
    }

    @Override
    public String replace(AbstractArena arena) {
        return replace(null, null);
    }
}
