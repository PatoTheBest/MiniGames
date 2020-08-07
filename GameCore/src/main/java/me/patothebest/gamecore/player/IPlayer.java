package me.patothebest.gamecore.player;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.cosmetics.shop.ShopItem;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitLayout;
import me.patothebest.gamecore.lang.Locale;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.scoreboard.CustomScoreboard;
import me.patothebest.gamecore.scoreboard.ScoreboardType;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.TrackedStatistic;
import me.patothebest.gamecore.util.Callback;
import me.patothebest.gamecore.util.ObservablePlayer;
import me.patothebest.gamecore.util.SerializableObject;
import me.patothebest.gamecore.util.Sounds;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * The Interface IPlayer.
 *
 */
public interface IPlayer extends SerializableObject, ObservablePlayer {

    /**
     * Prepares a player, this is where the PlayerLoadEvent
     * is called.
     */
    void loginPrepare();

    /**
     * Prepares a player, this is where the PlayerLoadEvent
     * is called.
     */
    void joinPrepare();

    /**
     * Executes a callback when the player is fully loaded.
     * <p>
     * If the player is not loaded completely, it will add the callback
     * to a queue and execute it when the player loads. If the player is
     * already loaded, it will be executed immediately.
     *
     * @param callback the callback
     */
    void executeWhenFullyLoaded(Callback<IPlayer> callback);

    /**
     * @return true if the player has fully joined the server and isn't on
     * the "login" state
     */
    boolean isFullyJoined();

    /**
     * @return true if the player has all the data loaded. Some data may be
     * loaded a bit late like stats as it's not crucial information
     */
    boolean isAllDataLoaded();

    /**
     * The method's only use right now is to reinitialize
     * the scoreboard after a plugin reload.
     */
    void reinitializeAfterReload();

    /**
     * Destroy's the player information, scoreboard, kits from the cache.
     */
    void destroy();

    /**
     * Gets the locale.
     *
     * @return the player's locale
     */
    Locale getLocale();

    /**
     * Sets the player's locale.
     *
     * @param locale the locale to set
     */
    void setLocale(Locale locale);

    /**
     * Gets the current {@link AbstractArena} the player is in.
     *
     * @return null if the player is not in an arena,
     * otherwise returns the arena the player is in
     */
    @Nullable
    AbstractArena getCurrentArena();

    /**
     * Set's the {@link AbstractArena} the player is in.
     *
     * @param currentArena the arena the player is in
     */
    void setCurrentArena(AbstractArena currentArena);

    /**
     * Gets the {@link AbstractGameTeam}.
     *
     * @return the team the player is on
     */
    @Nullable
    AbstractGameTeam getGameTeam();

    /**
     * Sets the {@link AbstractGameTeam} the player is in.
     *
     * @param gameTeam the game team the player is in
     */
    void setGameTeam(AbstractGameTeam gameTeam);

    /**
     * Checks if the player is currently in an arena.
     *
     * @return true if the player is in an arena
     */
    boolean isInArena();

    /**
     * Gets the game kills.
     *
     * @return the amount of kills a player has in a game
     */
    int getGameKills();

    /**
     * Gets the game deaths.
     *
     * @return the amount of deaths a player has in a game
     */
    int getGameDeaths();

    /**
     * Gets the {@link PlayerInventory}.
     *
     * @return the player inventory
     */
    PlayerInventory getPlayerInventory();

    /**
     * Gets the {@link Kit} the player has.
     *
     * @return the kit the player has
     */
    Kit getKit();

    /**
     * Gets the {@link Player}.
     *
     * @return the bukkit player
     */
    Player getPlayer();

    /**
     * Checks if the player is playing.
     *
     * @return true, if the player is playing
     */
    boolean isPlaying();

    /**
     * Sets the {@link Kit} the player is using.
     *
     * @param kit the kit the player is/will use
     */
    void setKit(Kit kit);

    /**
     * Gets the player id (for mysql).
     *
     * @return the player id
     */
    int getPlayerId();

    /**
     * Sets the player id (for mysql).
     *
     * @param playerId the new player id
     */
    void setPlayerId(int playerId);

    /**
     * Shows a specific scoreboard type to the player.
     *
     * @param scoreboardToShow the scoreboard to show
     */
    void show(ScoreboardType scoreboardToShow);

