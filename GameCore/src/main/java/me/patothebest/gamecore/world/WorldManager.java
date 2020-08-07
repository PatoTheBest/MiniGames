package me.patothebest.gamecore.world;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.modules.ListenerModule;

public class WorldManager implements ListenerModule {

    private final ArenaManager arenaManager;

    @Inject private WorldManager(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    // TODO: Arena regen with only loading/unloading chunks
    /*@EventHandler
    public void onUnload(ChunkUnloadEvent event) {
        for (AbstractArena value : arenaManager.getArenas().values()) {
            if (value.getWorld() == event.getWorld()) {
                if (!value.isEnabled()) {
                    return;
                }

                event.setSaveChunk(false);
                return;
            }
        }
    }*/
}
