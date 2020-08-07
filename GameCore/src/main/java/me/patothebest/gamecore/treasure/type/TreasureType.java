package me.patothebest.gamecore.treasure.type;

import java.util.List;

public enum TreasureType {

    NORMAL("normal", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBiMDY4NzA5NzkwZDQxYjg5MjdiODQyMmQyMWJiNTI0MDRiNTViNGNhMzUyY2RiN2M2OGU0YjM2NTkyNzIxIn19fQ=="),
    RARE("rare", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhjMWUxYzYyZGM2OTVlYjkwZmExOTJkYTZhY2E0OWFiNGY5ZGZmYjZhZGI1ZDI2MjllYmZjOWIyNzg4ZmEyIn19fQ=="),
    EPIC("epic", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzhmODhiMTYxNzYzZjYyZTRjNTFmNWViMWQzOGZhZjNiODJjNDhhODM5YWMzMTcxMjI5NTU3YWRlNDI3NDM0In19fQ=="),
    LEGENDARY("legendary", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGNjNzk5NTRkMzUwYTk4YzcyMTcyNTY4MzFmNjVjNjJhNDI4MDc0YjZlNGFlOWVlZGU3YTQ0ZjlkZTRhNyJ9fX0="),
    VOTE("vote", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19");

    private final String configPath;
    private final String url;
    private String name;
    private List<String> description;
    private int price;
    private boolean enabled;
    private boolean canBeBought;

    TreasureType(String configPath, String url) {
        this.configPath = configPath;
        this.url = url;
    }

    /**
     * Gets name
     *
     * @return rhe name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets price
     *
     * @return the price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Gets the skin url
     *
     * @return the skin url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the config path
     *
     * @return the config path
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * Sets the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description
     *
     * @return the description
     */
    public List<String> getDescription() {
        return description;
    }

    /**
     * Sets the description
     */
    public void setDescription(List<String> description) {
        this.description = description;
    }

    /**
     * Sets the price
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Gets the enabled
     *
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets if the chest can be bought or not
     *
     * @return if can be bought
     */
    public boolean canBeBought() {
        return canBeBought;
    }

    /**
     * Sets if the chest can be bought
     */
    public void setCanBeBought(boolean canBeBought) {
        this.canBeBought = canBeBought;
    }
}