    /**
     * Destroy the scoreboard type.
     *
     * @param scoreboardToShow the scoreboard to show
     */
    void destroy(ScoreboardType scoreboardToShow);

    /**
     * Gets the player scoreboards.
     *
     * @return the scoreboards
     */
    Map<ScoreboardType, CustomScoreboard> getScoreboards();

    /**
     * Gets the scoreboard to show.
     *
     * @return the scoreboard to show
     */
    ScoreboardType getScoreboardToShow();

    /**
     * Sets the scoreboard to show.
     *
     * @param scoreboardToShow the new scoreboard to show
     */
    void setScoreboardToShow(ScoreboardType scoreboardToShow);

    /**
     * Sends a message to the player.
     *
     * @param message the message
     */
    void sendMessage(String message);

    /**
     * Sends a localized message to the player
     *
     * @param lang the localized message
     */
    default void sendMessage(ILang lang) {
        lang.sendMessage(this);
    }

    /**
     * Checks if a player can use the specified kit.
     *
     * @param kit the kit to use
     * @return true if the player can use it
     */
    boolean canUseKit(Kit kit);

    /**
     * Consumes a use on the kit (only if the kit is not
     * permanent, aka -1).
     *
     * @param kit the kit to use
     * @return true if it was successful
     */
    boolean useKit(Kit kit);

    /**
     * Removes a set amount of kit uses
     *
     * @param kit the kit
     * @param amount the amount of items to remove
     */
    boolean removeKit(Kit kit, int amount);

    /**
     * Removes a kit completely
     *
     * @param kit the kit
     */
    boolean removeKit(Kit kit);

    /**
     * This returns an {@link java.util.Collections.UnmodifiableMap} of {@link Kit}
     * and {@link Integer} which is the amount. Not all the kits are in this maps,
     * only the kits that have uses remaining or the kits that had previous
     * uses but ran out. This map cannot be modified, if you wish to modify
     * any value, you must use {@link IPlayer#addKitUses(Kit, int)}}
     *
     * @return kit uses the player has
     */
    Map<Kit, Integer> getKitUses();

    /**
     * This returns true if the kit is a permanent kit or not. The way this checks
     * is by checking its uses remaining is equal to -1
     *
     * @param kit the kit
     * @return true if kit is permanent
     */
    default boolean isPermanentKit(Kit kit) {
        return getKitUses().containsKey(kit) && getKitUses().get(kit) == -1;
    }

    /**
     * Buys a specific kit permanently.
     *
     * @param kit the kit
     */
    void buyPermanentKit(Kit kit);

    /**
     * Buys a single kit use for the specified kit.
     *
     * @param kit the kit
     */
    void buyKitSingleKitUse(Kit kit);

    /**
     * Buys kit uses for the specified kit.
     *
     * @param kit the kit
     * @param amount the kit uses to buy
     */
    void addKitUses(Kit kit, int amount);

    /**
     * Sets kit uses for the specified kit.
     *
     * @param kit the kit
     * @param amount the kit uses
     */
    void setKitUses(Kit kit, int amount);

    /**
     * Modifies a kit layout for the player
     *
     * @param kit the kit
     * @param kitLayout the layout
     */
    void modifyKitLayout(Kit kit, KitLayout kitLayout);

    /**
     * Returns a layout for the specified kit. If there is no
     * custom layout, null will be returned
     *
     * @param kit the lot
     * @return the layout if any, null if there's no custom layout
     */
    KitLayout getLayout(Kit kit);

    /**
     * Gets all the custom layouts the player has
     *
     * @return the layouts map
     */
    Map<Kit, KitLayout> getKitLayouts();

    /**
     * Gets the owned shop items the player has
     *
     * @param shopItemClass the item class
     * @param <Type> the shop item
     * @return a list of all the shop items the player owns
     * of the specified type
     */
    <Type extends ShopItem> Map<Type, Integer> getShopItems(Class<Type> shopItemClass);

    /**
     * Checks whether or not the item is in the map
     * <p>
     * This shouldn't be used normally, more for internal use
     *
     * @param shopItem the shop item item
     * @param <Type> the shop item type
     * @return true if the item is in the map
     */
    default <Type extends ShopItem> boolean isItemInMap(Type shopItem) {
        return getShopItems(shopItem.getClass()).containsKey(shopItem);
    }

