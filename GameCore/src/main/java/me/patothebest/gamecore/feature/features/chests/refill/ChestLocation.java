package me.patothebest.gamecore.feature.features.chests.refill;

public class ChestLocation {

    private final String folderName;

    public ChestLocation(String folderName) {
        this.folderName = folderName;
    }

    /**
     * Gets the folder name
     *
     * @return the folder name
     */
    public String getFolderName() {
        return folderName;
    }
}
