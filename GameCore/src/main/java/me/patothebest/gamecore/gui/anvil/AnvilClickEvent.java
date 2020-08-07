package me.patothebest.gamecore.gui.anvil;

import org.bukkit.entity.Player;

public class AnvilClickEvent  {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final AnvilSlot slot;
    private final String name;
    private final String playerName;
    private boolean close;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //
    
    AnvilClickEvent(final AnvilSlot slot, final String name, final Player player) {
        super();
        this.close = false;
        this.slot = slot;
        this.name = name;
        this.playerName = player.getName();
    }

    // -------------------------------------------- //
    // GETTERS AND SETTERS
    // -------------------------------------------- //
    
    public String getPlayerName() {
        return this.playerName;
    }

    public AnvilSlot getSlot() {
        return this.slot;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean getWillClose() {
        return this.close;
    }
    
    public void setWillClose(final boolean close) {
        this.close = close;
    }
}