    /**
     * Gets the remaining uses of that shop item
     *
     * @param shopItem the shop item item
     * @param <Type> the shop item type
     * @return the amount of uses remaining
     */
    default <Type extends ShopItem> int getRemainingUses(Type shopItem) {
        return getShopItems(shopItem.getClass()).getOrDefault(shopItem, 0);
    }

    /**
     * Checks whether or not a player has the amount of that item
     *
     * @param shopItem the shop item item
     * @param amount the amount of minimum uses to have
     * @param <Type> the shop item type
     * @return true if the player has enough of that item
     */
    default <Type extends ShopItem> boolean hasEnough(Type shopItem, int amount) {
        return isItemInMap(shopItem) && (isPermanent(shopItem) || getRemainingUses(shopItem) >= amount);
    }

    /**
     * Checks whether or not a player can use that item
     *
     * @param shopItem the shop item item
     * @param <Type> the shop item type
     * @return true if the player can use the item
     */
    default <Type extends ShopItem> boolean canUse(Type shopItem) {
        return hasEnough(shopItem, 1);
    }

    /**
     * Checks whether or not the player has unlocked the item permanently
     *
     * @param shopItem the shop item item
     * @param <Type> the shop item type
     * @return true if the player has the item permanently
     */
    default <Type extends ShopItem> boolean isPermanent(Type shopItem) {
        return getRemainingUses(shopItem) == -1;
    }

    /**
     * Buys the specified item permanently
     *
     * @param shopItem the shop item
     * @param <Type> the item type
     */
    default <Type extends ShopItem> void buyItemPermanently(Type shopItem) {
        buyItemUses(shopItem, -1);
    }

    /**
     * Buys the specified uses for the item
     *
     * @param shopItem the shop item
     * @param <Type> the item type
     */
    <Type extends ShopItem> void buyItemUses(Type shopItem, int uses);

    /**
     * Checks whether or not the item is currently selected
     *
     * @param shopItem the shop item item
     * @param <Type> the shop item type
     * @return true if the player has the item permanently
     */
    default <Type extends ShopItem> boolean isSelected(Type shopItem) {
        return getSelectedItem(shopItem.getClass()) == shopItem;
    }

    /**
     * Uses an item
     *
     * @param shopItem the shop item to use
     * @param <Type> the shop item type
     */
    <Type extends ShopItem> boolean useItem(Type shopItem);

    /**
     * Removes an item set amount
     *
     * @param shopItem the shop item to use
     * @param <Type> the shop item type
     * @param amount the amount of items to use
     */
    <Type extends ShopItem> boolean removeItem(Type shopItem, int amount);

    /**
     * Removes an item completely
     *
     * @param shopItem the shop item to use
     * @param <Type> the shop item type
     */
    <Type extends ShopItem> boolean removeItem(Type shopItem);

    /**
     * Set the item amount
     *
     * @param shopItem the shop item to use
     * @param <Type> the shop item type
     * @param amount the amount of items to use
     */
    <Type extends ShopItem> boolean setItemAmount(Type shopItem, int amount);

    /**
     * Gets the selected shop item for the shop type
     * <p>
     * This is to have persistent items, have the players be able
     * to select the items in the lobby and switch to the other server
     * while having the last item selected
     *
     * @param shopItemClass the shop item class
     * @param <Type> the shop item type
     * @return the selected item, null if nothing is selected
     */
    @Nullable
    <Type extends ShopItem> Type getSelectedItem(Class<Type> shopItemClass);

    /**
     * Gets the selected shop item for the shop type
     * <p>
     * This is to have persistent items, have the players be able
     * to select the items in the lobby and switch to the other server
     * while having the last item selected
     *
     * @param shopItemClass the shop item class
     * @param <Type> the shop item type
     * @return the selected item, null if nothing is selected
     */
    default <Type extends ShopItem> Type getSelectedItemOrDefault(Class<Type> shopItemClass, Type defaultObject) {
        return getSelectedItem(shopItemClass) == null ? defaultObject : getSelectedItem(shopItemClass);
    }

    /**
     * Selects the item
     * <p>
     * This selection is persistent, so if a player logs off
     * and logs back on, the item will continue to be selected
     *
     * @param shopItem the shop item to select
     * @param <Type> the shop item type
     */
    <Type extends ShopItem> void selectItem(Type shopItem);

