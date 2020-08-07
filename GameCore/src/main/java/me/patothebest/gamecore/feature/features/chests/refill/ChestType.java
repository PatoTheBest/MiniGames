package me.patothebest.gamecore.feature.features.chests.refill;

public enum ChestType {

    BASIC("basic"),
    NORMAL("normal"),
    OP("op")

    ;

    private final String fileName;

    ChestType(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the file name
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }
}
