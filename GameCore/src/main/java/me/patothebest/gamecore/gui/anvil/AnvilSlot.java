package me.patothebest.gamecore.gui.anvil;

public enum AnvilSlot {

    INPUT_LEFT(0),
    INPUT_RIGHT(1),
    OUTPUT(2);
    
    private final int slot;
    
    AnvilSlot(int slot) {
        this.slot = slot;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public static AnvilSlot bySlot(int slot) {
        for (AnvilSlot anvilSlot : values()) {
            if(anvilSlot.getSlot() == slot) {
                return anvilSlot;
            }
        }

        return null;
    }
}