    /**
     * Checks whether or not a player can use thae selected item
     *
     * @param shopItemClass the shop item class
     * @param <Type> the shop item type
     * @return true if the player can use the item
     */
    default <Type extends ShopItem> boolean canUseSelectedItem(Class<Type> shopItemClass) {
        return getSelectedItem(shopItemClass) != null && canUse(getSelectedItem(shopItemClass));
    }

    /**
     * Deselects a selected item
     *
     * @param shopItem the shop item to deselect
     * @param <Type> the shop item type
     */
    <Type extends ShopItem> void deSelectItem(Type shopItem);

    /**
     * Deselects a selected item
     *
     * @param shopItemClass the class to deselect
     * @param <Type> the shop item type
     */
    default <Type extends ShopItem> void deSelectItem(Class<Type> shopItemClass) {
        deSelectItem(getSelectedItem(shopItemClass));
    }

    /**
     * Gets the player location
     *
     * @return the player location
     */
    Location getLocation();

    /**
     * Gets the treasure keys map
     *
     * @return the treasure keys map
     */
    Map<TreasureType, Integer> getTreasureKeyMap();

    /**
     * Gets the amount of keys the player has of that specific
     * treasure chest type
     *
     * @param treasureType the treasure chest type
     * @return the amount of keys
     */
    int getKeys(TreasureType treasureType);

    /**
     * Set the amount of keys for the specified treasure type
     *
     * @param treasureType the treasure type
     * @param amount the amount of keys
     */
    void setKeys(TreasureType treasureType, int amount);

    /**
     * Gets the money the player has
     * <p>
     * In case vault is not present or vault is present but
     * no economy plugin was found, meaning the economy provider
     * returns null, the player will display an amount of 0
     *
     * @return the amount of money the player has
     */
    double getMoney();

    /**
     * Gives the player money
     * <p>
     * This will return false if there's no economy plugin present
     * or if the transaction was a failure for any reason. If it is
     * successful, it will send the player a notification in chat
     *
     * @param amount the amount to give
     * @return true if successful
     */
    boolean giveMoney(double amount);

    /**
     * Gets the points from the player
     *
     * @return the points
     */
    int getPoints();

    /**
     * Sets the player points
     *
     * @param newPoints the new points
     */
    void setPoints(int newPoints);

    /**
     * Gets the statistics map
     * <p>
     * The map contains the statistics the player has all time and
     * of this month. They are separated into weekly stats from this
     * month.
     * <p>
     * A new player will have this map empty, since there will be no
     * statistic in the database. This is done on purpose, so that the
     * database doesn't get filled up with a lot of records. Only stats
     * that have a value of 1 or more will get saved into the database,
     * so if a player joins a game, kills a player and wins, the only
     * stats that will save in the database are kills and wins, since the
     * payer has 0 loses and 0 deaths, this is of course if the individual
     * statistic is enabled in the configuration.
     *
     * @return the statistics map
     */
    Map<Class<? extends Statistic>, TrackedStatistic> getStatistics();

    /**
     * Gets the player name
     *
     * @return the player name
     */
    String getName();

    /**
     * Sets the player's identity
     *
     * @param playerIdentity the player's identity
     */
    void setPlayerIdentity(PlayerIdentity playerIdentity);

    /**
     * Returns a unique and persistent id for this player
     *
     * @return unique id
     */
    UUID getUniqueId();

    /**
     * Gets the join location of the player
     *
     * @return the join location
     */
    @Nullable Location getJoinLocation();

    /**
     * Sets the join location for the player
     *
     * @param joinLocation the location
     */
    void setJoinLocation(Location joinLocation);

    /**
     * Play a sound for a player at their location
     * <p>
     * The sound will have a pitch of 1f and the max volume
     * which is of 10f
     *
     * @param sound the sound to play for the player
     */
    void playSound(Sounds sound);

    /**
     * Play a sound for a player at their location
     *
     * @param sound the sound to play for the player
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(Sounds sound, float volume, float pitch);

    /**
     * Gets the current world this player is in
     *
     * @return World
     */
    World getWorld();

    /**
     * Gets the player entity id in the world
     *
     * @return the entity id of the player
     */
    int getEntityId();
}
